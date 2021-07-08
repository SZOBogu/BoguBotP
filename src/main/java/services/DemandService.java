package services;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import org.springframework.beans.factory.annotation.Autowired;
import pojos.TechDemandList;
import pojos.UnitDemandList;
import pojos.UpgradeDemandList;

//@Service
public class DemandService {
    private UnitDemandList unitsToCreateDemandList;
    private UnitDemandList workerAttentionDemandList;
    private TechDemandList techDemandList;
    private UpgradeDemandList upgradeDemandList;

    @Autowired
    private WorkerService workerService;

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
}
