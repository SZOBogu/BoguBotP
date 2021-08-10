package bots;

import applicationContext.MyApplicationContext;
import bwapi.*;
import helpers.*;
import managers.MilitaryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import managers.BuildingManager;
import managers.DemandManager;
import managers.WorkerManager;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    private WorkerManager workerManager;
    private DemandManager demandManager;
    private BuildingManager buildingManager;
    private MilitaryManager militaryManager;

    private BuildOrder buildOrder;

    private Game game;
    private Player player;

    @Override
    public void onStart(){
        this.game = bwClient.getGame();
        this.player = game.self();
        MapHelper mapHelper = new MapHelper(game);
        this.buildingManager.setMap(mapHelper.getMap());
        this.workerManager.setMap(mapHelper.getMap());
        this.workerManager.setGame(game);
        this.workerManager.setPlayer(player);

        for(Unit unit : player.getUnits()){
            if(unit.getType().isWorker()) {
                this.workerManager.add(unit);
            }
            if(unit.getType() == UnitType.Protoss_Nexus){
                this.buildingManager.add(unit);
                this.buildingManager.trainUnit(UnitType.Protoss_Probe);
            }
        }
        this.workerManager.manage();
//        this.buildingManager.setPlayer(player);




        this.buildOrder = new BuildOrder();

        for(BuildOrderEntry entry: this.buildOrder.getBuildOrder()) {
            this.demandManager.demandCreatingUnit(entry.getUnitType());
        }
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
                    System.out.println("Pylon demanded");
                }
                else if(this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Gateway) > demandManager.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon)){
                    this.demandManager.demandCreatingUnit(UnitType.Protoss_Dragoon);
                    System.out.println("Dragoons on demand list: " + demandManager.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon));
                }
            }
        this.workerManager.manage();
        this.demandManager.manage();
    }

    public void onUnitCreate(Unit unit){
        if(this.demandManager.isOnDemandList(unit.getType())){
            //doesn't work with assimilators
            this.demandManager.fulfillDemandCreatingUnit(unit.getType());
        }
    }


    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.workerManager.add(unit);
            this.workerManager.manage();

            if(this.workerManager.getWorkerCount() > 30 * this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Nexus)){
                this.demandManager.demandCreatingUnit(UnitType.Protoss_Nexus);
                System.out.println("Nexus demanded");
            }
        }
        if(unit.getType().isBuilding() && player.getUnits().contains(unit)){
//            this.workerManager.freeBuilder();
            this.buildingManager.add(unit);
        }
        if(unit.getType() == UnitType.Protoss_Assimilator){
            this.demandManager.fulfillDemandCreatingUnit(unit.getType());
            this.workerManager.freeWorkers(3);
            this.workerManager.delegateWorkersToGatherGas(unit);
        }

        if(unit.getType() == UnitType.Protoss_Forge){
            this.demandManager.demandUpgrade(UpgradeType.Protoss_Ground_Weapons);
        }

        if(unit.getType() == UnitType.Protoss_Cybernetics_Core){
            this.demandManager.demandUpgrade(UpgradeType.Singularity_Charge);
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
            this.workerManager.handleWorkerDestruction(unit);
        }
    }

    @Autowired
    public void setWorkerManager(WorkerManager workerManager) {
        this.workerManager = workerManager;
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

    @Override
    public String toString() {
        return "Bot";
    }
}
