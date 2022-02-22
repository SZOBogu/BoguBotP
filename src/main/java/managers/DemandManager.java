package managers;

import bwapi.Game;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import helpers.ProductionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojos.TechDemandList;
import pojos.UnitDemandList;
import pojos.UpgradeDemandList;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemandManager implements IBroodWarManager{
    private final UnitDemandList unitsToCreateDemandList;
    private final UnitDemandList workerAttentionDemandList;
    private final TechDemandList techDemandList;
    private final UpgradeDemandList upgradeDemandList;

    private Game game;
    private BuildingManager buildingManager;
    private GlobalBasesManager globalBasesManager;

    public DemandManager() {
        this.unitsToCreateDemandList = new UnitDemandList();
        this.workerAttentionDemandList = new UnitDemandList();
        this.techDemandList = new TechDemandList();
        this.upgradeDemandList = new UpgradeDemandList();
    }

    public void demandCreatingUnit(ProductionOrder order){
        System.out.println("DemandList for units: " + this.unitsToCreateDemandList.getList());
        this.unitsToCreateDemandList.demand(order);
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

    public void fulfillDemandCreatingUnit(ProductionOrder productionOrder){
        System.out.println("DemandList for units: " + this.unitsToCreateDemandList.getList());
        this.unitsToCreateDemandList.fulfillDemand(productionOrder);
    }

    public void fulfillDemandCreatingUnit(UnitType unitType){
        System.out.println("DemandList for units: " + this.unitsToCreateDemandList.getList());
        this.unitsToCreateDemandList.fulfillDemand(unitType);
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

    public boolean isOnDemandList(UnitType unitType){
        return this.unitsToCreateDemandList.isOnDemandList(unitType);
    }

    public boolean isOnDemandList(TechType techType){
        return this.unitsToCreateDemandList.isOnDemandList(techType);
    }

    public boolean isOnDemandList(UpgradeType upgradeType){
        return this.unitsToCreateDemandList.isOnDemandList(upgradeType);
    }

    public UnitType getFirstDemandedUnitType(){
        return (UnitType) this.unitsToCreateDemandList.get(0);
    }

    public int howManyUnitsOnDemandList(UnitType unitType){
        return this.unitsToCreateDemandList.howManyItemsOnDemandList(unitType);
    }

    public UnitType getFirstBuildingDemanded(){
        ProductionOrder buildingOrder = null;

        for(ProductionOrder object : this.unitsToCreateDemandList.getList()){
            buildingOrder = object;
            if(buildingOrder.getUnitType().isBuilding()){
                return buildingOrder.getUnitType();
            }
        }
        return null;
    }

    public ProductionOrder getNextItemOnList(){
        ProductionOrder order = this.unitsToCreateDemandList.getList().stream().filter(o -> o.getFrameMark() <= this.game.getFrameCount()).findFirst().orElse(null);

        //if there are no orders to be demanded on current frame, check for orders on current time mark
        if(order == null){
            order = this.unitsToCreateDemandList.getList().stream().filter(o -> o.getTimeMark() <= this.game.elapsedTime()).findFirst().orElse(null);
        }
        //if there are no orders to be demanded on current time, check for orders on current population mark
        if(order == null){
            order = this.unitsToCreateDemandList.getList().stream().filter(o -> o.getPopulationMark() <= this.game.self().supplyUsed()).findFirst().orElse(null);
        }
        //if there are still no orders just return a first one without any marks
        if(order == null){

            List<ProductionOrder> orders = this.unitsToCreateDemandList.getList().stream()
                    .filter(o -> o.getFrameMark() == 450000)
                    .filter(o -> o.getTimeMark() == 18000)
                    .filter(o -> o.getPopulationMark() == 1201)
                    .sorted()
                    .collect(Collectors.toList());

            if(!orders.isEmpty())
                order = orders.get(0);
        }
        return order;
    }

    public boolean areBuildingsDemanded(){
        for(ProductionOrder object : this.unitsToCreateDemandList.getList()){
            if(object.getUnitType().isBuilding()){
                return true;
            }
        }
        return false;
    }

    public void tellBaseToBuild(ProductionOrder order){
        this.globalBasesManager.handleBuildingOrder(order);
    }


    public void manage(){
        if(!this.unitsToCreateDemandList.isEmpty()){
            ProductionOrder productionOrder = this.getNextItemOnList();
            if(productionOrder != null && !productionOrder.getUnitType().isBuilding()){
                buildingManager.trainUnit(productionOrder);
            }
            else if(this.getNextItemOnList() != null)
                this.tellBaseToBuild(this.getNextItemOnList());
        }
        if(!this.techDemandList.isEmpty()){
            TechType type = (TechType)this.techDemandList.get(0);
            buildingManager.researchTech(type);
        }

        if(!this.upgradeDemandList.isEmpty()){
            UpgradeType type = (UpgradeType)this.upgradeDemandList.get(0);
            buildingManager.makeUpgrade(type);
        }
    }

    @Autowired
    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }

    @Autowired
    public void setGlobalBasesManager(GlobalBasesManager globalBasesManager) {
        this.globalBasesManager = globalBasesManager;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "DemandManager";
    }
}
