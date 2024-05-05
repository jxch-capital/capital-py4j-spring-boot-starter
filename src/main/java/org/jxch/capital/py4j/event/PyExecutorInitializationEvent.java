package org.jxch.capital.py4j.event;

import org.springframework.context.ApplicationEvent;

public class PyExecutorInitializationEvent extends ApplicationEvent {

    public PyExecutorInitializationEvent(Object source) {
        super(source);
    }

}
