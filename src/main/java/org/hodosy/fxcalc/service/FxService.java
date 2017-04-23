package org.hodosy.fxcalc.service;

import org.apache.log4j.Logger;
import org.hodosy.fxcalc.controller.response.CalculateExchangeRateResponse;
import org.hodosy.fxcalc.controller.response.Message;
import org.hodosy.fxcalc.global.ErrorCodes;
import org.hodosy.fxcalc.global.MessageException;
import org.hodosy.fxcalc.global.SideEnum;
import org.hodosy.fxcalc.service.pojo.CalculateFxInput;
import org.hodosy.fxcalc.service.pojo.DailyCurrencyRateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class FxService implements IFxService {

    private static final Logger logger = Logger.getLogger(FxService.class);
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final String eurofxBaseCurrency;
    private final ZoneId eurofxZoneId;
    private final BigDecimal commission;
    private final int roundingScale;
    private final int keepForDays;
    private final MathContext moneyRoundingScale = new MathContext(3, RoundingMode.HALF_UP);
    private final ConcurrentNavigableMap<LocalDate, DailyCurrencyRateHolder> currencyRateHolder;

    private BigDecimal income = BigDecimal.ZERO;

    @Autowired
    public FxService(@Value("${eurofxref.base.currency:EUR}") String eurofxBaseCurrency,
                     @Value("${eurofxref.timezone:CET}") String zoneId,
                     @Value("${comission.in.percentage:1}") BigDecimal commission,
                     @Value("${rounding.scale:5}") int roundingScale,
                     @Value("${keep.for.days:60}") int keepForDays) {
        this.eurofxBaseCurrency = eurofxBaseCurrency;
        this.currencyRateHolder = new ConcurrentSkipListMap<>();
        eurofxZoneId = ZoneId.of(zoneId);
        this.roundingScale = roundingScale;
        this.commission = validateCommission(commission);
        this.keepForDays = keepForDays;
    }

    private BigDecimal validateCommission(BigDecimal commission) {
        if (commission.compareTo(BigDecimal.ZERO) >= 0 && commission.compareTo(HUNDRED) < 100) {
            return commission.divide(HUNDRED, roundingScale, BigDecimal.ROUND_HALF_UP);
        }
        throw new IllegalArgumentException("Unacceptable commission (" + commission + "). Must be between 99 and 0");
    }

    @PostConstruct
    public void mock() {
        Map<String, BigDecimal> asd = new HashMap<>();
        asd.put("HUF", new BigDecimal("0.1234"));
        currencyRateHolder.put(LocalDate.now(), new DailyCurrencyRateHolder(asd));
    }

    @Override
    public CalculateExchangeRateResponse calculateFx(@Valid @NotNull CalculateFxInput input) throws MessageException {
        CalculateExchangeRateResponse output = new CalculateExchangeRateResponse();
        ZonedDateTime eurofxTime = input.getDateTime().withZoneSameInstant(eurofxZoneId);

        DailyCurrencyRateHolder dailyCurrencyRateHolder = currencyRateHolder.get(eurofxTime.toLocalDate());
        if (dailyCurrencyRateHolder == null) {
            if(ZonedDateTime.now().isBefore(input.getDateTime())){
                throw new MessageException(new Message.MessageBuilder(ErrorCodes.EXCHANGE_RATE_NA_TIME_FEATURE).field("dateTime").build());
            }else if(currencyRateHolder.lastKey().isBefore(eurofxTime.toLocalDate())){
                dailyCurrencyRateHolder = currencyRateHolder.lastEntry().getValue();
            }else {
                throw new MessageException(new Message.MessageBuilder(ErrorCodes.EXCHANGE_RATE_NA_TIME).field("dateTime").build());
            }
        }
        output.setCalculatedOn(eurofxTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        BigDecimal exchangeRate = dailyCurrencyRateHolder.getExchangeRate(input.getCurrency());
        if (exchangeRate == null) {
            throw new MessageException(new Message.MessageBuilder(ErrorCodes.EXCHANGE_RATE_NA_CURRENCY).field("currency").build());
        }
        output.setExchangeRate(exchangeRate);

        BigDecimal commission;
        BigDecimal calculatedValue;
        if (input.getSide() == SideEnum.Buy) {
            output.setBuyCurrency(input.getCurrency());
            output.setSellCurrency(eurofxBaseCurrency);
            commission = input.getAmount().multiply(this.commission).round(moneyRoundingScale);
            calculatedValue = input.getAmount().subtract(commission, moneyRoundingScale).multiply(exchangeRate);
        } else {
            output.setBuyCurrency(eurofxBaseCurrency);
            output.setSellCurrency(input.getCurrency());
            calculatedValue = input.getAmount().divide(exchangeRate, roundingScale);
            commission = calculatedValue.multiply(this.commission).round(moneyRoundingScale);
            calculatedValue = calculatedValue.subtract(commission);
        }
        calculatedValue = calculatedValue.round(moneyRoundingScale);
        addCommission(commission);
        output.setCommission(commission);
        output.setValue(calculatedValue);

        return output;
    }

    public void addCurrencyMapping(LocalDate eurofxTime, DailyCurrencyRateHolder dailyCurrencyRateHolder) {
        currencyRateHolder.put(eurofxTime, dailyCurrencyRateHolder);
    }

    public void evictOldEntries() {
        currencyRateHolder.keySet()
                .stream()
                .filter(getOldestAllowedEntry()::isAfter)
                .peek(LD -> logger.info("The entry from " + LD + " is older than " + keepForDays + " days and will be removed"))
                .forEach(currencyRateHolder::remove);
    }

    private LocalDate getOldestAllowedEntry() {
        return ZonedDateTime.now().minusDays(keepForDays).withZoneSameInstant(eurofxZoneId).toLocalDate();
    }

    private synchronized void addCommission(BigDecimal commission) {
        this.income = this.income.add(commission);
    }

    public synchronized BigDecimal getIncome() {
        return this.income;
    }
}
