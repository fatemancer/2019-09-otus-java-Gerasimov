package ru.otus.specimen;

import java.util.List;

public class ClassB {

    public int thisReturns(int a, int b) {
        return a * b;
    }

    public int thisAlsoReturns(long a, int b) {
        return (int) a + b;
    }

    public List<String> calculate(int a, int b) {
        return List.of(
                String.valueOf(this.thisReturns(a, b)),
                String.valueOf(this.thisAlsoReturns(a, b))
        );
    }
}
