package managers;

import bwapi.Game;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import helpers.ProductionOrder;

public interface IDemandManager extends IBroodWarManager {
    void demandCreatingUnit(ProductionOrder order);

    void demandUpgrade(UpgradeType upgradeType);

    void demandTech(TechType techType);

    void forceDemandingUnit(ProductionOrder order);

    void fulfillDemandCreatingUnit(ProductionOrder productionOrder);

    boolean fulfillDemandCreatingUnitWithType(UnitType unitType);

    void fulfillDemandUpgrade(UpgradeType upgradeType);

    void fulfillDemandTech(TechType techType);

    boolean isOnDemandList(UnitType unitType);

    boolean isOnDemandList(TechType techType);

    boolean isOnDemandList(UpgradeType upgradeType);

    UnitType getFirstDemandedUnitType();

    int howManyUnitsOnDemandList(UnitType unitType);

    UnitType getFirstBuildingDemanded();

    ProductionOrder getNextItemOnList();

    boolean areBuildingsDemanded();

    @Override
    void manage();
}
