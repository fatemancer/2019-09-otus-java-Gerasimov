package ru.otus.department;

import ru.otus.atm.Entity;
import ru.otus.atm.EntityConstructor;
import ru.otus.atm.EntityData;

import java.util.List;

// PATTERN:bridge
// Выделяем отдельно сущности Entity и возвращаемую ими информацию EntityData, позволяющую хранить
// информацию в виде строковых пар ключ-значение с дополнительным типом DataType. Так как фасад нашего класса
// извлекает только интересующую нас информацию из EntityData, эти объекты могут использоваться параллельно в любой
// другой иерархии классов, не только Entity -> ATM.

public interface EntityDepartment {

    List<EntityData> getRawData();

    void resetEntity(Long id);

    void resetAll();

    Entity getEntity(Long id);

    Entity createEntity();

    Entity createEntity(EntityConstructor entityConstructor);
}
