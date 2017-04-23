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
    @NotNull
    @Pattern(regexp = "^\\+?[0-9]+(\\.[0-9]{1,2})?")
    private String amount;
    // http://stackoverflow.com/questions/12756159/regex-and-iso8601-formatted-datetime
    @Pattern(regexp = "^\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d(\\.\\d+)?(([+-]\\d\\d:\\d\\d)|Z)?$")
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
