package ru.otus;

import ru.otus.annotation.MagicAspectLogging;
import ru.otus.annotation.MagicBytecodeLogging;

public interface ExampleInterface {

    @MagicAspectLogging
    int doStuff(int first, int second);

    @MagicBytecodeLogging
    int doStuff(int first, int second, int third);
}
