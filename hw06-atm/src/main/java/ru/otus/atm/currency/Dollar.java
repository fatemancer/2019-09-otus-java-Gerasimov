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

    private final int nominal;

    Dollar(int i) {
        this.nominal = i;
    }

    @Override
    public AbstractNote[] getNominals() {
        return Dollar.values();
    }

    @Override
    public int getNominal() {
        return nominal;
    }
}
