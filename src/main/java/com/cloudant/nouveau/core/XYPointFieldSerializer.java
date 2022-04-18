package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.XYPointField;
import org.apache.lucene.geo.XYEncodingUtils;
import org.apache.lucene.util.BytesRef;

public class XYPointFieldSerializer extends StdSerializer<XYPointField> {

    public XYPointFieldSerializer() {
        this(null);
    }

    public XYPointFieldSerializer(Class<XYPointField> vc) {
        super(vc);
    }

    @Override
    public void serialize(XYPointField field, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "xy");
        gen.writeStringField("name", field.name());
        final BytesRef bytesRef = field.binaryValue();
        gen.writeNumberField("x", XYEncodingUtils.decode(bytesRef.bytes, 0));
        gen.writeNumberField("y", XYEncodingUtils.decode(bytesRef.bytes, Integer.BYTES));
        gen.writeEndObject();
    }

}
