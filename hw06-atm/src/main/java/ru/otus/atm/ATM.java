package ru.otus.atm;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class ATM {

    private static final Logger log = LoggerFactory.getLogger(ATM.class);

    private Cassette cassette;
    private List<BiConsumer<Long, Validator.Context>> validators = ImmutableList.of(
            Validator::nonNull,
            Validator::positive,
            Validator::nonOverflowing,
            Validator::quickDivisorCheck
    );

    ATM(Cassette cassette) {
        this.cassette = cassette;
    }

    public void accept(List<Note> notes) {
        for (Note n : notes) {
            this.cassette.add(n);
            log.info("Added {} to {}", n, cassette);
        }
    }

    public Map<Note, Integer> give(Long amount) {
        Validator.Context context = new Validator.Context(cassette.sum());
        validators.forEach(v -> v.accept(amount, context));
        return split(amount);
    }

    private Map<Note, Integer> split(long amount) {
        long rest = amount;
        Map<Note, Integer> result = new TreeMap<>(Cassette.COMPARATOR);
        while (true) {
            Note currentNominal = cassette.getCurrentNominal();
            log.debug("Trying to use {}", currentNominal);
            int notesForCurrentNominal = cassette.getNotesForCurrentNominal();
            while (
                    Math.abs(rest / currentNominal.getNominal()) > 0
                    && notesForCurrentNominal > 0
            ) {
                rest -= currentNominal.getNominal();
                notesForCurrentNominal--;
                log.debug("{} - {} = {}, {} notes of {} left",
                        rest + currentNominal.getNominal(),
                        currentNominal.getNominal(),
                        rest,
                        notesForCurrentNominal,
                        currentNominal);
                result.merge(currentNominal, 1, Integer::sum);
            }
            if (cassette.hasNextNominal()) {
                cassette.nextNominal();
            } else {
                break;
            }
        }
        if (rest == 0) {
            seal(result);
            return result;
        } else {
            String error = String.format("Impossible to give change for: %s", amount);
            throw new IllegalArgumentException(error);
        }
    }

    private void seal(Map<Note, Integer> result) {
        cassette.remove(result);
        cassette.reset();
        log.info(
                "Transaction closed: removed {}, notes left: {}, making total of {}. Cassette nominal reset to {}",
                result,
                cassette,
                cassette.sum(),
                cassette.getCurrentNominal()
        );
    }
}
