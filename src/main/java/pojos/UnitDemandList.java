package pojos;

import bwapi.Game;
import bwapi.UnitType;
import helpers.BuildOrder;
import helpers.ProductionOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public class UnitDemandList implements DemandList{
    private final List<ProductionOrder> demandList;

    public UnitDemandList() {
        this.demandList = new ArrayList<>();
    }

    @Override
    public void demand(Object demandedEntity) {
        ProductionOrder order = (ProductionOrder) demandedEntity;

        this.demandList.add(order);
    }

    @Override
    public void fulfillDemand(Object fulfilledDemandEntity) {
        ProductionOrder order = (ProductionOrder) fulfilledDemandEntity;
        try{
            ProductionOrder orderToRemove = this.demandList.stream().filter(o -> o.getUnitType() == order.getUnitType()).sorted().findFirst().get();
            this.demandList.remove(orderToRemove);
        }
        catch(NoSuchElementException noSuchElementException) {
            System.out.println("NoSuchElementException in unitDemandList");;
        }
    }

    public void fulfillDemandOnUnitType(Object fulfilledDemandEntity) {
        UnitType unitType = (UnitType) fulfilledDemandEntity;

        ProductionOrder order = this.demandList.stream().filter(o -> o.getUnitType() == unitType).findFirst().orElse(null);
        this.demandList.remove(order);
    }

    @Override
    public boolean isEmpty() {
        return this.demandList.isEmpty();
    }

    public Object get(int i) {
        return this.demandList.get(i);
    }

    @Override
    public List<ProductionOrder> getList() {
        return this.demandList;
    }

    @Override
    public int size() {
        return this.demandList.size();
    }

    @Override
    public boolean isOnDemandList(Object isDemanded) {
        UnitType unit = (UnitType) isDemanded;
        for(ProductionOrder order: this.demandList){
            if(order.getUnitType() == unit){
                return true;
            }
        }
        return false;
    }

    @Override
    public int howManyItemsOnDemandList(Object demandedType) {
        UnitType unitTypeDemanded = (UnitType)demandedType;
        return (int) this.demandList.stream().filter(i -> i.getUnitType() == unitTypeDemanded).count();
    }

    @Override
    public String toString() {
        return "UnitDemandList";
    }
}