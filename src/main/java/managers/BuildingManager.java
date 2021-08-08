package managers;

import bwapi.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuildingManager {
    private LinkedHashSet<Unit> buildings = new LinkedHashSet<>();
    private DemandManager demandManager;

    //TODO: weed out the bunch of bullshit in set
    public void addBuilding(Unit unit){
        if(unit.getType().isBuilding())
            this.buildings.add(unit);
    }

    public void removeBuilding(Unit unit){
        this.buildings.remove(unit);
    }

    public List<Unit> getAssimilators(){
        return this.getCompletedBuildingsOfType(UnitType.Protoss_Assimilator);
    }

    public List<Unit> getAllBuildingsOfType(UnitType buildingType){
        return this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType() == buildingType).collect(Collectors.toList());
    }

    public List<Unit> getCompletedBuildingsOfType(UnitType buildingType){
        return this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType() == buildingType).filter(i -> !i.isBeingConstructed()).collect(Collectors.toList());
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
                this.demandManager.fulfillDemandCreatingUnit(unitType);
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
        this.removeBuilding(building);
        this.demandManager.demandCreatingUnit(building.getType());

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
