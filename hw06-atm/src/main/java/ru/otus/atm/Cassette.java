package ru.otus.atm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.currency.AbstractNote;

import java.util.HashMap;
import java.util.Map;

public class Cassette {

    private static final Logger log = LoggerFactory.getLogger(Cassette.class);

    private Map<AbstractNote, Integer> holder;

    Cassette(AbstractNote note, int amount) {
        this();
        holder.put(note, amount);
    }

    private Cassette() {
        holder = new HashMap<>();
    }

    void add(AbstractNote n, int amount) {
        holder.merge(n, 1, (cur, upd) -> cur + amount);
    }

    void remove(AbstractNote n, int amount) {
        holder.merge(n, 0, (cur, upd) -> cur - amount);
    }

    Long sum() {
        return holder.entrySet().stream().mapToLong(entry -> entry.getKey().getNominal() * entry.getValue()).sum();
    }

    AbstractNote getCurrentNominal() {
        return holder.keySet().stream().findFirst().orElse(null);
    }

    int getNominalValue() {
        return getCurrentNominal().getNominal();
    }

    int getNotesForCurrentNominal() {
        return holder.values().stream().findFirst().orElse(0);
    }

    @Override
    public String toString() {
        return "Cassette{" +
                "holder=" + holder +
                ", currency=" + getCurrentNominal().getClass().getSimpleName() +
                '}';
    }
}
