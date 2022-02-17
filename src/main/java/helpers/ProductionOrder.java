package helpers;

import bwapi.UnitType;
import enums.ProductionPriority;
import managers.BaseManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProductionOrder implements Comparable<ProductionOrder>{
    private UnitType unitType;
    private int populationMark;
    private long timeMark;
    private long frameMark;
    private BaseManager baseManager;
    private ProductionPriority priority;

    public ProductionOrder(ProductionOrderBuilder builder) {
        this.unitType = builder.unitType;
        this.populationMark = builder.populationMark;
        this.timeMark = builder.timeMark;
        this.frameMark = builder.frameMark;
        this.baseManager = builder.baseManager;
        this.priority = builder.priority;
    }

    public static class ProductionOrderBuilder{
        private final UnitType unitType;
        private int populationMark;
        private int timeMark;
        private int frameMark;
        private BaseManager baseManager;
        private ProductionPriority priority;

        public ProductionOrderBuilder(UnitType unitType){
            this.unitType = unitType;
            this.populationMark = 1201;
            this.timeMark = 18000;
            this.frameMark = 450000;
            this.baseManager = null;
            this.priority = ProductionPriority.NORMAL;
        }

        public ProductionOrderBuilder populationMark(int populationMark){
            this.populationMark = populationMark;
            return this;
        }

        public ProductionOrderBuilder timeMark(int timeMark){
            this.timeMark = timeMark;
            return this;
        }

        public ProductionOrderBuilder frameMark(int frameMark){
            this.frameMark = frameMark;
            return this;
        }

        public ProductionOrderBuilder baseManager(BaseManager baseManager){
            this.baseManager = baseManager;
            return this;
        }

        public ProductionOrderBuilder priority(ProductionPriority priority){
            this.priority = priority;
            return this;
        }

        public ProductionOrder build(){
            return new ProductionOrder(this);
        }
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public int getPopulationMark() {
        return populationMark;
    }

    public void setPopulationMark(int populationMark) {
        this.populationMark = populationMark;
    }

    public long getTimeMark() {
        return timeMark;
    }

    public void setTimeMark(long timeMark) {
        this.timeMark = timeMark;
    }

    public long getFrameMark() {
        return frameMark;
    }

    public void setFrameMark(long frameMark) {
        this.frameMark = frameMark;
    }

    public BaseManager getBaseManager() {
        return baseManager;
    }

    public void setBaseManager(BaseManager baseManager) {
        this.baseManager = baseManager;
    }

    public ProductionPriority getPriority() {
        return priority;
    }

    public void setPriority(ProductionPriority priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(ProductionOrder o) {
        return this.priority.compareTo(o.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionOrder that = (ProductionOrder) o;
        return getUnitType() == that.getUnitType() && Objects.equals(getBaseManager(), that.getBaseManager()) && getPriority() == that.getPriority();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUnitType(), getBaseManager(), getPriority());
    }

    @Override
    public String toString() {
        String description = this.priority + " order for: " + unitType;

        if(this.populationMark <= 0){
            description += " on population of " + this.populationMark/2;
        }
        else if(this.timeMark <= 0){
            description += " on time of " + TimeUnit.SECONDS.toMinutes(this.timeMark);
        }
        else if(this.frameMark <= 0){
            description += " on frame of " + this.frameMark;
        }

        if(this.baseManager != null) {
            description += " for base on " + PositionPrinter.toString(baseManager.getBase());
        }
        return description;
    }
}
