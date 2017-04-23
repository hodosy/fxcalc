package org.hodosy.fxcalc.global;

import java.util.logging.Level;

public enum ErrorCodes {
    INPUT_ERROR("1001", "Wrong input parameter")

    ;
    private final String code;
    private final String genericMessage;

    ErrorCodes(String code, String genericMessage) {
        this.code = code;
        this.genericMessage = genericMessage;
    }

    public String getCode() {
        return code;
    }

    public String getGenericMessage() {
        return genericMessage;
    }

    public String getSeverity(){
        switch (getCode().substring(0, 1)){
            case "1":
                return Level.SEVERE.toString();
        }
        return null;
    }
}
