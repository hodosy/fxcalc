package org.hodosy.fxcalc.service.pojo;

import java.math.BigDecimal;
import java.util.Map;

public class DailyCurrencyRateHolder {

    private final Map<String, BigDecimal> exchangeRate;

    public DailyCurrencyRateHolder(Map<String, BigDecimal> exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getExchangeRate(String key) {
        return exchangeRate.get(key);
    }

}
