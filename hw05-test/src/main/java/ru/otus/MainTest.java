package ru.otus;

import com.google.common.collect.ImmutableList;
import ru.otus.framework.Assertions;
import ru.otus.framework.annotations.After;
import ru.otus.framework.annotations.AfterAll;
import ru.otus.framework.annotations.Before;
import ru.otus.framework.annotations.BeforeAll;
import ru.otus.framework.annotations.FrameworkTest;
import ru.otus.framework.annotations.Test;
import ru.otus.specimen.ClassA;
import ru.otus.specimen.ClassB;
import ru.otus.specimen.DatabaseLikeClass;

public class MainTest extends FrameworkTest {

    private ClassA classA;
    private ClassB classB;
    private static DatabaseLikeClass databaseLikeClass;

    @BeforeAll
    public static void beforeAll() {
        databaseLikeClass = new DatabaseLikeClass();
    }

    @Before
    public void setUpA() {
        classA = new ClassA();
    }

    @Before
    public void setUpB() {
        classB = new ClassB();
    }

    @Test
    public void testClassA() {
        Assertions.assertException(classA, ClassA::thisThrows, IllegalArgumentException.class);
        Assertions.assertEquals(classA.thisTerminatesSuccessfully(), "Something");
        databaseLikeClass.put(classA.thisTerminatesSuccessfully());
        Assertions.assertListRefEquals(databaseLikeClass.read(), ImmutableList.of("Something"));
        classB = null;
    }

    @Test
    public void testClassB() {
        databaseLikeClass.put(classB.calculate(1, 4));
        Assertions.assertEquals(classB.thisReturns(6, 2), 12);
    }

    @Test
    public void testDbIsCleared() {
        databaseLikeClass.reset();
        Assertions.assertEquals(databaseLikeClass.size(), 0);
    }

    @Test
    public void aWrongAssumption() {
        Assertions.assertEquals(
                classB.thisAlsoReturns(Integer.MAX_VALUE, Integer.MAX_VALUE),
                2L * Integer.MAX_VALUE);
    }

    @Test void someOtherWrongAssumption() {
        Assertions.assertException(classA, ClassA::thisTerminatesSuccessfully, IllegalArgumentException.class);
    }

    @After
    public void tearDown() {
        System.out.println("Current db contents: " + databaseLikeClass.read());
    }

    @AfterAll
    public static void tearDownAll() {
        System.out.println("All test cases done. Framework will now print statistics");
    }
}
