package ru.otus.atm;

public interface EntityFactory<T extends Entity> {

    T create();

    T create(EntityConstructor<T> entityConstructor);
}
