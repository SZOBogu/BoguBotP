package helpers;

import bwapi.UnitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildOrder {
    /*
        8/9 — Pylon
        10/17 — Gateway
        11/17 — Assimilator
        13/17 — Cybernetics Core
        15/17 — Gateway
     */
    private final List<ProductionOrder> buildOrder;

    public BuildOrder(){
        this.buildOrder = new ArrayList<>(Arrays.asList(
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(20).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(22).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Zealot).populationMark(24).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(30).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(32).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(38).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Forge).populationMark(42).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(42).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(42).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(46).build()
                ));
    }
    public List<ProductionOrder> getBuildOrder() {
        return buildOrder;
    }
}
