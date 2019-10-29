package ru.otus;

import ru.otus.framework.TestFramework;

public class Main {

    public static void main(String[] args) throws Throwable {
        TestFramework.run(MainTest.class);
        TestFramework.run(SupplementaryTest.class);
        TestFramework.run(EvenMoreSupplementaryTest.class);
    }
}
