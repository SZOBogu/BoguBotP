package helpers;

import bwapi.UnitType;

import java.util.List;
import java.util.stream.Collectors;

public class SupplyManagementPopulationMarkGetter {
    private SupplyManagementPopulationMarkGetter() {}

    public static int getPopulationMark(BuildOrder buildOrder){
        List<ProductionOrder> pylonProductionOrders = buildOrder.getBuildOrder().stream().filter(e -> e.getUnitType() == UnitType.Protoss_Pylon).collect(Collectors.toList());
        int mark = 0;

        for(ProductionOrder order : pylonProductionOrders){
            if(mark < order.getPopulationMark() && order.getPopulationMark() != 1201){
                mark = order.getPopulationMark();
            }
        }

        return mark;
    }
}
