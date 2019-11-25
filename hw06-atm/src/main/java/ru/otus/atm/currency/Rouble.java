package ru.otus.atm.currency;

public enum Rouble implements AbstractNote {

    THOUSAND_FIVE(5_000),
    THOUSAND_TWO(2_000),
    THOUSAND_ONE(1_000),
    HUNDRED_FIVE(500),
    HUNDRED_TWO(200),
    HUNDRED_ONE(100),
    FIFTY(50),
    TEN(10),
    ;

    public static final Rouble[] nominals = Rouble.values();
    private final int nominal;

    Rouble(int i) {
        this.nominal = i;
    }

    @Override
    public AbstractNote[] getNominals() {
        return nominals;
    }

    @Override
    public int getNominal() {
        return nominal;
    }
}
