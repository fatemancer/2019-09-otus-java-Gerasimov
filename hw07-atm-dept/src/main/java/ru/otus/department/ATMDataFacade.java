package ru.otus.department;

import ru.otus.atm.DataType;
import ru.otus.atm.EntityData;
import ru.otus.atm.currency.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

// PATTERN:facade
public class ATMDataFacade {

    ATMDepartment atmDepartment;
    ExchangeRateManager exchangeRateManager;

    public ATMDataFacade(ATMDepartment atmDepartment, ExchangeRateManager exchangeRateManager) {
        this.atmDepartment = atmDepartment;
        this.exchangeRateManager = exchangeRateManager;
    }

    public BigDecimal getReserves() {
        List<EntityData> rawData = atmDepartment.getRawData();

        return rawData.stream()
                .filter(entityData -> entityData.getKey().equals(DataType.MONEY))
                .map(EntityData::getData)
                .flatMap(entries -> {
                    String currency = entries.getOrDefault("currency", "UNKNOWN");
                    String amount = entries.getOrDefault("totalMoneyAmount", "0");
                    return Stream.of(calculateTotal(currency, amount));
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateTotal(String currency, String amount) {
        BigDecimal rate = getRate(currency);
        BigDecimal parsedAmount = BigDecimal.valueOf(Long.parseLong(amount));
        return rate.multiply(parsedAmount);
    }

    private BigDecimal getRate(String currency) {
        Currency parsedCurrency = Currency.valueOf(currency);
        return exchangeRateManager.getCurrentRates().getOrDefault(parsedCurrency, BigDecimal.ZERO);
    }

    void resetAll() {
        atmDepartment.resetAll();
    }

}
