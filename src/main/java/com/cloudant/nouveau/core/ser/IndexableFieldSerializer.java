package com.cloudant.nouveau.core.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.XYPointField;
import org.apache.lucene.geo.GeoEncodingUtils;
import org.apache.lucene.geo.XYEncodingUtils;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

class IndexableFieldSerializer extends StdSerializer<IndexableField> {

    public IndexableFieldSerializer() {
        this(null);
    }

    public IndexableFieldSerializer(Class<IndexableField> vc) {
        super(vc);
    }

    @Override
    public void serialize(final IndexableField field, final JsonGenerator gen, final SerializerProvider provider)
            throws IOException {
        final SupportedType type = supportedType(field);
        gen.writeStartObject();
        gen.writeStringField("@type", type.toString());
        gen.writeStringField("name", field.name());
        switch (type) {
            case double_dv:
            case double_point:
            case stored_double:
                gen.writeNumberField("value", field.numericValue().doubleValue());
                break;
            case float_dv:
            case float_point:
                gen.writeNumberField("value", field.numericValue().floatValue());
                break;
            case int_point:
                gen.writeNumberField("value", field.numericValue().intValue());
                break;
            case latlon_dv:
            case latlon_point: {
                final long value = (Long) field.numericValue();
                gen.writeNumberField("lat", GeoEncodingUtils.decodeLatitude((int) (value >> 32)));
                gen.writeNumberField("lon", GeoEncodingUtils.decodeLongitude((int) (value & 0xFFFFFFFF)));
                break;
            }
            case long_point:
            case sorted_numeric_dv:
                gen.writeNumberField("value", field.numericValue().longValue());
                break;
            case binary_dv:
            case sorted_dv:
            case sorted_set_dv:
            case stored_binary: {
                final BytesRef bytesRef = field.binaryValue();
                gen.writeFieldName("value");
                gen.writeBinary(bytesRef.bytes, bytesRef.offset, bytesRef.length);
                break;
            }
            case stored_string:
                gen.writeStringField("value", field.stringValue());
                break;
            case string:
            case text:
                gen.writeStringField("value", field.stringValue());
                gen.writeBooleanField("stored", field.fieldType().stored());
                break;
            case xy_dv:
            case xy_point: {
                final BytesRef bytesRef = field.binaryValue();
                gen.writeNumberField("x", XYEncodingUtils.decode(bytesRef.bytes, 0));
                gen.writeNumberField("y", XYEncodingUtils.decode(bytesRef.bytes, Integer.BYTES));
                break;
            }
        }
        gen.writeEndObject();
    }

    private SupportedType supportedType(final IndexableField field) {
        if (field instanceof DoubleDocValuesField) {
            return SupportedType.double_dv;
        }
        if (field instanceof DoublePoint) {
            return SupportedType.double_point;
        }
        if (field instanceof FloatPoint) {
            return SupportedType.float_point;
        }
        if (field instanceof IntPoint) {
            return SupportedType.int_point;
        }
        if (field instanceof LongPoint) {
            return SupportedType.long_point;
        }
        if (field instanceof SortedDocValuesField) {
            return SupportedType.sorted_dv;
        }
        if (field instanceof SortedSetDocValuesField) {
            return SupportedType.sorted_set_dv;
        }
        if (field instanceof StoredField) {
            final StoredField storedField = (StoredField) field;
            if (storedField.numericValue() != null) {
                return SupportedType.stored_double;
            } else if (storedField.stringValue() != null) {
                return SupportedType.stored_string;
            } else if (storedField.binaryValue() != null) {
                return SupportedType.stored_binary;
            }
        }
        if (field instanceof StringField) {
            return SupportedType.string;
        }
        if (field instanceof TextField) {
            return SupportedType.text;
        }
        if (field instanceof XYPointField) {
            return SupportedType.xy_point;
        }

        throw new IllegalArgumentException(field + " is not a supported type");
    }

}
