package ru.otus;

import ru.otus.annotation.MagicAspectLogging;
import ru.otus.annotation.MagicBytecodeLogging;

public class Example implements ExampleInterface {

    @MagicAspectLogging
    @Override
    public int doStuff(int first, int second) {
        return first + second;
    }

    @MagicBytecodeLogging
    @Override
    public int doStuff(int first, int second, int third) {
        return first + second;
    }
}
