package com.jdaalba.eda.resilience.messaging;

import com.jdaalba.eda.resilience.vo.OutputEvent;

public interface Sink {
    void send(OutputEvent event);
}
