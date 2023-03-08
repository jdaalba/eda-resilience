package com.jdaalba.eda.resilience;

import com.jdaalba.eda.resilience.outbox.DbListener;

import java.util.stream.Stream;

public class SidecarMain {

    public static void main(String[] args) {
        var listener = new DbListener();
        Stream.iterate(
                        true,
                        Boolean::booleanValue,
                        b -> {
                            listener.execute();
                            return b;
                        }
                )
                .forEach(b -> {
                });
    }
}
