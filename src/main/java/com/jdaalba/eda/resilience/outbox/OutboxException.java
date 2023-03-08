package com.jdaalba.eda.resilience.outbox;

public class OutboxException extends RuntimeException {

    public OutboxException(Exception cause) {
        super(cause);
    }
}
