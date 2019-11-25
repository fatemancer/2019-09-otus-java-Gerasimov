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

    public static final Dollar[] nominals = Dollar.values();
    private final int nominal;

    Dollar(int i) {
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
