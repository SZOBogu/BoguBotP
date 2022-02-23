package helpers;

import bwapi.Game;
import bwapi.UnitType;
import enums.ProductionPriority;
import managers.BuildingManager;
import pojos.UnitDemandList;

public class SupplyBlockDetector {
    private SupplyBlockDetector() {}

    public static boolean isSupplyBlocked(Game game, BuildingManager buildingManager){
        return game.self().supplyUsed() >= game.self().supplyTotal() && buildingManager.getUncompletedBuildingsOfType(UnitType.Protoss_Pylon).isEmpty();
    }
}
