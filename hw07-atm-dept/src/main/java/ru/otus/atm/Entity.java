package ru.otus.atm;

public interface Entity {

    EntityData<? extends Entity> getData(Long id);

    void setState(EntityState<? super EntityConstructor> state);

    EntityState<EntityConstructor> getState();
}
