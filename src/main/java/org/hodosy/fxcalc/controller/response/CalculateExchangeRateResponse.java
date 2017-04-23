package org.hodosy.fxcalc.controller.response;

import java.math.BigDecimal;

public class CalculateExchangeRateResponse {
    private String calculatedOn;
    private String sellCurrency;
    private String buyCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal value;
    private BigDecimal commission;

    public String getCalculatedOn() {
        return calculatedOn;
    }

    public void setCalculatedOn(String calculatedOn) {
        this.calculatedOn = calculatedOn;
    }

    public String getSellCurrency() {
        return sellCurrency;
    }

    public void setSellCurrency(String sellCurrency) {
        this.sellCurrency = sellCurrency;
    }

    public String getBuyCurrency() {
        return buyCurrency;
    }

    public void setBuyCurrency(String buyCurrency) {
        this.buyCurrency = buyCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}
