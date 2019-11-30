package ru.otus.department;

import ru.otus.atm.Entity;
import ru.otus.atm.EntityConstructor;
import ru.otus.atm.EntityData;

import java.util.List;

public interface EntityDepartment {

    List<EntityData> getRawData();

    void resetEntity(Long id);

    void resetAll();

    Entity getEntity(Long id);

    Entity createEntity();

    Entity createEntity(EntityConstructor entityConstructor);
}
