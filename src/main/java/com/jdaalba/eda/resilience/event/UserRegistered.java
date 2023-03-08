package com.jdaalba.eda.resilience.event;

import com.jdaalba.eda.resilience.messaging.Event;

import java.time.Instant;

public record UserRegistered(
        long id, String firstName, String lastName, String email, String gender, Instant emittedOn
) implements Event {
}
