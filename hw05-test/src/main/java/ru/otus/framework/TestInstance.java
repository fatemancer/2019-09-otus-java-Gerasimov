package ru.otus.framework;

import ru.otus.framework.annotations.FrameworkTest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

class TestInstance {

    HashMap<String, Integer> statistics;
    HashMap<String, Throwable> failedMethods;
    Class<? extends FrameworkTest> currentTestClass;

    TestInstance(Class<? extends FrameworkTest> myTestClass) {
        statistics = new HashMap<>();
        failedMethods = new HashMap<>();
        currentTestClass = myTestClass;
    }

    void run() {
        try {
            runServiceMethods("BeforeAll", null);
            runTests(currentTestClass);
        } catch (Exception e) {
            System.err.println(String.format("Fatal: %s (Cause: %s)", e.getMessage(), e.getCause()));
        } finally {
            runServiceMethods("AfterAll", null);
            prettyPrint(statistics);
        }
    }

    private void runServiceMethods(String annotationName, Object currentTestClassInstance) {
        Method[] methods = currentTestClass.getDeclaredMethods();
        Arrays.stream(methods)
                .filter(method -> hasAnnotation(method, annotationName))
                .forEach(m -> internalRunner(m, currentTestClassInstance));
    }

    private void runTests(Class<? extends FrameworkTest> myTestClass) {
        Method[] methods = myTestClass.getDeclaredMethods();
        Arrays.stream(methods)
                .filter(method -> hasAnnotation(method, "Test"))
                .forEach(m -> {
                    Object currentTestClassInstance = getInstance(myTestClass);
                    try {
                        runServiceMethods("Before", currentTestClassInstance);
                        internalTestRunner(m, currentTestClassInstance);
                    } finally {
                        runServiceMethods("After", currentTestClassInstance);
                    }
                });
    }

    private Object getInstance(Class<? extends FrameworkTest> myTestClass)  {
        try {
            return myTestClass.getDeclaredConstructor(null).newInstance(null);
        } catch (Exception e) {
            throw new IllegalStateException("Test must have public no-arg constructor", e);
        }
    }

    private void prettyPrint(HashMap<String, Integer> statistics) {
        System.out.println("\n\nSTATISTICS: \n" + "=".repeat(80));
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
                        String.format(
                                "\u001B[31m%s\u001B[0m : Failed to call method, \u001B[31mreal cause: %s\u001B[0m",
                                k,
                                v.getCause()
                        ));
            });
        }
        System.out.println("\n" + "=".repeat(80) + "\n");
    }

    private void internalRunner(Method m, Object myTestClass) {
        try {
            forceModifiers(m);
            if (Modifier.isStatic(m.getModifiers())) {
                m.invoke(currentTestClass);
            } else {
                m.invoke(myTestClass);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to preprocess/postprocess test. " +
                    "Check whether BeforeAll/AfterAll are static and Before/After are not", e);
        }
    }

    private void internalTestRunner(Method m, Object myTestClass) {
        try {
            forceModifiers(m);
            m.invoke(myTestClass);
            addSuccess();
        } catch (Exception e) {
            addFailure();
            failedMethods.put(m.getName(), e);
        }
    }

    private void forceModifiers(Method m) {
        if (!m.trySetAccessible()) {
            throw new IllegalStateException("Framework is disallowed to change test access modifiers");
        }
    }

    private boolean hasAnnotation(Method method, String annotationName) {
        return Arrays
                .stream(method.getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals(annotationName));
    }

    private void addSuccess() {
        statistics.merge("\u001B[32mSUCCESS\u001B[0m", 1, (x, y) -> x + 1);
    }

    private void addFailure() {
        statistics.merge("\u001B[31mFAILURE\u001B[0m", 1, (x, y) -> x + 1);
    }

}