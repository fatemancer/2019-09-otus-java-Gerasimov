package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;
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
        if (Arrays.stream(context.getNoteInATM().getNominals())
                .map(AbstractNote::getNominal)
                .noneMatch(n -> amount % n == 0)) {
            String error = String.format("Sum must be possible to be combined of: %s", Arrays.toString(Rouble.values()));
            throw new IllegalArgumentException(error);
        }
    }

    static class Context {

        final Long available;
        final AbstractNote noteInATM;

        Context(Long available, AbstractNote noteInATM) {
            this.available = available;
            this.noteInATM = noteInATM;
        }

        Long getAvailable() {
            return available;
        }

        public AbstractNote getNoteInATM() {
            return noteInATM;
        }
    }
}
