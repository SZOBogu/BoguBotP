package services;

import bwapi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//@Service
public class BuildingService {
    List<Unit> buildings = new ArrayList<>();
    private DemandService demandService;

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
                        this.demandService.fulfillDemandCreatingUnit(unitType);
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
                this.demandService.fulfillDemandTech(techType);
            }
        }
    }

    public void makeUpgrade(UpgradeType upgradeType){
        for (Unit unit : buildings) {
            if (unit.canUpgrade(upgradeType)) {
                unit.upgrade(upgradeType);
                this.demandService.fulfillDemandUpgrade(upgradeType);
            }
        }
    }

    @Autowired
    public void setDemandService(DemandService demandService) {
        this.demandService = demandService;
    }
}
