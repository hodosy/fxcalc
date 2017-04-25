package org.hodosy.fxcalc.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;

public class CustomResponseEntity<T> extends ResponseEntity<CustomResponseEntity.ResponseWrapper<T>> {
    public CustomResponseEntity(HttpStatus status) {
        this(null, status);
    }

    public CustomResponseEntity(T body, HttpStatus status) {
        super(new ResponseWrapper<>(body), status);
    }

    public CustomResponseEntity(T body) {
        this(body, HttpStatus.OK);
    }

    public void appendMessage(Message message) {
        getBody().getMessages().add(message);
    }

    public static class ResponseWrapper<T> {
        private final T response;
        private final Collection<Message> messages = new ArrayList<>();

        private ResponseWrapper(T response) {
            this.response = response;
        }

        public T getResponse() {
            return response;
        }

        public Collection<Message> getMessages() {
            return messages;
        }


    }
}
