package org.hodosy.fxcalc.controller.response;

import org.hodosy.fxcalc.global.ErrorCodes;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private final String code;
    private final String severity;
    private final String text;
    private final String field;
    private final Map<String, String> parameters;

    Message(String code, String severity, String text, String field, Map<String, String> parameters) {
        this.code = code;
        this.severity = severity;
        this.text = text;
        this.field = field;
        this.parameters = parameters;
    }

    public String getCode() {
        return code;
    }

    public String getSeverity() {
        return severity;
    }

    public String getText() {
        return text;
    }

    public String getField() {
        return field;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public static class MessageBuilder{
        private String code;
        private String severity;
        private String text;
        private String field;
        private Map<String, String> parameters = new HashMap<>();

        public MessageBuilder code(String code){
            this.code = code;
            return this;
        }

        public MessageBuilder code(ErrorCodes code){
            this.code = code.getCode();
            this.severity = code.getSeverity();
            return this;
        }

        public MessageBuilder severity(String severity){
            this.severity = severity;
            return this;
        }
        public MessageBuilder text(String text){
            this.text = text;
            return this;
        }
        public MessageBuilder field(String field){
            this.field = field;
            return this;
        }

        public MessageBuilder appendParameter(String key, String value){
            parameters.put(key, value);
            return this;
        }

        public Message build(){
            return new Message(code, severity, text, field, parameters);
        }
    }
}
