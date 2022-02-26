package managers;

import bwapi.*;
import enums.ProductionPriority;
import helpers.ProductionOrder;
import helpers.ProductionOrderFactory;
import helpers.SupplyBlockDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojos.TechDemandList;
import pojos.UnitDemandList;
import pojos.UpgradeDemandList;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemandManager implements IBroodWarManager, IDemandManager {
    private final UnitDemandList unitsToCreateDemandList;
    private final UnitDemandList workerAttentionDemandList;
    private final TechDemandList techDemandList;
    private final UpgradeDemandList upgradeDemandList;

    private Game game;
    private BuildingManager buildingManager;
    private GlobalBasesManager globalBasesManager;

    private int lastUnitProducedTimestamp;
    private int lastBlockCheck;

    private int manageSupplyPopulationMark;

    public DemandManager() {
        this.unitsToCreateDemandList = new UnitDemandList();
        this.workerAttentionDemandList = new UnitDemandList();
        this.techDemandList = new TechDemandList();
        this.upgradeDemandList = new UpgradeDemandList();
        this.lastUnitProducedTimestamp = 0;
        this.lastBlockCheck = 0;
    }

    @Override
    public void demandCreatingUnit(ProductionOrder order){
        System.out.println("DemandList for units: " + this.unitsToCreateDemandList.getList());
        if(order.getUnitType().getRace() == Race.Protoss) {
            this.unitsToCreateDemandList.demand(order);
            System.out.println(order.getUnitType() + " demanded.");
        }
    }

    @Override
    public void demandUpgrade(UpgradeType upgradeType){
        if(upgradeType.getRace() == Race.Protoss)
            this.upgradeDemandList.demand(upgradeType);
    }

    @Override
    public void demandTech(TechType techType){
        if(techType.getRace() == Race.Protoss)
            this.techDemandList.demand(techType);
    }

    public void demandWorkerAttention(UnitType worker){
        this.workerAttentionDemandList.demand(worker);
    }

    @Override
    public void fulfillDemandCreatingUnit(ProductionOrder productionOrder){
        System.out.println("DemandList for units: " + this.unitsToCreateDemandList.getList());
        this.unitsToCreateDemandList.fulfillDemand(productionOrder);
        this.lastUnitProducedTimestamp = this.game.elapsedTime();
    }

    @Override
    public boolean fulfillDemandCreatingUnitWithType(UnitType unitType){
        System.out.println("DemandList for units: " + this.unitsToCreateDemandList.getList());
        List<ProductionOrder> orderedUnits = this.unitsToCreateDemandList.getList().stream().filter(o -> o.getUnitType() == unitType).sorted().collect(Collectors.toList());
        if(orderedUnits.isEmpty()){
            return false;
        }
        else{
            this.unitsToCreateDemandList.fulfillDemand(orderedUnits.get(0));
        }
        this.lastUnitProducedTimestamp = this.game.elapsedTime();
        return true;
    }

    @Override
    public void fulfillDemandUpgrade(UpgradeType upgradeType){
        this.upgradeDemandList.fulfillDemand(upgradeType);
    }

    @Override
    public void fulfillDemandTech(TechType techType){
        this.techDemandList.fulfillDemand(techType);
    }

    public void fulfillDemandWorkerAttention(UnitType worker){
        this.workerAttentionDemandList.fulfillDemand(worker);
    }

    @Override
    public boolean isOnDemandList(UnitType unitType){
        return this.unitsToCreateDemandList.isOnDemandList(unitType);
    }

    @Override
    public boolean isOnDemandList(TechType techType){
        return this.unitsToCreateDemandList.isOnDemandList(techType);
    }

    @Override
    public boolean isOnDemandList(UpgradeType upgradeType){
        return this.unitsToCreateDemandList.isOnDemandList(upgradeType);
    }

    @Override
    public UnitType getFirstDemandedUnitType(){
        return (UnitType) this.unitsToCreateDemandList.get(0);
    }

    @Override
    public int howManyUnitsOnDemandList(UnitType unitType){
        return this.unitsToCreateDemandList.howManyItemsOnDemandList(unitType);
    }

    @Override
    public UnitType getFirstBuildingDemanded(){
        ProductionOrder buildingOrder;

        for(ProductionOrder object : this.unitsToCreateDemandList.getList()){
            buildingOrder = object;
            if(buildingOrder.getUnitType().isBuilding()){
                return buildingOrder.getUnitType();
            }
        }
        return null;
    }

    @Override
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

    @Override
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

    public void manageSupply(){
        if(this.game.self().supplyUsed() > this.manageSupplyPopulationMark) {
            if (game.self().supplyTotal() - game.self().supplyUsed() <= 2 * Math.min(1, buildingManager.countMilitaryProductionBuildings()) && game.self().supplyTotal() <= 400 && !this.isOnDemandList(UnitType.Protoss_Pylon)) {
                ProductionOrder order = ProductionOrderFactory.createPylonOrder();
                order.setPriority(ProductionPriority.IMPORTANT);
                this.demandCreatingUnit(order);
            }
        }
    }

    @Override
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
        if(this.game.elapsedTime() % 10 == 0){
            manageSupply();
        }
        /*
        if(this.game.elapsedTime() - this.lastUnitProducedTimestamp > 50){
            if(SupplyBlockDetector.isSupplyBlocked(this.game, this.buildingManager)  && this.game.elapsedTime() - this.lastBlockCheck > 100){
                if(this.unitsToCreateDemandList.howManyItemsOnDemandList(UnitType.Protoss_Pylon) < this.buildingManager.countAllBuildingsOfType(UnitType.Protoss_Gateway)){
                    for(int i = 0; i < this.buildingManager.countAllBuildingsOfType(UnitType.Protoss_Gateway) - this.unitsToCreateDemandList.howManyItemsOnDemandList(UnitType.Protoss_Pylon); i++){
                        ProductionOrder order = ProductionOrderFactory.createPylonOrder();
                        order.setPriority(ProductionPriority.IMPORTANT);
                        this.unitsToCreateDemandList.getList().add(0, order);
                        System.out.println("SUPPLY BLOCK DETECTED. New Pylon orders added");
                    }
                }
                else if(this.unitsToCreateDemandList.howManyItemsOnDemandList(UnitType.Protoss_Pylon) >= this.buildingManager.countAllBuildingsOfType(UnitType.Protoss_Gateway)){
                    this.unitsToCreateDemandList.getList().stream().filter(o -> o.getUnitType() == UnitType.Protoss_Pylon).forEach(o -> o.setPriority(ProductionPriority.LOW));
                    System.out.println("SUPPLY BLOCK DETECTED. Pylon orders priority increased");
                }
                this.lastBlockCheck = this.game.elapsedTime();
            }
        }
         */
    }
    @Override
    public void setManageSupplyPopulationMark(int manageSupplyPopulationMark) {
        this.manageSupplyPopulationMark = manageSupplyPopulationMark;
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
