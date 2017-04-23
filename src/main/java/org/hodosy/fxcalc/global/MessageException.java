package org.hodosy.fxcalc.global;

import org.hodosy.fxcalc.controller.response.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class MessageException extends RuntimeException{

    private final Collection<Message> messages;

    public MessageException() {
        messages = new ArrayList<>();
    }

    public MessageException(Message message){
        messages = Collections.singleton(message);
    }

    public Collection<Message> getMessages() {
        return messages;
    }

    public void forEach(Consumer<? super Message> action) {
        messages.forEach(action);
    }
}
