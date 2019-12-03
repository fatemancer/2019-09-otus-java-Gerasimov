package ru.otus.atm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.currency.AbstractNote;
import ru.otus.atm.currency.CurrencyManager;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class ATMImpl implements ATM {

    private static final Logger log = LoggerFactory.getLogger(ATMImpl.class);
    private static final Comparator<AbstractNote> COMPARATOR = Comparator.comparing(AbstractNote::getNominal).reversed();

    private CassetteHolder cassetteHolder;
    private final List<BiConsumer<Long, Validator.Context>> validators = ImmutableList.of(
            Validator::nonNull,
            Validator::positive,
            Validator::nonOverflowing,
            Validator::quickDivisorCheck
    );

    public ATMImpl(EntityConstructor entityConstructor) {
        this.cassetteHolder = (CassetteHolder) entityConstructor;
    }

    @Override
    public void accept(List<AbstractNote> notes) {
        cassetteHolder.splitAndConsume(notes);
    }

    @Override
    public Map<AbstractNote, Integer> give(Long amount) {
        Validator.Context context = new Validator.Context(cassetteHolder.sum(), cassetteHolder.getCurrency());
        // PATTERN:strategy
        // "Неклассический" вариант с функциональными интерфейсами
        validators.forEach(v -> v.accept(amount, context));
        return split(amount);
    }

    private Map<AbstractNote, Integer> split(long amount) {
        long rest = amount;
        Map<AbstractNote, Integer> result = new TreeMap<>(COMPARATOR);
        // PATTERN:iterator
        for (Cassette cassette : cassetteHolder) {
            AbstractNote currentNominal = cassette.getCurrentNominal();
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
        }
        if (rest == 0) {
            seal(result);
            return result;
        } else {
            String error = String.format("Impossible to give change for: %s", amount);
            throw new IllegalArgumentException(error);
        }
    }

    private void seal(Map<AbstractNote, Integer> result) {
        cassetteHolder.remove(result);
        log.info(
                "Transaction closed: removed {}, notes left: {}, making total of {}",
                result,
                cassetteHolder,
                cassetteHolder.sum()
        );
    }

    @Override
    public EntityData<? extends Entity> getData(Long id) {
        return new EntityData.EntityDataBuilder()
                .setId(id)
                .setKey(DataType.MONEY)
                .setData(ImmutableMap.of(
                        "currency", CurrencyManager.getCurrency(cassetteHolder.getCurrency().getClass()).toString(),
                        "totalMoneyAmount", cassetteHolder.sum().toString()))
                .build();
    }

    @Override
    public void setState(EntityState<? super EntityConstructor> state) {
        cassetteHolder = (CassetteHolder) state.getEntityConstructor();
    }

    @Override
    public EntityState<EntityConstructor> getState() {
        return new ATMState(cassetteHolder);
    }
}
