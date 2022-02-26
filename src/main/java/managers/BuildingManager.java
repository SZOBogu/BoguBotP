package managers;

import bwapi.*;
import helpers.CostCalculator;
import helpers.ProductionOrder;
import helpers.ProductionOrderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BuildingManager {
    private final LinkedHashSet<Unit> buildings = new LinkedHashSet<>();
    private IDemandManager demandManager;

    public void add(Unit unit){
            this.buildings.add(unit);
    }

    public void remove(Unit unit){
        this.buildings.remove(unit);
    }

    public List<Unit> getAllBuildingsOfType(UnitType buildingType){
        return this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType() == buildingType).collect(Collectors.toList());
    }

    public List<Unit> getCompletedBuildingsOfType(UnitType buildingType){
        return this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType() == buildingType).filter(Unit::isCompleted).collect(Collectors.toList());
    }

    public List<Unit> getUncompletedBuildingsOfType(UnitType buildingType){
        List<Unit> uncompletedBuildings = this.getAllBuildingsOfType(buildingType);
        List<Unit> completedBuildings = this.getCompletedBuildingsOfType(buildingType);
        uncompletedBuildings.removeAll(completedBuildings);
        return uncompletedBuildings;
    }

    public int countAllBuildingsOfType(UnitType demandedBuildingType){
        return this.getAllBuildingsOfType(demandedBuildingType).size();
    }

    public int countCompletedBuildingsOfType(UnitType demandedBuildingType){
        return this.getCompletedBuildingsOfType(demandedBuildingType).size();
    }

    public int countMilitaryProductionBuildings(){
        int count = 0;
        count += countCompletedBuildingsOfType(UnitType.Protoss_Gateway);
        count += countCompletedBuildingsOfType(UnitType.Protoss_Stargate);
        count += countCompletedBuildingsOfType(UnitType.Protoss_Robotics_Facility);
        return count;
    }

    public void trainUnit(ProductionOrder productionOrder){
        Unit buildingThatCanTrain = this.buildings.stream()
                .filter(building -> !building.getType().buildsWhat().isEmpty())
                .filter(building -> building.getTrainingQueue().isEmpty())
                .filter(building -> building.canTrain(productionOrder.getUnitType()))
                .findFirst().orElse(null);

        try{
            if(buildingThatCanTrain != null) {
                buildingThatCanTrain.train(productionOrder.getUnitType());
                this.demandManager.fulfillDemandCreatingUnit(productionOrder);
            }
        }
        catch(ArrayIndexOutOfBoundsException e){     //to catch exception when adding 6th unit to training queue
            assert true;    //do nothing
        }
    }

    public void researchTech(TechType techType){
        Unit buildingToResearchTech = this.buildings.stream().filter(i -> i.canResearch(techType)).findAny().orElse(null);
        if(buildingToResearchTech != null) {
            buildingToResearchTech.research(techType);
            this.demandManager.fulfillDemandTech(techType);
        }
    }

    public void makeUpgrade(UpgradeType upgradeType){
        Unit buildingToMakeUpgrade = this.buildings.stream().filter(i -> i.canUpgrade(upgradeType)).findAny().orElse(null);

        if(buildingToMakeUpgrade != null) {
            buildingToMakeUpgrade.upgrade(upgradeType);
            this.demandManager.fulfillDemandUpgrade(upgradeType);
        }
    }

    public void handleBuildingDestruction(Unit building){
        this.remove(building);
        ProductionOrder order = new ProductionOrder.ProductionOrderBuilder(building.getType()).build();
        this.demandManager.demandCreatingUnit(order);

        //TODO: order worker service to reassign workers upon destroyed assimilator
    }

    public LinkedHashSet<Unit> getBuildings() {
        return buildings;
    }

    @Autowired
    public void setDemandService(IDemandManager demandManager) {
        this.demandManager = demandManager;
    }

    @Override
    public String toString() {
        return "BuildingManager";
    }
}
