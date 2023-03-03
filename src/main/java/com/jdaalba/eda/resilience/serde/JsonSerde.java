package com.jdaalba.eda.resilience.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class JsonSerde implements Serde<Object> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static class JsonSerializer implements Serializer<Object> {
        @Override
        public byte[] serialize(String topic, Object data) {
            try {
                return MAPPER.writeValueAsBytes(data);
            } catch (IOException cause) {
                throw new SerdeException(cause);
            }
        }

        @Override
        public byte[] serialize(String topic, Headers headers, Object data) {
            headers.add(new RecordHeader("_class", data.getClass().getName().getBytes()));
            return Serializer.super.serialize(topic, headers, data);
        }
    }
    @Override
    public Serializer<Object> serializer() {
        return new JsonSerializer();
    }

    public static class JsonDeserializer implements Deserializer<Object> {

        @Override
        public Object deserialize(String topic, byte[] data) {
            return null;
        }

        @Override
        public Object deserialize(String topic, Headers headers, byte[] data) {
            try {
                var clazz = Class.forName(new String(headers.headers("_class").iterator().next().value()));
                return MAPPER.readValue(data, clazz);
            } catch (ClassNotFoundException | IOException cause) {
                throw new SerdeException(cause);
            }
        }
    }

    @Override
    public Deserializer<Object> deserializer() {
        return new JsonDeserializer();
    }
}
