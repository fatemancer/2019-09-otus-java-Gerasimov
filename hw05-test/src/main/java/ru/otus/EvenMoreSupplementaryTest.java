package ru.otus;

import ru.otus.framework.Assertions;
import ru.otus.framework.annotations.AfterAll;
import ru.otus.framework.annotations.BeforeAll;
import ru.otus.framework.annotations.FrameworkTest;
import ru.otus.framework.annotations.Test;
import ru.otus.specimen.ClassC;

public class EvenMoreSupplementaryTest extends FrameworkTest {

    ClassC classC;

    @BeforeAll
    static void setUp() {
        throw new RuntimeException("Always fails");
    }

    @Test
    void classCTest() {
        Assertions.assertNotEquals(classC.returnsA(), classC.returnsB());
    }


    @AfterAll
    static void teardown() {
        System.out.println("Still runs");
    }
}
