package com.cloudant.nouveau.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;

public class BytesRefSerializerTest {

    @Test
    public void testSerializeText() throws Exception {
        final BytesRef bytesRef = new BytesRef("foo");
        assertThat(mapper().writeValueAsString(bytesRef)).isEqualTo("\"Zm9v\"");
    }

    @Test
    public void testSerializeBinary() throws Exception {
        final BytesRef bytesRef = new BytesRef(new byte[]{1, 2, 3, 4});
        assertThat(mapper().writeValueAsString(bytesRef)).isEqualTo("\"AQIDBA==\"");
    }

    private ObjectMapper mapper() {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(BytesRef.class, new BytesRefSerializer());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper;
    }

}
