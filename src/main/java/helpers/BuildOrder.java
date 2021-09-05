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
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(8).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(10).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(11).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(15).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(19).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Forge).populationMark(21).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(23).build()
                ));
    }
    public List<ProductionOrder> getBuildOrder() {
        return buildOrder;
    }
}
