package ru.otus.atm;

public class EntityState<T extends EntityConstructor> {

    final T entityConstructor;

    EntityState(T entityConstructor) {
        this.entityConstructor = entityConstructor;
    }

    public T getEntityConstructor() {
        return entityConstructor;
    }
}
