package managers;

import bwapi.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuildingManager {
    List<Unit> buildings = new ArrayList<>();
    private DemandManager demandManager;

    public void addBuilding(Unit unit){
        this.buildings.add(unit);
    }

    public void removeBuilding(Unit unit){
        this.buildings.remove(unit);
    }

    public List<Unit> getGateways(){
        return this.getBuildingsOfType(UnitType.Protoss_Gateway);
    }

    public List<Unit> getStargates(){
        return this.getBuildingsOfType(UnitType.Protoss_Stargate);
    }

    public List<Unit> getRoboticsFacilities(){
        return this.getBuildingsOfType(UnitType.Protoss_Robotics_Facility);
    }

    public List<Unit> getNexuses() {
        return this.getBuildingsOfType(UnitType.Protoss_Nexus);
    }

    public List<Unit> getPylons() {
        return this.getBuildingsOfType(UnitType.Protoss_Pylon);
    }

    public List<Unit> getAssimilators(){
        return this.getBuildingsOfType(UnitType.Protoss_Assimilator);
    }

    public List<Unit> getBuildingsOfType(UnitType buildingType){
        return this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType().equals(buildingType)).collect(Collectors.toList());
    }

    public int countBuildingsOfType(UnitType demandedBuildingType){
        return (int)this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType().equals(demandedBuildingType)).count();
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

    @Autowired
    public void setDemandService(DemandManager demandManager) {
        this.demandManager = demandManager;
    }

    @Override
    public String toString() {
        return "BuildingManager";
    }
}
