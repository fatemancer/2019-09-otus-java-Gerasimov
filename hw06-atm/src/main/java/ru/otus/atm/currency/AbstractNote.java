package ru.otus.atm.currency;

public interface AbstractNote {

    AbstractNote first();

    int getNominal();

    AbstractNote next();

    boolean hasNext();
}
