package ru.otus;

import ru.otus.framework.Assertions;
import ru.otus.framework.annotations.Before;
import ru.otus.framework.annotations.FrameworkTest;
import ru.otus.framework.annotations.Test;
import ru.otus.specimen.ClassC;

public class SupplementaryTest extends FrameworkTest {

    ClassC classC;

    @Before
    void setUp() {
        classC = new ClassC();
    }

    @Test
    void classCTest() {
        Assertions.assertNotEquals(classC.returnsA(), classC.returnsB());
    }
}
