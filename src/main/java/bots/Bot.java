package bots;

import applicationContext.MyApplicationContext;
import aspects.LoggingAspect;
import bwapi.*;
import bwem.Base;
import helpers.*;
import managers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

//    private WorkerManager workerManager;
    private DemandManager demandManager;
    private BuildingManager buildingManager;
    private MilitaryManager militaryManager;
    private ExpansionManager expansionManager;

    private BuildOrder buildOrder;
    private MapHelper mapHelper;

    private Game game;
    private Player player;

    @Override
    public void onStart(){
        this.game = bwClient.getGame();
        this.player = game.self();
        this.mapHelper = new MapHelper(game);

        LoggingAspect loggingAspect = (LoggingAspect)MyApplicationContext.getBean("loggingAspect");
        loggingAspect.setGame(this.game);
        loggingAspect.setPlayer(this.player);

        WorkerManager workerManager = new WorkerManager.WorkerManagerBuilder(
                this.player, this.game, this.mapHelper,
                this.mapHelper.getBaseClosestToTilePosition(player.getUnits().get(0).getTilePosition())
        )
                .demandManager(this.demandManager)
                .expansionManager(this.expansionManager)
                .build();

        for(Unit unit : player.getUnits()){
            if(unit.getType().isWorker()) {
                workerManager.add(unit);
            }
            if(unit.getType() == UnitType.Protoss_Nexus){
                this.buildingManager.add(unit);
                this.buildingManager.trainUnit(UnitType.Protoss_Probe);
                this.mapHelper.setMainNexus(unit);
            }
        }
        this.militaryManager.setMapHelper(this.mapHelper);

        this.expansionManager.setMapHelper(this.mapHelper);
        this.expansionManager.setGame(game);
        this.expansionManager.setPlayer(player);
        this.expansionManager.init();
        this.expansionManager.addWorkerManager(workerManager);

        workerManager.setExpansionManager(this.expansionManager);
        workerManager.setDemandManager(this.demandManager);

        this.buildOrder = new BuildOrder();

        for(BuildOrderEntry entry: this.buildOrder.getBuildOrder()) {
            this.demandManager.demandCreatingUnit(entry.getUnitType());
        }

        this.militaryManager.setGlobalRallyPoint();
    }

    @Override
    public void onFrame(){
        this.game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + " minerals");

        UnitType nextInBuildOrder = this.buildOrder.getNextThingInBuildOrder();

        if(nextInBuildOrder.isBuilding()) {
            this.demandManager.demandCreatingUnit(nextInBuildOrder);
        }
        else if(CostCalculator.canAfford(player, nextInBuildOrder)){
            this.buildingManager.trainUnit(nextInBuildOrder);
        }

        if(this.player.getUnits().size() > 15) {
                if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400 && !this.demandManager.isOnDemandList(UnitType.Protoss_Pylon)) {
                    this.demandManager.demandCreatingUnit(UnitType.Protoss_Pylon);
//                    System.out.println("Pylon demanded");
                }
                else if(this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Gateway) > demandManager.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon)){
                    this.demandManager.demandCreatingUnit(UnitType.Protoss_Dragoon);
//                    System.out.println("Dragoons on demand list: " + demandManager.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon));
                }
            }
        this.expansionManager.manage();
        this.demandManager.manage();
        this.militaryManager.manage();

//        if(this.game.elapsedTime() == 330){
//            this.militaryManager.tellScoutToGetToNextBase();
//        }
    }

    public void onUnitCreate(Unit unit){
        if(this.demandManager.isOnDemandList(unit.getType())){
            //doesn't work with assimilators
            this.demandManager.fulfillDemandCreatingUnit(unit.getType());
        }
    }


    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.expansionManager.assignToAppropriateWorkerService(unit);
        }
        if(unit.getType().isBuilding() && player.getUnits().contains(unit)){
//            this.workerManager.freeBuilder();
            this.buildingManager.add(unit);
        }
        if(unit.getType() == UnitType.Protoss_Assimilator){
            this.demandManager.fulfillDemandCreatingUnit(unit.getType());
            this.expansionManager.assignToAppropriateWorkerService(unit);
        }

        if(unit.getType() == UnitType.Protoss_Forge){
            this.demandManager.demandUpgrade(UpgradeType.Protoss_Ground_Weapons);
        }

        if(unit.getType() == UnitType.Protoss_Cybernetics_Core){
            this.demandManager.demandUpgrade(UpgradeType.Singularity_Charge);
        }
        if(unit.getType() == UnitType.Protoss_Nexus){
//            this.demandManager.demandCreatingUnit(UnitType.Protoss_Assimilator);
        }

        if(MilitaryUnitChecker.checkIfUnitIsMilitary(unit) && player.getUnits().contains(unit)){
            this.militaryManager.add(unit);
        }
    }

    public void onUnitDestroy(Unit unit) {
        if(unit.getType().isBuilding()){
            this.buildingManager.handleBuildingDestruction(unit);
        }
        if(unit.getType().isWorker()){
            this.expansionManager.handleWorkerDestruction(unit);
        }
        if(MilitaryUnitChecker.checkIfUnitIsMilitary(unit)){
            this.militaryManager.remove(unit);
        }
    }

    @Autowired
    public void setBwClient(BWClient bwClient) {
        this.bwClient = bwClient;
    }
    @Autowired
    public void setDemandManager(DemandManager demandManager) {
        this.demandManager = demandManager;
    }
    @Autowired
    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }
    @Autowired
    public void setMilitaryManager(MilitaryManager militaryManager) {
        this.militaryManager = militaryManager;
    }
    @Autowired
    public void setExpansionManager(ExpansionManager expansionManager) {
        this.expansionManager = expansionManager;
    }

    @Override
    public String toString() {
        return "Bot";
    }
}
