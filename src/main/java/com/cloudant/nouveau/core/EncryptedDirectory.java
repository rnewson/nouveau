package com.cloudant.nouveau.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FilterDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

public final class EncryptedDirectory extends FilterDirectory {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Key kek;

    public EncryptedDirectory(final Key kek, final Directory in) {
        super(in);
        if (!"AES".equals(kek.getAlgorithm())) {
            throw new IllegalArgumentException("Key must be AES");
        }
        if (32 != kek.getEncoded().length) {
            throw new IllegalArgumentException("Key must be 256 bits");
        }
        this.kek = kek;
    }

    public IndexOutput createOutput​(String name, IOContext context) throws IOException {
        return new EncryptedIndexOutput(kek, in.createOutput(name, context));
    }

    public IndexOutput createTempOutput​(String prefix, String suffix, IOContext context) throws IOException {
        return new EncryptedIndexOutput(kek, in.createTempOutput(prefix, suffix, context));
    }

    public IndexInput openInput​(String name, IOContext context) throws IOException {
        return new EncryptedIndexInput(kek, in.openInput(name, context));
    }

    private class EncryptedIndexInput extends IndexInput {

        private final IndexInput in;
        private final int headerLen;
        private final Cipher c;
        private final Key dek;
        private final byte[] buf = new byte[16];

        private EncryptedIndexInput(final Key kek, final IndexInput in) throws IOException {
            super("EncryptedIndexInput");
            this.in = in;
            try {
                byte wekLen = in.readByte();
                this.headerLen = 1 + wekLen;
                final byte[] wekBytes = new byte[wekLen];
                in.readBytes(wekBytes, 0, wekLen);
                final Cipher wrap = Cipher.getInstance("AESWrap");
                wrap.init(Cipher.UNWRAP_MODE, kek);
                this.dek = wrap.unwrap(wekBytes, "AES", Cipher.SECRET_KEY);

                c = Cipher.getInstance("AES/CTR/NoPadding");
                c.init(Cipher.DECRYPT_MODE, dek, new IvParameterSpec(new byte[16]));
            } catch (GeneralSecurityException e) {
                throw convertGeneralSecurityException(e);
            }
        }

        @Override
        public void close() throws IOException {
            in.close();
        }

        @Override
        public long getFilePointer() {
            return in.getFilePointer() - headerLen;
        }

        @Override
        public void seek(final long pos) throws IOException {
            in.seek(pos + headerLen);
            final long block = pos / 16L;
            final byte[] ctr = new byte[16];
            ctr[8] = (byte) (0xff & (block >> 56));
            ctr[9] = (byte) (0xff & (block >> 48));
            ctr[10] = (byte) (0xff & (block >> 40));
            ctr[11] = (byte) (0xff & (block >> 32));
            ctr[12] = (byte) (0xff & (block >> 24));
            ctr[13] = (byte) (0xff & (block >> 16));
            ctr[14] = (byte) (0xff & (block >> 8));
            ctr[15] = (byte) (0xff & block);
            try {
                c.init(Cipher.DECRYPT_MODE, dek, new IvParameterSpec(ctr));
                c.update(buf, 0, (int) pos % 16, buf, 0);
            } catch (GeneralSecurityException e) {
                throw convertGeneralSecurityException(e);
            }
        }

        @Override
        public long length() {
            return in.length();
        }

        @Override
        public IndexInput slice(String sliceDescription, long offset, long length) throws IOException {
            return null;
        }

        @Override
        public byte readByte() throws IOException {
            readBytes(buf, 0, 1);
            return buf[0];
        }

        @Override
        public void readBytes(byte[] b, int offset, int len) throws IOException {
            in.readBytes(b, offset, len);
            try {
                c.update(b, offset, len, b, offset);
            } catch (GeneralSecurityException e) {
                throw convertGeneralSecurityException(e);
            }
        }

    }

    private class EncryptedIndexOutput extends IndexOutput {

        private final IndexOutput out;
        private final Cipher c;
        private final int headerLen;
        private final CRC32 crc = new CRC32();
        private final byte[] buf = new byte[1024];
        private boolean closed = false;

        private EncryptedIndexOutput(final Key kek, final IndexOutput out) throws IOException {
            super("EncryptedIndexOutput(path=\"" + out.getName() + "\")", out.getName());
            this.out = out;
            try {
                final byte[] keyBytes = new byte[32];
                RANDOM.nextBytes(keyBytes);
                final Key dek = new SecretKeySpec(keyBytes, "AES");

                final Cipher wrap = Cipher.getInstance("AESWrap");
                wrap.init(Cipher.WRAP_MODE, kek);
                final byte[] wek = wrap.wrap(dek);
                out.writeByte((byte)wek.length);
                out.writeBytes(wek, wek.length);
                this.headerLen = 1 + wek.length;

                c = Cipher.getInstance("AES/CTR/NoPadding");
                c.init(Cipher.ENCRYPT_MODE, dek, new IvParameterSpec(new byte[16]));
            } catch (GeneralSecurityException e) {
                throw convertGeneralSecurityException(e);
            }
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                CodecUtil.writeFooter(out);
                out.close();
            }
        }

        @Override
        public long getFilePointer() {
            return out.getFilePointer() - headerLen;
        }

        @Override
        public long getChecksum() throws IOException {
            return crc.getValue();
        }

        @Override
        public void writeByte(byte b) throws IOException {
            buf[0] = b;
            writeBytes(buf, 0, 1);
        }

        @Override
        public void writeBytes(byte[] b, int offset, int length) throws IOException {
            crc.update(b, offset, length);
            byte[] buf = this.buf;
            if (length > this.buf.length) {
                buf = new byte[length];
            }
            try {
                c.update(b, offset, length, buf, 0);
            } catch (final ShortBufferException e) {
                throw convertGeneralSecurityException(e);
            }
            out.writeBytes(buf, 0, length);
        }

    }

    private IOException convertGeneralSecurityException(final GeneralSecurityException e) {
        return new IOException(e);
    }

}
