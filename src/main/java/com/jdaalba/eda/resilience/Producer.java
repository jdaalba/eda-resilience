package com.jdaalba.eda.resilience;


import com.jdaalba.eda.resilience.messaging.Sink;
import com.jdaalba.eda.resilience.messaging.impl.KafkaSink;
import com.jdaalba.eda.resilience.reader.UserReader;
import com.jdaalba.eda.resilience.vo.OutputEvent;

public class Producer {

    public static void main(String[] args) {
        Sink sink = new KafkaSink();
        new UserReader().read().forEach(e -> sink.send(new OutputEvent(e, "users")));
    }
}
