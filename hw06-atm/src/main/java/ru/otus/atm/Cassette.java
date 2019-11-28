package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;

interface Cassette {
    void add(int amount);

    void remove(int amount);

    Long sum();

    AbstractNote getCurrentNominal();

    int getNominalValue();

    int getNotesForCurrentNominal();
}
