package pojos;

import bwapi.UnitType;

import java.util.List;

public interface DemandList<T> {
    void demand(T demandedEntity);
    void fulfillDemand(T fulfilledDemandEntity);
    boolean isEmpty();
    T get(int i);
    List getList();
    int size();
    boolean isOnDemandList(T isDemanded);
    int howManyItemsOnDemandList(T demandedType);
}
