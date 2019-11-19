package ru.otus.atm;

public enum Note {
    THOUSAND_FIVE(5_000),
    THOUSAND_TWO(2_000),
    THOUSAND_ONE(1_000),
    HUNDRED_FIVE(500),
    HUNDRED_TWO(200),
    HUNDRED_ONE(100),
    FIFTY(50),
    TEN(10),
    ;

    public static Note[] nominals = Note.values();
    private int nominal;

    Note(int i) {
        this.nominal = i;
    }

    public static Note first() {
        return Note.THOUSAND_FIVE;
    }

    public int getNominal() {
        return nominal;
    }

    Note next() {
        return nominals[(this.ordinal()+1) % nominals.length];
    }

    boolean hasNext() {
        return this.ordinal() < nominals.length - 1;
    }

}
