package org.hodosy.fxcalc.controller.response;

import java.math.BigDecimal;

public class CommissionResponse {
    private final BigDecimal commission;
    private final String currency;

    public CommissionResponse(BigDecimal commission, String currency) {
        this.commission = commission;
        this.currency = currency;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public String getCurrency() {
        return currency;
    }
}
