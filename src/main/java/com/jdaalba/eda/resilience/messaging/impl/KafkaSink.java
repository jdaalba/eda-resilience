package com.jdaalba.eda.resilience.messaging.impl;

import com.jdaalba.eda.resilience.exception.MessagingException;
import com.jdaalba.eda.resilience.messaging.Event;
import com.jdaalba.eda.resilience.messaging.Sink;
import com.jdaalba.eda.resilience.serde.EventSerializer;
import com.jdaalba.eda.resilience.vo.OutputEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KafkaSink implements Sink {

    private static final Logger log = LoggerFactory.getLogger(KafkaSink.class);

    private final KafkaProducer<String, Event> producer;

    public KafkaSink() {
        var bootstrapServers = "127.0.0.1:29092";
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

        producer = new KafkaProducer<>(properties);
    }

    @Override
    public void send(OutputEvent event) {
        log.info("Sending {}", event);
        var pr = new ProducerRecord<>(event.destination(), UUID.randomUUID().toString(), event.event());
        try {
            producer.send(pr).get();
            producer.flush();
        } catch (InterruptedException | ExecutionException e) {
            throw new MessagingException(e);
        }
    }
}
