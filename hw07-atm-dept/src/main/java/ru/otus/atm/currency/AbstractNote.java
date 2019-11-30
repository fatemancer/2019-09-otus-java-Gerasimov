package ru.otus.atm.currency;

public interface AbstractNote {

    AbstractNote[] getNominals();

    int getNominal();
}
