package ru.otus.atm;

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
        if (Arrays.stream(Note.nominals).map(Note::getNominal).noneMatch(n -> amount % n == 0)) {
            String error = String.format("Sum must be possible to be combined of: %s", Arrays.toString(Note.values()));
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
