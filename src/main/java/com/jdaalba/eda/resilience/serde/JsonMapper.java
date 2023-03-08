package com.jdaalba.eda.resilience.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jdaalba.eda.resilience.messaging.Event;

import java.io.IOException;

public final class JsonMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private JsonMapper() {
    }

    public static byte[] asBytes(Event event) {
        try {
            return MAPPER.writeValueAsBytes(event);
        } catch (IOException cause) {
            throw new SerdeException(cause);
        }
    }

    public static <T extends Event>  T asEvent(byte[] payload, String className) {
        try {
            return MAPPER.readValue(payload, getClass(className));
        } catch (ClassNotFoundException | IOException cause) {
            throw new SerdeException(cause);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> Class<T> getClass(String className) throws ClassNotFoundException {
        return (Class<T>) Class.forName(className);
    }
}
