package pojos;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpgradeDemandList implements DemandList{
    private List<UpgradeType> demandList;

    public UpgradeDemandList() {
        this.demandList = new ArrayList<>();
    }

    @Override
    public void demand(Object demandedEntity) {
        this.demandList.add((UpgradeType)demandedEntity);
    }

    @Override
    public void fulfillDemand(Object fulfilledDemandEntity) {
        this.demandList.remove((UpgradeType)fulfilledDemandEntity);
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
        UpgradeType upgrade = (UpgradeType) isDemanded;
        return this.demandList.contains(upgrade);
    }

    @Override
    public int howManyItemsOnDemandList(Object demandedType) {
        List<UpgradeType> upgradesDemanded = this.demandList;
        return (int) upgradesDemanded.stream().filter(Objects::nonNull).filter(i -> i.equals(demandedType)).count();
    }
}
