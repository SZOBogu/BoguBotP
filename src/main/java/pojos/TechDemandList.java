package pojos;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TechDemandList implements DemandList{
    private List<TechType> demandList;

    public TechDemandList() {
        this.demandList = new ArrayList<>();
    }

    @Override
    public void demand(Object demandedEntity) {
        TechType techType = (TechType) demandedEntity;
        this.demandList.add(techType);
    }

    @Override
    public void fulfillDemand(Object fulfilledDemandEntity) {
        TechType techType = (TechType) fulfilledDemandEntity;
        this.demandList.remove(techType);
    }

    @Override
    public boolean isEmpty() {
        return this.demandList.isEmpty();
    }

    @Override
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
        TechType tech = (TechType) isDemanded;
        return this.demandList.contains(tech);
    }

    @Override
    public int howManyItemsOnDemandList(Object demandedType) {
        List<TechType> techsDemanded = this.demandList;
        return (int) techsDemanded.stream().filter(Objects::nonNull).filter(i -> i.equals(demandedType)).count();
    }

    @Override
    public String toString() {
        return "TechDemandList";
    }
}
