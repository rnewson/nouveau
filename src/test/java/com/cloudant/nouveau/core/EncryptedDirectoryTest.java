package com.cloudant.nouveau.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.junit.jupiter.api.Test;

public class EncryptedDirectoryTest {

    private final Random random = new SecureRandom();

    @Test
    public void testBasics() throws Exception {
        try (final Directory d = new EncryptedDirectory(key(), new ByteBuffersDirectory())) {

            final IndexOutput out = d.createOutput("foo", null);
            final byte[] expected = new byte[512];
            out.writeBytes(expected, 0, expected.length);
            out.close();

            final IndexInput in = d.openInput("foo", null);
            final byte[] actual = new byte[expected.length];
            in.readBytes(actual, 0, actual.length);
            assertArrayEquals(expected, actual);

            for (int i=0; i < expected.length - 1; i++) {
                for (int j=1 ; j < (actual.length - i) / 2; j *= 2) {
                    in.seek(i);
                    in.readBytes(actual, 0, j);
                    assertArrayEquals(expected, actual);
                }
            }
        }
    }

    @Test
    public void testIndexing() throws Exception {
        final Document doc = new Document();
        for (int i=0; i<10; i++) {
            doc.add(new TextField("foo", "hello, here is some text for the test.", Store.YES));
        }
        final Directory dir = new EncryptedDirectory(key(), new ByteBuffersDirectory());
        final IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig());
        writer.addDocument(doc);
        writer.commit();
        writer.close();
    }

    private SecretKey key() {
        final byte[] key = new byte[32];
        random.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }

}
