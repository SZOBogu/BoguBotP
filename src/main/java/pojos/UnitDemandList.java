package pojos;

import bwapi.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnitDemandList implements DemandList{
    private List<UnitType> demandList;

    public UnitDemandList() {
        this.demandList = new ArrayList<>();
    }

    @Override
    public void demand(Object demandedEntity) {
        UnitType unitType = (UnitType) demandedEntity;

        this.demandList.add(unitType);
    }

    @Override
    public void fulfillDemand(Object fulfilledDemandEntity) {
        UnitType unitType = (UnitType) fulfilledDemandEntity;

        this.demandList.remove(unitType);
    }

    @Override
    public boolean isEmpty() {
        return this.demandList.isEmpty();
    }

    public Object get(int i) {
        return this.demandList.get(i);
    }

    @Override
    public List getList() {
        return this.demandList;
    }

    @Override
    public int size() {
        return this.demandList.size();
    }

    @Override
    public boolean isOnDemandList(Object isDemanded) {
        UnitType unit = (UnitType) isDemanded;
        return this.demandList.contains(unit);
    }

    @Override
    public int howManyItemsOnDemandList(Object demandedType) {
        List<UnitType> unitsDemanded = this.demandList;
        return (int) unitsDemanded.stream().filter(Objects::nonNull).filter(i -> i.equals(demandedType)).count();
    }
}
