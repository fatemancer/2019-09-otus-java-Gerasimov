package ru.otus.atm;

import ru.otus.atm.currency.AbstractNote;
import ru.otus.atm.currency.Rouble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ATMFactory {

    public static Class<? extends AbstractNote> DEFAULT_CURRENCY = Rouble.class;

    public static ATM create() {
        return defaultATM();
    }

    public static ATM create(CassetteHolder cassetteHolder) {
        validate(cassetteHolder);
        return new ATMImpl(cassetteHolder);
    }

    private static void validate(CassetteHolder cassetteHolder) {
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

    private static ATM defaultATM() {
        Map<AbstractNote, Integer> notes = new HashMap<>();
        notes.put(Rouble.THOUSAND_FIVE, 50);
        notes.put(Rouble.THOUSAND_TWO, 100);
        notes.put(Rouble.THOUSAND_ONE, 200);
        notes.put(Rouble.HUNDRED_FIVE, 400);
        notes.put(Rouble.HUNDRED_TWO, 200);
        notes.put(Rouble.HUNDRED_ONE, 200);
        notes.put(Rouble.FIFTY, 200);
        notes.put(Rouble.TEN, 200);

        return ATMFactory.create(new CassetteHolder(notes));
    }
}
