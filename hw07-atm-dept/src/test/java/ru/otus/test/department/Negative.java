package ru.otus.test.department;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.otus.atm.ATM;
import ru.otus.atm.ATMFactory;
import ru.otus.atm.CassetteHolder;
import ru.otus.atm.Entity;
import ru.otus.atm.currency.Rouble;
import ru.otus.department.ATMDepartment;
import ru.otus.department.ExchangeRateManager;

public class Negative {

    private static ATMDepartment atmDepartment;
    private static ExchangeRateManager exchangeRateManager;

    private static long DEFAULT_ATM_AMOUNT = 922000L;

    private ATM firstATM;
    private ATM secondATM;
    private Entity thirdWrapped;

    @BeforeAll
    static void setUpFactory() {
        atmDepartment = new ATMDepartment(new ATMFactory());
        exchangeRateManager = new ExchangeRateManager();
    }

    @BeforeEach
    void setUp() {
        atmDepartment = new ATMDepartment(new ATMFactory());
        firstATM = atmDepartment.createEntity();
        secondATM = atmDepartment.createEntity(
                new CassetteHolder(ImmutableMap.of(
                        Rouble.HUNDRED_ONE, 10,
                        Rouble.THOUSAND_FIVE, 5
                ))
        );
        thirdWrapped = atmDepartment.createEntity();
    }
}
