package ru.otus.sequence;

public class Main {

    public static void main(String[] args) {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        SequenceHandler sequenceHandler = new SequenceHandler(1, 10, 5);
        sequenceHandler.printSequence();
    }
}
