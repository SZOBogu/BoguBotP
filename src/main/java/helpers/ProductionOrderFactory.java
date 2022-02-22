package helpers;

import bwapi.UnitType;
import enums.ProductionPriority;
import managers.BaseManager;

public class ProductionOrderFactory {
    public static ProductionOrder createProbeOrder(){
        return new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Probe).priority(ProductionPriority.LOW).build();
    }

    public static ProductionOrder createProbeOrder(BaseManager baseManager){
        return new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Probe).priority(ProductionPriority.LOW).baseManager(baseManager).build();
    }

    public static ProductionOrder createZealotOrder(){
        return new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Zealot).build();
    }

    public static ProductionOrder createDragoonOrder(){
        return new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).build();
    }

    public static ProductionOrder createPylonOrder(){
        return new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).build();
    }
}
