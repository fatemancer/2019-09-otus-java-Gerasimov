package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;

import java.util.HashMap;
import java.util.Map;

public class CassetteImpl implements Cassette {

    private final Map<AbstractNote, Integer> holder;

    CassetteImpl(AbstractNote note, int amount) {
        this();
        holder.put(note, amount);
    }

    private CassetteImpl() {
        holder = new HashMap<>();
    }

    @Override
    public void add(AbstractNote n, int amount) {
        holder.merge(n, 1, (cur, upd) -> cur + amount);
    }

    @Override
    public void remove(AbstractNote n, int amount) {
        holder.merge(n, 0, (cur, upd) -> cur - amount);
    }

    @Override
    public Long sum() {
        return holder.entrySet().stream().mapToLong(entry -> entry.getKey().getNominal() * entry.getValue()).sum();
    }

    @Override
    public AbstractNote getCurrentNominal() {
        return holder.keySet().stream().findFirst().orElse(null);
    }

    @Override
    public int getNominalValue() {
        return getCurrentNominal().getNominal();
    }

    @Override
    public int getNotesForCurrentNominal() {
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
