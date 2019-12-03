package ru.otus.department;

import com.google.common.collect.ImmutableMap;
import ru.otus.atm.currency.Currency;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRateManager {

    static Map<Currency, BigDecimal> RATES = ImmutableMap.of(
            Currency.DOLLAR, BigDecimal.valueOf(64.1005),
            Currency.ROUBLE, BigDecimal.valueOf(1.0)
    );

    public Map<Currency, BigDecimal> getCurrentRates() {
        return RATES;
    }
}
