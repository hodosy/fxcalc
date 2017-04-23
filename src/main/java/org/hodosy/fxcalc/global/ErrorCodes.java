package org.hodosy.fxcalc.global;

import java.util.logging.Level;

public enum ErrorCodes {
    UNKNOWN_ERROR("1000", "Unexpected error"),
    INPUT_ERROR("1001", "Wrong input parameter"),
    EXCHANGE_RATE_NA_TIME("1002", "For the given time no exchange rate is available"),
    EXCHANGE_RATE_NA_CURRENCY("1003", "Exchange rate is not available for the given currency"),
    EXCHANGE_RATE_NA_TIME_FEATURE("1004", "The given time is in the feature")

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
