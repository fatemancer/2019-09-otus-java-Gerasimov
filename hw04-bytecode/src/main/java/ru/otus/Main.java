package ru.otus;

import ru.otus.dynamic.ExampleFactory;

public class Main {

    public static void main(String[] args) {
        testAspectAnnotations();
        testBytecodeAnnotations();
    }

    private static void testBytecodeAnnotations() {
        ExampleInterface exampleOne = new Example();
        System.out.println("Stuff done: " + exampleOne.doStuff(1, 2, 3));
    }

    private static void testAspectAnnotations() {
        ExampleInterface exampleOne = ExampleFactory.getExample();
        ExampleInterface exampleTwo = ExampleFactory.getExample();
        System.out.println("Stuff done: " + exampleOne.doStuff(1, 2));
        System.out.println("Stuff done: " + exampleTwo.doStuff(1, 3));
    }
}
