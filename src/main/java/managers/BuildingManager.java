package managers;

import bwapi.*;
import bwem.BWMap;
import bwem.Base;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BuildingManager {
    private final LinkedHashSet<Unit> buildings = new LinkedHashSet<>();
    private DemandManager demandManager;
    private BWMap map;

    public void add(Unit unit){
            this.buildings.add(unit);
    }

    public void remove(Unit unit){
        this.buildings.remove(unit);
    }

    public List<Unit> getAssimilators(){
        return this.getCompletedBuildingsOfType(UnitType.Protoss_Assimilator);
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
        this.remove(building);
        this.demandManager.demandCreatingUnit(building.getType());

        //TODO: order worker service to reassign workers upon destroyed assimilator
    }

    public Base getMainBase(){
        List<Base> bases = this.map.getBases();

        //TODO: make sure it absolutely always is main nexus
        //TODO: make method that returns list of basest in order of distance to main base;
        Unit nexus = this.getCompletedBuildingsOfType(UnitType.Protoss_Nexus).get(0);

        int distance = Integer.MAX_VALUE;
        Base closestBase = null;

        for (Base tempBase : bases) {
            int tempDistance = nexus.getTilePosition().getApproxDistance(tempBase.getLocation());
            if (tempDistance < distance) {
                closestBase = tempBase;
            }
        }
        return closestBase;
    }

    public LinkedHashSet<Unit> getBuildings() {
        return buildings;
    }

//    public void setPlayer(Player player) {
//        this.player = player;
//    }

    public void setMap(BWMap map) {
        this.map = map;
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
