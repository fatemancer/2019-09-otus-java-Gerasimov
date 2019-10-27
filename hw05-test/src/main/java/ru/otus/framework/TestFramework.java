package ru.otus.framework;

import ru.otus.framework.annotations.FrameworkTest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

public class TestFramework {

    private static HashMap<String, Integer> statistics;
    private static HashMap<String, Throwable> failedMethods;
    private static Class<? extends FrameworkTest> currentTestClass;

    public static void run(Class<? extends FrameworkTest> myTestClass) {
        init(myTestClass);
        runServiceMethods("BeforeAll", null);
        runTests(myTestClass);
        runServiceMethods("AfterAll", null);
        prettyPrint(statistics);
    }

    private static void init(Class<? extends FrameworkTest> myTestClass) {
        checkAssertions();
        statistics = new HashMap<>();
        failedMethods = new HashMap<>();
        currentTestClass = myTestClass;
    }

    private static void runServiceMethods(String annotationName, Object currentTestClassInstance) {
        Method[] methods = currentTestClass.getDeclaredMethods();
        Arrays.stream(methods)
                .filter(method -> hasAnnotation(method, annotationName))
                .forEach(m -> internalRunner(m, currentTestClassInstance));
    }

    private static void runTests(Class<? extends FrameworkTest> myTestClass) {
        Method[] methods = myTestClass.getDeclaredMethods();
        Arrays.stream(methods)
                .filter(method -> hasAnnotation(method, "Test"))
                .forEach(m -> {
                    Object currentTestClassInstance = getInstance(myTestClass);
                    runServiceMethods("Before", currentTestClassInstance);
                    internalTestRunner(m, currentTestClassInstance);
                    runServiceMethods("After", currentTestClassInstance);
                });
    }

    private static Object getInstance(Class<? extends FrameworkTest> myTestClass)  {
        try {
            return myTestClass.getDeclaredConstructor(null).newInstance(null);
        } catch (Exception e) {
            throw new IllegalStateException("Fatal: Test must have public no-arg constructor");
        }
    }

    private static void checkAssertions() {
        try {
            assert false;
        } catch (AssertionError e) {
            return;
        }
        throw new IllegalArgumentException("Fatal: tests will not work without -ea argument to VM");
    }

    private static void prettyPrint(HashMap<String, Integer> statistics) {
        System.out.println("\n==========================================");
        System.out.println(
                String.format("\u001B[1mTest cases statisics in: %s\u001B[0m", currentTestClass.getSimpleName())
        );

        statistics.forEach((k, v) -> {
            System.out.println(String.format("%s : %s", k, v));
        });

        System.out.println(
                String.format("  TOTAL : %s", statistics.values().stream().reduce(Integer::sum).orElse(0))
        );

        if (failedMethods.values().size() != 0) {
            System.out.println("\n\u001B[1mFailed cases (first fail of each case displayed): \u001B[0m");
            failedMethods.forEach((k, v) -> {
                System.out.println(
                        String.format("\u001B[31m%s\u001B[0m : Failed to call method, \u001B[31mreal cause: %s\u001B[0m",
                                k,
                                v.getCause()
                        ));
            });
        }
    }

    private static void internalRunner(Method m, Object myTestClass) {
        try {
            forceModifiers(m);
            if (Modifier.isStatic(m.getModifiers())) {
                m.invoke(currentTestClass);
            } else {
                m.invoke(myTestClass);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Fatal: Unable to preprocess/postprocess test. " +
                    "Check whether BeforeAll/AfterAll are static and Before/After are not");
        }
    }

    private static void internalTestRunner(Method m, Object myTestClass) {
        try {
            forceModifiers(m);
            m.invoke(myTestClass);
            addSuccess();
        } catch (Exception e) {
            addFailure();
            failedMethods.put(m.getName(), e);
        }
    }

    private static void forceModifiers(Method m) {
        if (!m.trySetAccessible()) {
            throw new IllegalStateException("Fatal: framework is disallowed to change test access modifiers");
        }
    }

    private static boolean hasAnnotation(Method method, String annotationName) {
        return Arrays
                .stream(method.getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals(annotationName));
    }

    private static void addSuccess() {
        statistics.merge("\u001B[32mSUCCESS\u001B[0m", 1, (x, y) -> x + 1);
    }

    private static void addFailure() {
        statistics.merge("\u001B[31mFAILURE\u001B[0m", 1, (x, y) -> x + 1);
    }
}
