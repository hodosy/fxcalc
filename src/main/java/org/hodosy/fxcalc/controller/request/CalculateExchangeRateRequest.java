package org.hodosy.fxcalc.controller.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CalculateExchangeRateRequest {

    @NotNull
    @Pattern(regexp = "[A-Z]{3}")
    private String currency;
    @NotNull
    @Pattern(regexp = "Buy|Sell")
    private String side;
    private String dateTime;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
