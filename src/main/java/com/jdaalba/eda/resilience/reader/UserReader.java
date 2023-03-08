package com.jdaalba.eda.resilience.reader;

import com.jdaalba.eda.resilience.event.UserRegistered;
import com.jdaalba.eda.resilience.exception.MessagingException;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.stream.Stream;

public class UserReader {

    private static final Logger log = LoggerFactory.getLogger(UserReader.class);

    public Stream<UserRegistered> read() {
        try (
                var reader = Files.newBufferedReader(Paths.get("src/main/resources/users.csv"));
                var records = CSVFormat.Builder.create().setHeader("id", "first_name", "last_name", "email", "gender")
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            var builder = Stream.<UserRegistered>builder();
            for (var record : records) {
                var event = new UserRegistered(
                        Long.parseLong(record.get("id")),
                        record.get("first_name"),
                        record.get("last_name"),
                        record.get("email"),
                        record.get("gender"),
                        Instant.now()
                );
                log.info("Event: {}", event);
                builder.add(event);
            }
            return builder.build();
        } catch (IOException e) {
            log.error("Error reading CSV: ", e);
            throw new MessagingException(e);
        }
    }
}
