package ru.otus;

import ru.otus.dynamic.MagicLoggingInjectedFactory;

public class Main {

    public static void main(String[] args) {
        testAspectAnnotations();
        testBytecodeAnnotations();
    }

    private static void testBytecodeAnnotations() {
        System.out.println("Bytecode annotations: ");
        ExampleInterface exampleOne = new Example();
        System.out.println("Stuff done: " + exampleOne.doStuff(1, 2, 3));
    }

    private static void testAspectAnnotations() {
        System.out.println("Aspect annotations: ");
        ExampleInterface exampleOne = MagicLoggingInjectedFactory.getInstance(Example.class, ExampleInterface.class);
        System.out.println("Stuff done: " + exampleOne.doStuff(1, 2));
        System.out.println(MagicLoggingInjectedFactory.getInstance(Example.class, ExampleInterface.class).doStuff(3, 4));

        ExampleInterface withArguments = MagicLoggingInjectedFactory.getInstance(
                ExampleWithArgs.class,
                ExampleInterface.class,
                90);
        System.out.println(withArguments.doStuff(5,5));
    }
}
