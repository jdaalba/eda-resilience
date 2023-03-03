package com.jdaalba.eda.resilience;


import com.jdaalba.eda.resilience.serde.JsonSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

import static java.util.Objects.isNull;

public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        var bootstrapServers = "127.0.0.1:29092";
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerde.JsonSerializer.class.getName());

        var producer = new KafkaProducer<String, Message>(properties);
        var record = new ProducerRecord<>("other", UUID.randomUUID().toString(), new Message("Me", "Myself", "Hello you"));

        producer.send(
                record,
                (RecordMetadata metadata, Exception e) -> {
                    if (isNull(e)) {
                        log.info("Success sending event");
                        log.info("Metadata:");
                        log.info("Topic: {}", metadata.topic());
                        log.info("Partition: {}", metadata.partition());
                        log.info("Offset: {}", metadata.offset());
                        log.info("Timestamp: {}", metadata.timestamp());
                    } else {
                        log.error("Error sending message");
                        // todo: handle error and close circuit
                    }
                }
        );

        producer.flush();
        producer.close();
    }
}
