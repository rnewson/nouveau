package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.util.BytesRef;

public class BytesRefSerializer extends StdSerializer<BytesRef> {

    public BytesRefSerializer() {
        this(null);
    }

    public BytesRefSerializer(Class<BytesRef> vc) {
        super(vc);
    }

    @Override
    public void serialize(BytesRef bytesRef, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeBinary(bytesRef.bytes, bytesRef.offset, bytesRef.length);
    }

    @Override
    public void serializeWithType(BytesRef bytesRef, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        typeSer.writeTypePrefixForObject(bytesRef, gen);
        gen.writeFieldName("value");
        gen.writeBinary(bytesRef.bytes, bytesRef.offset, bytesRef.length);
        typeSer.writeTypeSuffixForObject(bytesRef, gen);
    }

}

