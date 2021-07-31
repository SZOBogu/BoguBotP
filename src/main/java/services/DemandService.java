package services;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.springframework.beans.factory.annotation.Autowired;
import pojos.TechDemandList;
import pojos.UnitDemandList;
import pojos.UpgradeDemandList;
import pojos.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

//@Service
public class DemandService implements IBroodWarManager{
    private final UnitDemandList unitsToCreateDemandList;
    private final UnitDemandList workerAttentionDemandList;
    private final TechDemandList techDemandList;
    private final UpgradeDemandList upgradeDemandList;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private BuildingService buildingService;

    public DemandService() {
        this.unitsToCreateDemandList = new UnitDemandList();
        this.workerAttentionDemandList = new UnitDemandList();
        this.techDemandList = new TechDemandList();
        this.upgradeDemandList = new UpgradeDemandList();
    }

    public void demandCreatingUnit(UnitType unit){
        this.unitsToCreateDemandList.demand(unit);
    }

    public void demandUpgrade(UpgradeType upgradeType){
        this.upgradeDemandList.demand(upgradeType);
    }

    public void demandTech(TechType techType){
        this.techDemandList.demand(techType);
    }

    public void demandWorkerAttention(UnitType worker){
        this.workerAttentionDemandList.demand(worker);
    }

    public void fulfillDemandCreatingUnit(UnitType unit){
        this.unitsToCreateDemandList.fulfillDemand(unit);
    }

    public void fulfillDemandUpgrade(UpgradeType upgradeType){
        this.upgradeDemandList.fulfillDemand(upgradeType);
    }

    public void fulfillDemandTech(TechType techType){
        this.techDemandList.fulfillDemand(techType);
    }

    public void fulfillDemandWorkerAttention(UnitType worker){
        this.workerAttentionDemandList.fulfillDemand(worker);
    }

    public UnitType getFirstBuildingDemanded(){
        UnitType building = null;

        for(Object object : this.unitsToCreateDemandList.getList()){
            building = (UnitType) object;
            if(building.isBuilding()){
                return building;
            }
        }
        return building;
    }

    public boolean areBuildingsDemanded(){
        for(Object object : this.unitsToCreateDemandList.getList()){
            UnitType building = (UnitType) object;
            if(building.isBuilding()){
                return true;
            }
        }
        return false;
    }

    public boolean isOnDemandList(UnitType unitType){
        return this.unitsToCreateDemandList.isOnDemandList(unitType);
    }

    public boolean isOnDemandList(TechType techType){
        return this.unitsToCreateDemandList.isOnDemandList(techType);
    }

    public boolean isOnDemandList(UpgradeType upgradeType){
        return this.unitsToCreateDemandList.isOnDemandList(upgradeType);
    }

    public void demandWorkersToBeAvailable(int howManyWorkersToGet){
        List<Worker> workers = this.workerService.freeWorkers(howManyWorkersToGet);

        for(Worker worker : workers){
            this.workerAttentionDemandList.fulfillDemand(worker);
        }
    }

    public int howManyUnitsOnDemandList(UnitType unitType){
        return this.unitsToCreateDemandList.howManyItemsOnDemandList(unitType);
    }

    public UnitType getFirstDemandedUnitType(){
        return (UnitType) this.unitsToCreateDemandList.get(0);
    }

    public void manage(){
        if(!this.unitsToCreateDemandList.isEmpty()){
            UnitType type = (UnitType)this.unitsToCreateDemandList.get(0);
            if(!type.isBuilding()){
                buildingService.trainUnit(type);
            }
        }
        if(!this.techDemandList.isEmpty()){
            TechType type = (TechType)this.techDemandList.get(0);
            buildingService.researchTech(type);
        }

        if(!this.upgradeDemandList.isEmpty()){
            UpgradeType type = (UpgradeType)this.upgradeDemandList.get(0);
            buildingService.makeUpgrade(type);
        }
    }
}
