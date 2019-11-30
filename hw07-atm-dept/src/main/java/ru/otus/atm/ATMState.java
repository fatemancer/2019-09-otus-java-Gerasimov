package ru.otus.atm;

public class ATMState extends EntityState {

    ATMState(EntityConstructor entityConstructor) {
        super(entityConstructor.copy());
    }
}
