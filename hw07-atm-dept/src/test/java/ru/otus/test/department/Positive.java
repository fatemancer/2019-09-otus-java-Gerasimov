package ru.otus.test.department;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.atm.*;
import ru.otus.atm.currency.Dollar;
import ru.otus.atm.currency.Rouble;
import ru.otus.department.ATMDataFacade;
import ru.otus.department.ATMDepartment;
import ru.otus.department.ExchangeRateManager;

import java.math.BigDecimal;
import java.util.List;

public class Positive {

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

    @Test
    void checkRemnants() {
        ATMDataFacade atmDataFacade = new ATMDataFacade(atmDepartment, exchangeRateManager);
        BigDecimal reserves = atmDataFacade.getReserves();
        System.out.println(reserves);
        Assertions.assertEquals(DEFAULT_ATM_AMOUNT + (1000 + 25000) + DEFAULT_ATM_AMOUNT, reserves.longValueExact());
    }

    @Test
    void saveAndRestoreState() {
        List<EntityData> initial = atmDepartment.getRawData();
        System.out.println(atmDepartment);

        secondATM.give(500L);
        List<EntityData> current = atmDepartment.getRawData();
        System.out.println(atmDepartment);
        Assertions.assertNotEquals(initial, current);

        atmDepartment.resetAll();
        List<EntityData> terminal = atmDepartment.getRawData();
        System.out.println(atmDepartment);
        Assertions.assertEquals(initial, terminal);
    }

    @Test
    void saveAndRestoreStateOfOne() {
        List<EntityData> initial = atmDepartment.getRawData();
        System.out.println(atmDepartment);

        secondATM.give(500L);
        List<EntityData> firstWithdrawal = atmDepartment.getRawData();
        System.out.println(atmDepartment);
        Assertions.assertNotEquals(initial, firstWithdrawal);

        firstATM.give(20000L);
        List<EntityData> secondWithdrawal = atmDepartment.getRawData();
        Assertions.assertNotEquals(firstWithdrawal, secondWithdrawal);

        atmDepartment.resetEntity(0L);

        List<EntityData> terminal = atmDepartment.getRawData();
        System.out.println(atmDepartment);
        Assertions.assertEquals(firstWithdrawal, terminal);
    }

    @Test
    void differentCurrency() {
        ATMDataFacade atmDataFacade = new ATMDataFacade(atmDepartment, exchangeRateManager);
        ATM dollarATM = atmDepartment.createEntity(new CassetteHolder(
                ImmutableMap.of(
                        Dollar.HUNDRED_ONE, 100,
                        Dollar.FIFTY, 50
                )
        ));

        BigDecimal reserves = atmDataFacade.getReserves();

        System.out.println(reserves);
        Long dollars = 100L * 100L + 50L * 50L;

        BigDecimal exactConvertedValue = BigDecimal.valueOf(64.1005).multiply(BigDecimal.valueOf(dollars));
        BigDecimal exactOtherAtms = BigDecimal.valueOf(DEFAULT_ATM_AMOUNT + (1000 + 25000) + DEFAULT_ATM_AMOUNT);

        Assertions.assertEquals(exactOtherAtms.add(exactConvertedValue), reserves);

    }
}
