package org.hodosy.fxcalc.service;

import org.hodosy.fxcalc.controller.response.CalculateExchangeRateResponse;
import org.hodosy.fxcalc.global.MessageException;
import org.hodosy.fxcalc.global.SideEnum;
import org.hodosy.fxcalc.service.pojo.CalculateFxInput;
import org.hodosy.fxcalc.service.pojo.DailyCurrencyRateHolder;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FxServiceTest{

    @Test(expected = MessageException.class)
    public void testFeatureTime() throws Exception {
        FxService service = initialisedService();
        CalculateFxInput input = new CalculateFxInput.CalculateFxInputBuilder()
                .amount(new BigDecimal("123"))
                .currency("HUF")
                .dateTime(ZonedDateTime.of(3000, 4, 23, 18, 53, 0, 0, ZoneId.systemDefault()))
                .side(SideEnum.Buy)
                .build();

        service.calculateFx(input);
    }

    @Test(expected = MessageException.class)
    public void testNoSuchTimeForPeriod() throws Exception {
        FxService service = initialisedService();
        CalculateFxInput input = new CalculateFxInput.CalculateFxInputBuilder()
                .amount(new BigDecimal("123"))
                .currency("HUF")
                .dateTime(ZonedDateTime.of(2017, 4, 10, 18, 53, 0, 0, ZoneId.systemDefault()))
                .side(SideEnum.Buy)
                .build();

        service.calculateFx(input);
    }

    @Test(expected = MessageException.class)
    public void testNoSuchCurrency() throws Exception {
        FxService service = initialisedService();
        CalculateFxInput input = new CalculateFxInput.CalculateFxInputBuilder()
                .amount(new BigDecimal("123"))
                .currency("XXX")
                .dateTime(ZonedDateTime.of(2017, 4, 19, 18, 53, 0, 0, ZoneId.systemDefault()))
                .side(SideEnum.Buy)
                .build();

        service.calculateFx(input);
    }

    @Test
    public void testValidQuery_existingEntry() throws Exception {
        FxService service = initialisedService();
        CalculateFxInput input = new CalculateFxInput.CalculateFxInputBuilder()
                .amount(new BigDecimal("123"))
                .currency("HUF")
                .dateTime(ZonedDateTime.of(2017, 4, 21, 18, 53, 0, 0, ZoneId.systemDefault()))
                .side(SideEnum.Buy)
                .build();

        CalculateExchangeRateResponse response = service.calculateFx(input);

        assertEquals(new BigDecimal("314.18"), response.getExchangeRate());
    }

    @Test
    public void testValidQuery_notExistingEntry() throws Exception {
        FxService service = initialisedService();
        CalculateFxInput input = new CalculateFxInput.CalculateFxInputBuilder()
                .amount(new BigDecimal("123"))
                .currency("HUF")
                .dateTime(ZonedDateTime.of(2017, 4, 22, 18, 53, 0, 0, ZoneId.systemDefault()))
                .side(SideEnum.Buy)
                .build();

        CalculateExchangeRateResponse response = service.calculateFx(input);

        assertEquals(new BigDecimal("314.18"), response.getExchangeRate());
    }

    @Test
    public void testValidQuery_gapEntry() throws Exception {
        FxService service = initialisedService();
        CalculateFxInput input = new CalculateFxInput.CalculateFxInputBuilder()
                .amount(new BigDecimal("123"))
                .currency("HUF")
                .dateTime(ZonedDateTime.of(2017, 4, 18, 18, 53, 0, 0, ZoneId.systemDefault()))
                .side(SideEnum.Buy)
                .build();

        CalculateExchangeRateResponse response = service.calculateFx(input);

        assertEquals(new BigDecimal("312.05"), response.getExchangeRate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidQuery_negativeCommission() throws Exception {
        new FxService("EUR", "CET", new BigDecimal("-1"), 5, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidQuery_greedyCommission() throws Exception {
        new FxService("EUR", "CET", new BigDecimal("100"), 5, 1);
    }

    private FxService initialisedService() {
        FxService service = new FxService("EUR", "CET", BigDecimal.ONE, 5, 1);

        Map<String, BigDecimal> rate20170421 = new HashMap<>();
        rate20170421.put("HUF", new BigDecimal("314.18"));
        rate20170421.put("CHF", new BigDecimal("1.0680"));
        service.addCurrencyMapping(LocalDate.of(2017, 4, 21), new DailyCurrencyRateHolder(rate20170421));

        Map<String, BigDecimal> rate20170420 = new HashMap<>();
        rate20170420.put("HUF", new BigDecimal("313.5"));
        rate20170420.put("CHF", new BigDecimal("1.0701"));
        service.addCurrencyMapping(LocalDate.of(2017, 4, 20), new DailyCurrencyRateHolder(rate20170420));

        Map<String, BigDecimal> rate20170419 = new HashMap<>();
        rate20170419.put("HUF", new BigDecimal("313.05"));
        rate20170419.put("CHF", new BigDecimal("1.069"));
        service.addCurrencyMapping(LocalDate.of(2017, 4, 19), new DailyCurrencyRateHolder(rate20170419));

        Map<String, BigDecimal> rate20170417 = new HashMap<>();
        rate20170417.put("HUF", new BigDecimal("312.05"));
        rate20170417.put("CHF", new BigDecimal("1.069"));
        service.addCurrencyMapping(LocalDate.of(2017, 4, 17), new DailyCurrencyRateHolder(rate20170417));

        return service;
    }

}