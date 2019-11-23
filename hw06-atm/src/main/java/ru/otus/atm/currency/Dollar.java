package ru.otus.atm.currency;

public enum Dollar implements AbstractNote {

    HUNDRED_ONE(100),
    FIFTY(50),
    TWENTY(20),
    TEN(10),
    FIVE(5),
    TWO(2),
    ONE(1)
    ;

    public static Dollar[] nominals = Dollar.values();
    private int nominal;

    Dollar(int i) {
        this.nominal = i;
    }

    @Override
    public Dollar first() {
        return Dollar.HUNDRED_ONE;
    }

    @Override
    public int getNominal() {
        return nominal;
    }

    @Override
    public Dollar next() {
        return nominals[(this.ordinal()+1) % nominals.length];
    }

    @Override
    public boolean hasNext() {
        return this.ordinal() < nominals.length - 1;
    }
}
