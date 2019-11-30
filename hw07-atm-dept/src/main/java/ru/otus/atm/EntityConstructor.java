package ru.otus.atm;

public interface EntityConstructor<T extends Entity> {

    EntityConstructor copy();
}
