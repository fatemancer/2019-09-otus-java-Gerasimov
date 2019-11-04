package ru.otus;

import ru.otus.framework.annotations.After;
import ru.otus.framework.annotations.AfterAll;
import ru.otus.framework.annotations.FrameworkTest;
import ru.otus.framework.annotations.Test;

public class EvenMoreSupplementaryTestV2 extends FrameworkTest {

    @Test
    void classCTest() {
        throw new RuntimeException("Always fails");
    }

    @After
    void classCAfter() {
        System.out.println("Still runs after each");
    }

    @AfterAll
    static void teardown() {
        System.out.println("Still runs after all");
    }
}
