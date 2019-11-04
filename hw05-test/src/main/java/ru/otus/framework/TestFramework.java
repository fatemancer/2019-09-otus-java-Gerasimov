package ru.otus.framework;

import ru.otus.framework.annotations.FrameworkTest;

public class TestFramework {

    public static void run(Class<? extends FrameworkTest> myTestClass) {
        checkAssertions();
        TestInstance testInstance = new TestInstance(myTestClass);
        testInstance.run();
    }

    private static void checkAssertions() {
        try {
            assert false;
        } catch (AssertionError e) {
            return;
        }
        throw new IllegalArgumentException("Fatal: tests will not work without -ea argument to VM");
    }

}
