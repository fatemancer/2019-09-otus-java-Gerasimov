package ru.otus.department;

import ru.otus.atm.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ATMDepartment implements EntityDepartment {

    private final ATMFactory atmFactory;
    private final Map<Long, ATM> controlledATM = new HashMap<>();
    // PATTERN:memento
    private final Map<Long, EntityState<EntityConstructor>> controlledATMStates = new HashMap<>();
    long firstAvailableIndex = 0;

    public ATMDepartment(ATMFactory atmFactory) {
        this.atmFactory = atmFactory;
    }

    @Override
    public List<EntityData> getRawData() {
        return controlledATM.entrySet().stream()
                .map((entry) -> {
                    var idx = entry.getKey();
                    var atm = entry.getValue();
                    return atm.getData(idx);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void resetEntity(Long id) {
        EntityState<EntityConstructor> entityState = controlledATMStates.get(id);
        controlledATM.get(id).setState(entityState);
    }

    @Override
    public void resetAll() {
        for (Map.Entry<Long, ATM> entry : controlledATM.entrySet()) {
            entry.getValue().setState(
                    controlledATMStates.get(entry.getKey())
            );
        }
    }

    @Override
    public ATM getEntity(Long id) {
        return controlledATM.get(id);
    }

    @Override
    public ATM createEntity() {
        ATM atm = atmFactory.create();
        EntityState<? extends EntityConstructor> state = atm.getState();
        controlledATM.put(firstAvailableIndex, atm);
        controlledATMStates.put(firstAvailableIndex, atm.getState());
        firstAvailableIndex = firstAvailableIndex + 1;
        return atm;
    }

    @Override
    public ATM createEntity(EntityConstructor entityConstructor) {
        ATM atm = atmFactory.create(entityConstructor);
        controlledATM.put(firstAvailableIndex, atm);
        controlledATMStates.put(firstAvailableIndex, atm.getState());
        firstAvailableIndex = firstAvailableIndex + 1;
        return atm;
    }

    @Override
    public String toString() {
        return "ATMDepartment{" +
                "atmFactory=" + atmFactory +
                ", controlledATM=" + controlledATM +
                ", controlledATMStates=" + controlledATMStates +
                ", firstAvailableIndex=" + firstAvailableIndex +
                '}';
    }
}
