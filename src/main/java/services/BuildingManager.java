package services;

import bwapi.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuildingManager {
    List<Unit> buildings = new ArrayList<>();
    private DemandManager demandManager;

    public int countBuildingsOfType(UnitType demandedBuildingType){
        return (int)this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType().equals(demandedBuildingType)).count();
    }

    public void addBuilding(Unit unit){
        this.buildings.add(unit);
    }

    public void removeBuilding(Unit unit){
        this.buildings.remove(unit);
    }

    public List<Unit> getBuildingsOfType(UnitType buildingType){
        return this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType().equals(buildingType)).collect(Collectors.toList());
    }

    public List<Unit> getGateways(){
        return this.getBuildingsOfType(UnitType.Protoss_Gateway);
    }

    public void trainUnit(UnitType unitType){
        for (Unit unit : buildings) {
            if (!unit.getType().buildsWhat().isEmpty()  && unit.getTrainingQueue().isEmpty()) {
                if (unit.canTrain(unitType)) {
                    try {
                        unit.train(unitType);
                        this.demandManager.fulfillDemandCreatingUnit(unitType);
                    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                        assert true;    //do nothing
                    }
                }
            }
        }
    }

    public void researchTech(TechType techType){
        for (Unit unit : buildings) {
            if (unit.canResearch(techType)) {
                unit.research(techType);
                this.demandManager.fulfillDemandTech(techType);
            }
        }
    }

    public void makeUpgrade(UpgradeType upgradeType){
        for (Unit unit : buildings) {
            if (unit.canUpgrade(upgradeType)) {
                unit.upgrade(upgradeType);
                this.demandManager.fulfillDemandUpgrade(upgradeType);
            }
        }
    }

    public void handleBuildingDestruction(Unit building){
        this.removeBuilding(building);
        this.demandManager.demandCreatingUnit(building.getType());

        //TODO: order worker service to reassign workers upon destroyed assimilator
    }

    @Autowired
    public void setDemandService(DemandManager demandManager) {
        this.demandManager = demandManager;
    }

    @Override
    public String toString() {
        return "BuildingManager";
    }
}
