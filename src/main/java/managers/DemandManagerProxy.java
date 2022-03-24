package managers;

import bwapi.*;
import helpers.DemandLimitMap;
import helpers.ProductionOrder;
import pojos.TextInGame;

import java.util.List;

public class DemandManagerProxy implements IDemandManager{
    private DemandManager demandManager;
    private BuildingManager buildingManager;
    private Game game;
    @Override
    public void demandCreatingUnit(ProductionOrder order) {
        if (order.getUnitType().getRace() == Race.Protoss){
            int currentOrders = 0;
            if (order.getUnitType().isBuilding()) {
                currentOrders += buildingManager.getUncompletedBuildingsOfType(order.getUnitType()).size();
            }
            currentOrders += this.demandManager.howManyUnitsOnDemandList(order.getUnitType());

            int limit = DemandLimitMap.getLimit(order.getUnitType());

            if (currentOrders < limit)
                this.demandManager.demandCreatingUnit(order);
        }
        if(order.getUnitType() == UnitType.Protoss_Nexus){
            System.out.println("NEXUS ORDERED");
        }
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
    public void forceDemandingUnit(ProductionOrder order) {
        this.demandManager.demandCreatingUnit(order);
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

    public void setManageSupplyPopulationMark(int manageSupplyPopulationMark) {
        this.demandManager.setManageSupplyPopulationMark(manageSupplyPopulationMark);
    }

    public void setGame(Game game) {
        this.game = game;
        this.demandManager.setGame(game);
    }

    @Override
    public void manage() {
        this.demandManager.manage();
    }

    @Override
    public List<TextInGame> getTextToWriteInGame() {
        return this.demandManager.getTextToWriteInGame();
    }

    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
        this.demandManager.setBuildingManager(buildingManager);
    }

    public void setGlobalBasesManager(GlobalBasesManager globalBasesManager) {
        this.demandManager.setGlobalBasesManager(globalBasesManager);
    }

    public void setDemandManager(DemandManager demandManager) {
        this.demandManager = demandManager;
    }
}
