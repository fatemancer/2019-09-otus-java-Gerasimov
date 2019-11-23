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

    public static Rouble[] nominals = Rouble.values();
    private int nominal;

    Rouble(int i) {
        this.nominal = i;
    }

    @Override
    public Rouble first() {
        return Rouble.THOUSAND_FIVE;
    }

    @Override
    public int getNominal() {
        return nominal;
    }

    @Override
    public Rouble next() {
        return nominals[(this.ordinal()+1) % nominals.length];
    }

    @Override
    public boolean hasNext() {
        return this.ordinal() < nominals.length - 1;
    }

}
