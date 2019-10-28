package ru.otus;

public class ExampleWithArgs implements ExampleInterface {

    private int addToAll;

    public ExampleWithArgs(Integer addToAll) {
        this.addToAll = addToAll;
    }

    @Override
    public int doStuff(int first, int second) {
        return addToAll + first + second;
    }

    @Override
    public int doStuff(int first, int second, int third) {
        return addToAll + first + second + third;
    }
}
