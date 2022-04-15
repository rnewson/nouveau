package com.cloudant.nouveau.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.apache.lucene.util.BytesRef;

public class BytesRefSerializer extends JsonSerializer<BytesRef> {

	@Override
	public void serialize(BytesRef bytesRef, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
	    jsonGenerator.writeString(bytesRef.utf8ToString());
	}

}
