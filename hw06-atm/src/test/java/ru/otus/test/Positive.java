package ru.otus.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.ATM;
import ru.otus.atm.ATMFactory;
import ru.otus.atm.Note;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Правильные сценарии")
class Positive {

    private ATM atm;

    @BeforeEach
    void setUp() {
        atm = ATMFactory.create();
    }

    @Test
    @DisplayName("Снять сколько-то денег")
    void withdraw() {
        atm.accept(ImmutableList.of(
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.FIFTY));
        Map<Note, Integer> give = atm.give(250L);
        Map<Note, Integer> expected = ImmutableMap.of(
                Note.HUNDRED_TWO, 1,
                Note.FIFTY, 1
        );
        assertEquals(expected, give);
    }

    @Test
    @DisplayName("Снять сколько-то денег два раза")
    void withdrawConsecutive() {
        Map<Note, Integer> giveFirst = atm.give(250L);
        Map<Note, Integer> expectedFirst = ImmutableMap.of(
                Note.HUNDRED_TWO, 1,
                Note.FIFTY, 1
        );
        assertEquals(expectedFirst, giveFirst);

        Map<Note, Integer> giveThen = atm.give(2400L);
        Map<Note, Integer> expectedThen = ImmutableMap.of(
                Note.HUNDRED_TWO, 2,
                Note.THOUSAND_TWO, 1
        );
        assertEquals(expectedThen, giveThen);
    }

    @Test
    @DisplayName("Снять все")
    void withdrawAll() {
        Map<Note, Integer> give = atm.give(922000L);
        Map<Note, Integer> expected = Map.of(
                Note.THOUSAND_FIVE, 50,
                Note.THOUSAND_TWO, 100,
                Note.THOUSAND_ONE, 200,
                Note.HUNDRED_FIVE, 400,
                Note.HUNDRED_TWO, 200,
                Note.HUNDRED_ONE, 200,
                Note.FIFTY, 200,
                Note.TEN, 200
        );
        assertEquals(expected, give);
    }

    @Test
    @DisplayName("Положить и снять всё")
    void withdrawAllWithAdded() {
        atm.accept(ImmutableList.of(
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.FIFTY));
        Map<Note, Integer> give = atm.give(922100L);
        Map<Note, Integer> expected = Map.of(
                Note.THOUSAND_FIVE, 50,
                Note.THOUSAND_TWO, 100,
                Note.THOUSAND_ONE, 200,
                Note.HUNDRED_FIVE, 400,
                Note.HUNDRED_TWO, 200,
                Note.HUNDRED_ONE, 200,
                Note.FIFTY, 201,
                Note.TEN, 205
        );
        assertEquals(expected, give);
    }

}
