package ru.otus.specimen;

public class ClassA {

    public void thisThrows() {
        throw new IllegalArgumentException("Cannot be called");
    }

    public String thisTerminatesSuccessfully() {
        return "Something";
    }
}
