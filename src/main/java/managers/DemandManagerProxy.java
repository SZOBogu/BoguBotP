package managers;

import bwapi.Game;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import helpers.ProductionOrder;

public class DemandManagerProxy implements IDemandManager{
    private DemandManager demandManager;

    @Override
    public void demandCreatingUnit(ProductionOrder order) {
        this.demandManager.demandCreatingUnit(order);
    }

    @Override
    public void demandUpgrade(UpgradeType upgradeType) {
        this.demandManager.demandUpgrade(upgradeType);
    }

    @Override
    public void demandTech(TechType techType) {
        this.demandManager.demandTech(techType);
    }

    @Override
    public void fulfillDemandCreatingUnit(ProductionOrder productionOrder) {
        this.demandManager.fulfillDemandCreatingUnit(productionOrder);
    }

    @Override
    public boolean fulfillDemandCreatingUnitWithType(UnitType unitType) {
        return this.demandManager.fulfillDemandCreatingUnitWithType(unitType);
    }

    @Override
    public void fulfillDemandUpgrade(UpgradeType upgradeType) {
        this.demandManager.fulfillDemandUpgrade(upgradeType);
    }

    @Override
    public void fulfillDemandTech(TechType techType) {
        this.demandManager.fulfillDemandTech(techType);
    }

    @Override
    public boolean isOnDemandList(UnitType unitType) {
        return this.demandManager.isOnDemandList(unitType);
    }

    @Override
    public boolean isOnDemandList(TechType techType) {
        return this.demandManager.isOnDemandList(techType);
    }

    @Override
    public boolean isOnDemandList(UpgradeType upgradeType) {
        return this.demandManager.isOnDemandList(upgradeType);
    }

    @Override
    public UnitType getFirstDemandedUnitType() {
        return this.demandManager.getFirstDemandedUnitType();
    }

    @Override
    public int howManyUnitsOnDemandList(UnitType unitType) {
        return this.demandManager.howManyUnitsOnDemandList(unitType);
    }

    @Override
    public UnitType getFirstBuildingDemanded() {
        return this.demandManager.getFirstBuildingDemanded();
    }

    @Override
    public ProductionOrder getNextItemOnList() {
        return this.demandManager.getNextItemOnList();
    }

    @Override
    public boolean areBuildingsDemanded() {
        return this.demandManager.areBuildingsDemanded();
    }

    @Override
    public void setManageSupplyPopulationMark(int manageSupplyPopulationMark) {
        this.demandManager.setManageSupplyPopulationMark(manageSupplyPopulationMark);
    }

    @Override
    public void setGame(Game game) {
        this.demandManager.setGame(game);
    }

    @Override
    public void manage() {
        this.demandManager.manage();
    }

    @Override
    public void setBuildingManager(BuildingManager buildingManager) {
        this.demandManager.setBuildingManager(buildingManager);
    }

    @Override
    public void setGlobalBasesManager(GlobalBasesManager globalBasesManager) {
        this.demandManager.setGlobalBasesManager(globalBasesManager);
    }

    public void setDemandManager(DemandManager demandManager) {
        this.demandManager = demandManager;
    }
}
