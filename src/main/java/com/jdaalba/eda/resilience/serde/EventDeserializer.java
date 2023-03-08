package com.jdaalba.eda.resilience.serde;

import com.jdaalba.eda.resilience.messaging.Event;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

public class EventDeserializer implements Deserializer<Event> {

    @Override
    public Event deserialize(String topic, byte[] data) {
        return null;
    }

    @Override
    public Event deserialize(String topic, Headers headers, byte[] data) {
        return JsonMapper.asEvent(data, new String(headers.headers("_class").iterator().next().value()));
    }
}
