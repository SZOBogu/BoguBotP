package pojos;

import bwapi.TechType;
import java.util.ArrayList;
import java.util.List;

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
}
