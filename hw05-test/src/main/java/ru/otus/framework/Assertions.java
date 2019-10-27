package ru.otus.framework;

import java.util.List;
import java.util.function.Consumer;

public class Assertions {

    public static void assertEquals(Object a, Object b) {
        assert a.equals(b) : String.format("%s != %s", a, b);
    }

    public static void assertNotEquals(Object a, Object b) {
        assert !a.equals(b) : String.format("%s == %s", a, b);
    }

    public static void assertListRefEquals(List<?> a, List<?> b) {
        if (a.size() != b.size()) {
            assert false : String.format("Lists %s and %s are not equal", a, b);
        } else {
            for (int i = 0; i < a.size(); i++) {
                assert a.get(i).equals(b.get(i)) : String.format(
                        "List differ at position %s (%s != %s)", 
                        i, 
                        a.get(i), 
                        b.get(i)
                );
            }
        }
        assert true;
    }

    public static <T> void assertException(
            T someClass, 
            Consumer<T> thisThrows, 
            Class<? extends Exception> expected
    ) {
        try {
            thisThrows.accept(someClass);
        } catch (Exception caught) {
            assert caught.getClass().equals(expected) : String.format(
                    "Expected to throw %s but thrown %s",
                    expected,
                    caught
                    );
            return;
        }
        assert false : String.format("Expected to throw %s but nothing thrown", expected);
    }
}
