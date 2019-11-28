package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;

public class CassetteImpl implements Cassette {

    private final AbstractNote note;
    private int notesAmount;

    CassetteImpl(AbstractNote note, int amount) {
        this.note = note;
        notesAmount = amount;
    }

    @Override
    public void add(int amount) {
        this.notesAmount += amount;
    }

    @Override
    public void remove(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    String.format("Trying to remove %s notes, %s notes exist. Also, this exception means ATM request" +
                            "validator is faulty", amount, notesAmount)
            );
        }
        this.notesAmount -= amount;
    }

    @Override
    public Long sum() {
        return (long) (note.getNominal() * notesAmount);
    }

    @Override
    public AbstractNote getCurrentNominal() {
        return note;
    }

    @Override
    public int getNominalValue() {
        return getCurrentNominal().getNominal();
    }

    @Override
    public int getNotesForCurrentNominal() {
        return notesAmount;
    }

    @Override
    public String toString() {
        return "CassetteImpl{" +
                "note=" + note +
                ", notesAmount=" + notesAmount +
                ", currency=" + getCurrentNominal().getClass().getSimpleName() +
                '}';
    }
}
