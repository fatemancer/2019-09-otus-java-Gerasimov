package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;

import java.util.List;
import java.util.Map;

public interface ATM extends Entity {
    void accept(List<AbstractNote> notes);

    Map<AbstractNote, Integer> give(Long amount);
}
