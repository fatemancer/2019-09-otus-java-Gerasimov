package ru.otus.atm;

import java.util.HashMap;
import java.util.Map;

public class ATMFactory {

    public static ATM create() {
        return defaultATM();
    }

    public static ATM create(Cassette cassette) {
        return new ATM(cassette);
    }

    private static ATM defaultATM() {
        Map<Note, Integer> notes = new HashMap<>();
        notes.put(Note.THOUSAND_FIVE, 50);
        notes.put(Note.THOUSAND_TWO, 100);
        notes.put(Note.THOUSAND_ONE, 200);
        notes.put(Note.HUNDRED_FIVE, 400);
        notes.put(Note.HUNDRED_TWO, 200);
        notes.put(Note.HUNDRED_ONE, 200);
        notes.put(Note.FIFTY, 200);
        notes.put(Note.TEN, 200);

        return ATMFactory.create(new Cassette(notes));
    }
}
