package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;
import ru.otus.atm.currency.Rouble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ATMFactory implements EntityFactory {

    @Override
    public ATM create() {
        Map<AbstractNote, Integer> notes = new HashMap<>();
        notes.put(Rouble.THOUSAND_FIVE, 50);
        notes.put(Rouble.THOUSAND_TWO, 100);
        notes.put(Rouble.THOUSAND_ONE, 200);
        notes.put(Rouble.HUNDRED_FIVE, 400);
        notes.put(Rouble.HUNDRED_TWO, 200);
        notes.put(Rouble.HUNDRED_ONE, 200);
        notes.put(Rouble.FIFTY, 200);
        notes.put(Rouble.TEN, 200);

        return create(new CassetteHolder(notes));
    }

    @Override
    public ATM create(EntityConstructor entityConstructor) {
        CassetteHolder cassetteHolder = (CassetteHolder) entityConstructor;
        validate(cassetteHolder);
        return new ATMImpl(cassetteHolder);
    }

    static void validate(CassetteHolder cassetteHolder) {
        List<Class> noteTypes = cassetteHolder.getCassettes().stream()
                .map(Cassette::getCurrentNominal)
                .map(AbstractNote::getClass)
                .distinct()
                .collect(Collectors.toList());

        if (noteTypes.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("Only one currency per ATM supported, provided: %s", noteTypes)
            );
        }
    }
}
