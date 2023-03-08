package com.jdaalba.eda.resilience.vo;

import com.jdaalba.eda.resilience.messaging.Event;

public record OutputEvent(Event event, String destination) {
}
