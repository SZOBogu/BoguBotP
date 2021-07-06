package pojos;

import bwapi.UnitType;

import java.util.ArrayList;
import java.util.List;

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
}
