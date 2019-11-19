package ru.otus.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.ATM;
import ru.otus.atm.ATMFactory;
import ru.otus.atm.Cassette;
import ru.otus.atm.Note;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Ошибочные сценарии")
class Negative {
    private ATM atm;

    @BeforeEach
    void setUp() {
        atm = ATMFactory.create();
    }

    @Test
    @DisplayName("Снять больше чем есть")
    void withdrawTooMuch() {
        atm.accept(ImmutableList.of(
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.TEN,
                Note.FIFTY));
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> atm.give(1_000_000L)
        );
        assertEquals("Max available funds: 922100", e.getMessage());
    }

    @Test
    @DisplayName("Снять сумму, не кратную ни одной купюре")
    void withdrawTotallyUnsplitable() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> atm.give(1337L)
        );
        assertEquals(
                "Sum must be possible to be combined of: " +
                        "[THOUSAND_FIVE, THOUSAND_TWO, THOUSAND_ONE, HUNDRED_FIVE, HUNDRED_TWO, HUNDRED_ONE, FIFTY, TEN]",
                e.getMessage()
        );
    }

    @Test
    @DisplayName("Снять сумму, не собираемую из имеющихся купюр (но теоретически возможную)")
    void withdrawUnsplitable() {
        Cassette cassette = new Cassette(ImmutableMap.of(
                Note.TEN, 7,
                Note.HUNDRED_TWO, 2,
                Note.THOUSAND_FIVE, 1
        ));
        atm = ATMFactory.create(cassette);
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> atm.give(5080L)
        );
        assertEquals(
               "Impossible to give change for: 5080",
                e.getMessage()
        );
    }

    @Test
    @DisplayName("Снять сумму меньше 0")
    void withdrawNegative() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> atm.give(-100L)
        );
        assertEquals("Sum must be positive: -100", e.getMessage());
    }

    @Test
    @DisplayName("Снять 0 денег")
    void withdrawZero() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> atm.give(0L)
        );
        assertEquals("Sum must be positive: 0", e.getMessage());
    }
}
