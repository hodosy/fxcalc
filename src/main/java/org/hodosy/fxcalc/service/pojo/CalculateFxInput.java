package org.hodosy.fxcalc.service.pojo;

import org.hodosy.fxcalc.global.SideEnum;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public class CalculateFxInput {
    @NotNull
    private final String currency;
    @NotNull
    @Min(0)
    private final BigDecimal amount;
    @NotNull
    private final SideEnum side;
    @NotNull
    @Past
    private final ZonedDateTime dateTime;

    private CalculateFxInput(String currency, BigDecimal amount, SideEnum side, ZonedDateTime dateTime) {
        this.currency = Objects.requireNonNull(currency, "Currency can't be null");
        this.amount = Objects.requireNonNull(amount, "Amount can't be null");
        this.side = Objects.requireNonNull(side, "Side must be either Buy or Sell");
        this.dateTime = Objects.requireNonNull(dateTime, "Date time is not given");
    }

    public String getCurrency() {
        return currency;
    }

    public SideEnum getSide() {
        return side;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "CalculateFxInput{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                ", side=" + side +
                ", dateTime=" + dateTime +
                '}';
    }

    public static class CalculateFxInputBuilder {
        private String currency;
        private SideEnum side;
        private BigDecimal amount;
        private ZonedDateTime dateTime;

        public CalculateFxInputBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public CalculateFxInputBuilder side(SideEnum side) {
            this.side = side;
            return this;
        }

        public CalculateFxInputBuilder dateTime(ZonedDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public CalculateFxInputBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public CalculateFxInput build() {
            return new CalculateFxInput(currency, amount, side, dateTime);
        }
    }
}
