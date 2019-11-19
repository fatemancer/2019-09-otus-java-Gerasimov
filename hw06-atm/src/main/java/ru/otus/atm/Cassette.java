package ru.otus.atm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Cassette {

    static final Comparator<Note> COMPARATOR = Comparator.comparingInt(Note::getNominal).reversed();
    private static final Logger log = LoggerFactory.getLogger(Cassette.class);

    private SortedMap<Note, Integer> notes;
    private Note currentNominal;

    public Cassette(Map<Note, Integer> notes) {
        this();
        for (Map.Entry<Note, Integer> me : notes.entrySet()) {
            this.notes.put(me.getKey(), me.getValue());
        }
    }

    private Cassette() {
        this.notes = new TreeMap<>(COMPARATOR);
        this.currentNominal = Note.first();
        while (currentNominal.hasNext()) {
            log.debug("Init cassette cell: {}", currentNominal);
            notes.put(currentNominal, 0);
            if (currentNominal.hasNext()) {
                currentNominal = currentNominal.next();
            } else {
                break;
            }
        }
        reset();
    }

    void reset() {
        currentNominal = Note.first();
    }

    void add(Note n) {
        notes.merge(n, 1, Integer::sum);
    }

    Long sum() {
        return notes.entrySet().stream()
                .map(entry -> entry.getKey().getNominal() * entry.getValue())
                .mapToLong(Integer::longValue)
                .sum();
    }

    @Override
    public String toString() {
        return "Cassette{" +
                "notes=" + notes +
                '}';
    }

    Note getCurrentNominal() {
        return currentNominal;
    }

    int getNotesForCurrentNominal() {
           return notes.get(currentNominal);
    }

    boolean hasNextNominal() {
        return currentNominal.hasNext();
    }

    void nextNominal() {
        this.currentNominal = this.currentNominal.next();
    }

    void remove(Map<Note, Integer> result) {
        for (Map.Entry<Note, Integer> entry : result.entrySet()) {
            notes.computeIfPresent(entry.getKey(), (key, val) -> val = val - entry.getValue());
        }
    }
}
