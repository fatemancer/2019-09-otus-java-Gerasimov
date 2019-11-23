package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CassetteHolder {

    private static final Comparator<Cassette> COMPARATOR = Comparator.comparing(Cassette::getNominalValue).reversed();
    private List<Cassette> cassettes = new ArrayList<>();

    public CassetteHolder(Map<AbstractNote, Integer> notes) {
        for (Map.Entry<AbstractNote, Integer> entry : notes.entrySet()) {
            cassettes.add(new Cassette(entry.getKey(), entry.getValue()));
        }
        cassettes.sort(COMPARATOR);
    }

    List<Cassette> getCassettes() {
        return cassettes;
    }

    Long sum() {
        return cassettes.stream().mapToLong(Cassette::sum).sum();
    }

    void remove(Map<AbstractNote, Integer> result) {

        for (Cassette cassette : cassettes) {
            AbstractNote currentNominal = cassette.getCurrentNominal();
            if (result.containsKey(currentNominal)) {
                cassette.remove(currentNominal, result.get(currentNominal));
            }
        }
    }

    void splitAndConsume(List<AbstractNote> notes) {
        for (Cassette cassette : cassettes) {
            AbstractNote currentNominal = cassette.getCurrentNominal();
            for (AbstractNote note : notes) {
                if (note.equals(currentNominal)) {
                    cassette.add(note, 1);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "CassetteHolder{" +
                "cassettes=" + cassettes +
                '}';
    }
}
