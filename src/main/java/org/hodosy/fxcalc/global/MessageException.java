package org.hodosy.fxcalc.global;

import org.hodosy.fxcalc.controller.response.Message;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class MessageException extends RuntimeException {

    private final Collection<Message> messages;

    public MessageException(Message message) {
        super(message.toString());
        messages = Collections.singleton(message);
    }

    public void forEach(Consumer<? super Message> action) {
        messages.forEach(action);
    }

}
