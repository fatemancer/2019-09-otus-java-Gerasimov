package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;

interface Cassette {
    void add(AbstractNote n, int amount);

    void remove(AbstractNote n, int amount);

    Long sum();

    AbstractNote getCurrentNominal();

    int getNominalValue();

    int getNotesForCurrentNominal();
}
