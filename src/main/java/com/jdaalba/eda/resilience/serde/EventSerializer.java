package com.jdaalba.eda.resilience.serde;

import com.jdaalba.eda.resilience.messaging.Event;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.Serializer;

public class EventSerializer implements Serializer<Event> {

    @Override
    public byte[] serialize(String topic, Event event) {
        return JsonMapper.asBytes(event);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Event data) {
        headers.add(new RecordHeader("_class", data.getClass().getName().getBytes()));
        return Serializer.super.serialize(topic, headers, data);
    }
}