package ru.otus.atm.currency;

public enum  Currency {

    UNKNOWN(AbstractNote.class),
    DOLLAR(Dollar.class),
    ROUBLE(Rouble.class);

    Class currency;

    Currency(Class<? extends AbstractNote> clazz) {
        currency = clazz;
    }
}
