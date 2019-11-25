package ru.otus.test.basic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.ATM;
import ru.otus.atm.CassetteHolder;
import ru.otus.atm.currency.AbstractNote;
import ru.otus.atm.currency.Dollar;
import ru.otus.atm.currency.Rouble;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Правильные сценарии")
class Positive {

    private ATM atm;

    @BeforeEach
    void setUp() {
        atm = ATM.Factory.create();
    }

    @Test
    @DisplayName("Снять сколько-то денег")
    void withdraw() {
        atm.accept(ImmutableList.of(
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.FIFTY));
        Map<AbstractNote, Integer> give = atm.give(250L);
        Map<AbstractNote, Integer> expected = ImmutableMap.of(
                Rouble.HUNDRED_TWO, 1,
                Rouble.FIFTY, 1
        );
        assertEquals(expected, give);
    }

    @Test
    @DisplayName("Снять сколько-то денег два раза")
    void withdrawConsecutive() {
        Map<AbstractNote, Integer> giveFirst = atm.give(250L);
        Map<AbstractNote, Integer> expectedFirst = ImmutableMap.of(
                Rouble.HUNDRED_TWO, 1,
                Rouble.FIFTY, 1
        );
        assertEquals(expectedFirst, giveFirst);

        Map<AbstractNote, Integer> giveThen = atm.give(2400L);
        Map<AbstractNote, Integer> expectedThen = ImmutableMap.of(
                Rouble.HUNDRED_TWO, 2,
                Rouble.THOUSAND_TWO, 1
        );
        assertEquals(expectedThen, giveThen);
    }

    @Test
    @DisplayName("Снять все")
    void withdrawAll() {
        Map<AbstractNote, Integer> give = atm.give(922000L);
        Map<AbstractNote, Integer> expected = Map.of(
                Rouble.THOUSAND_FIVE, 50,
                Rouble.THOUSAND_TWO, 100,
                Rouble.THOUSAND_ONE, 200,
                Rouble.HUNDRED_FIVE, 400,
                Rouble.HUNDRED_TWO, 200,
                Rouble.HUNDRED_ONE, 200,
                Rouble.FIFTY, 200,
                Rouble.TEN, 200
        );
        assertEquals(expected, give);
    }

    @Test
    @DisplayName("Положить и снять всё")
    void withdrawAllWithAdded() {
        atm.accept(ImmutableList.of(
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.FIFTY));
        Map<AbstractNote, Integer> give = atm.give(922100L);
        Map<AbstractNote, Integer> expected = Map.of(
                Rouble.THOUSAND_FIVE, 50,
                Rouble.THOUSAND_TWO, 100,
                Rouble.THOUSAND_ONE, 200,
                Rouble.HUNDRED_FIVE, 400,
                Rouble.HUNDRED_TWO, 200,
                Rouble.HUNDRED_ONE, 200,
                Rouble.FIFTY, 201,
                Rouble.TEN, 205
        );
        assertEquals(expected, give);
    }

    @Test
    @DisplayName("Создать два банкомата для разных валют")
    void foreignCurrencyTest() {
        ATM atmRouble = ATM.Factory.create(new CassetteHolder(
                ImmutableMap.of(
                        Rouble.HUNDRED_FIVE, 3
                )));
        ATM atmDollar = ATM.Factory.create(new CassetteHolder(
                ImmutableMap.of(
                        Dollar.HUNDRED_ONE, 7
                )));
        atmRouble.give(1000L);
        atmDollar.give(500L);
    }

}
