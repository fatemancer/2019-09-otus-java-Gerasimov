package ru.otus.atm;

import ru.otus.atm.currency.Rouble;

import java.util.Arrays;

class Validator {

    static void nonNull(Long amount, Context context) {
        if (amount == null) {
            String error = String.format("Sum must not be null %s", amount);
            throw new IllegalArgumentException(error);
        }
    }

    static void positive(Long amount, Context context) {
        if (amount <= 0) {
            String error = String.format("Sum must be positive: %s", amount);
            throw new IllegalArgumentException(error);
        }
    }

    static void nonOverflowing(Long amount, Context context) {
        if (amount > context.getAvailable()) {
            String error = String.format("Max available funds: %s", context.getAvailable());
            throw new IllegalArgumentException(error);
        }
    }

    static void quickDivisorCheck(Long amount, Context context) {
        if (Arrays.stream(Rouble.nominals).map(Rouble::getNominal).noneMatch(n -> amount % n == 0)) {
            String error = String.format("Sum must be possible to be combined of: %s", Arrays.toString(Rouble.values()));
            throw new IllegalArgumentException(error);
        }
    }

    static class Context {

        Long available;

        Context(Long available) {
            this.available = available;
        }

        Long getAvailable() {
            return available;
        }
    }
}
