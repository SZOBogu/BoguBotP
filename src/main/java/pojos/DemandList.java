package pojos;

public interface DemandList<T> {
    void demand(T demandedEntity);
    void fulfillDemand(T fulfilledDemandEntity);
    boolean isEmpty();
}
