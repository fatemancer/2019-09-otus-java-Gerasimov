package ru.otus.test.basic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.ATM;
import ru.otus.atm.ATMFactory;
import ru.otus.atm.CassetteHolder;
import ru.otus.atm.currency.Dollar;
import ru.otus.atm.currency.Rouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.TEN,
                Rouble.FIFTY));
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
                        "[THOUSAND_FIVE, THOUSAND_TWO, THOUSAND_ONE, " +
                        "HUNDRED_FIVE, HUNDRED_TWO, HUNDRED_ONE, FIFTY, TEN]",
                e.getMessage()
        );
    }

    @Test
    @DisplayName("Снять сумму, не собираемую из имеющихся купюр (но теоретически возможную)")
    void withdrawUnsplitable() {
        CassetteHolder cassette = new CassetteHolder(ImmutableMap.of(
                Rouble.TEN, 7,
                Rouble.HUNDRED_TWO, 2,
                Rouble.THOUSAND_FIVE, 1
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

    @Test
    @DisplayName("Положить разные валюты в кассету")
    void mixedATMFails() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ATMFactory.create(new CassetteHolder(
                        ImmutableMap.of(
                                Dollar.HUNDRED_ONE, 3,
                                Rouble.HUNDRED_FIVE, 3
                        ))),
                "java.lang.IllegalArgumentException: Only one currency per ATM supported, " +
                        "provided: [class ru.otus.atm.currency.Dollar, class ru.otus.atm.currency.Rouble]");
    }
}
