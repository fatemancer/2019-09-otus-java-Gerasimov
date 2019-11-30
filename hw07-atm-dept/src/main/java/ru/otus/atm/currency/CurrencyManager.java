package ru.otus.atm.currency;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class CurrencyManager {

    static BiMap<? extends Class<? extends AbstractNote>, Currency> map = ImmutableBiMap.of(
            Dollar.class, Currency.DOLLAR,
            Rouble.class, Currency.ROUBLE
    );

    public static Currency getCurrency(Class<? extends AbstractNote> param) {
        return map.get(param);
    }

    public static Class<? extends AbstractNote> getCurrency(Currency param) {
        return map.inverse().get(param);
    }
}
