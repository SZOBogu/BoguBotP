package managers;

import bwapi.*;
import helpers.ProductionOrder;
import helpers.ProductionOrderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BuildingManager {
    private final LinkedHashSet<Unit> buildings = new LinkedHashSet<>();
    private DemandManager demandManager;

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

    public int countAllBuildingsOfType(UnitType demandedBuildingType){
        return this.getAllBuildingsOfType(demandedBuildingType).size();
    }

    public int countCompletedBuildingsOfType(UnitType demandedBuildingType){
        return this.getCompletedBuildingsOfType(demandedBuildingType).size();
    }

    public void trainUnit(UnitType unitType){
        Unit buildingThatCanTrain = this.buildings.stream()
                .filter(building -> !building.getType().buildsWhat().isEmpty())
                .filter(building -> building.getTrainingQueue().isEmpty())
                .filter(building -> building.canTrain(unitType))
                .findFirst().orElse(null);

        try{
            if(buildingThatCanTrain != null) {
                buildingThatCanTrain.train(unitType);
                this.demandManager.fulfillDemandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(unitType).build());
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
    public void setDemandService(DemandManager demandManager) {
        this.demandManager = demandManager;
    }

    @Override
    public String toString() {
        return "BuildingManager";
    }
}
