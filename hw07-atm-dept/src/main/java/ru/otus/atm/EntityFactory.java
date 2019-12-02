package ru.otus.atm;

public interface EntityFactory<T extends Entity> {

    T create();

    // PATTERN:factory
    T create(EntityConstructor<T> entityConstructor);
}
