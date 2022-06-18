package com.cloudant.nouveau.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.junit.jupiter.api.Test;

public class EncryptedDirectoryTest {

    private final Random random = new SecureRandom();

    @Test
    public void testOutput() throws Exception {
        try (final Directory d1 = new ByteBuffersDirectory()) {
            Directory d2 = new EncryptedDirectory(key(), d1);

            final IndexOutput out = d2.createOutput("foo", null);
            final byte[] expected = new byte[512];
            out.writeBytes(expected, 0, expected.length);
            out.close();

            final IndexInput in = d2.openInput("foo", null);
            final byte[] actual = new byte[expected.length];
            in.readBytes(actual, 0, actual.length);
            assertArrayEquals(expected, actual);

            in.seek(0);
            in.readBytes(actual, 0, actual.length);
            assertArrayEquals(expected, actual);

            in.seek(100);
            in.readBytes(actual, 0, actual.length - 100);
            assertArrayEquals(expected, actual);
        }
    }

    private SecretKey key() {
        final byte[] key = new byte[32];
        random.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }

}
