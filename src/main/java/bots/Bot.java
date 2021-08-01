package bots;

import applicationContext.MyApplicationContext;
import bwapi.*;
import helpers.BuildOrder;
import helpers.BuildOrderEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import services.BuildingService;
import services.DemandService;
import services.WorkerService;

import java.util.Random;

public class Bot extends DefaultBWListener {
    @Autowired
    private BWClient bwClient;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private DemandService demandService;

    @Autowired
    private BuildingService buildingService;

    private BuildOrder buildOrder;
    private Game game;
    private Player player;

    @Override
    public void onStart(){
        this.game = bwClient.getGame();
        this.player = game.self();
        this.workerService.setGame(game);
        this.workerService.setPlayer(player);

        for(Unit unit : player.getUnits()){
            if(unit.getType().isWorker()) {
                this.workerService.addWorker(unit);
            }
            if(unit.getType() == UnitType.Protoss_Nexus){
                this.buildingService.addBuilding(unit);
                this.buildingService.trainUnit(UnitType.Protoss_Probe);
            }
        }
        this.workerService.manage();

        this.buildOrder = new BuildOrder();

        for(BuildOrderEntry entry: this.buildOrder.getBuildOrder()) {
            this.demandService.demandCreatingUnit(entry.getUnitType());
        }
    }

    @Override
    public void onFrame(){
        this.game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + " minerals");

        UnitType nextInBuildOrder = this.buildOrder.getNextThingInBuildOrder();

//        if (!buildOrder.isComplete()) {
            if(nextInBuildOrder.isBuilding()) {
                this.demandService.demandCreatingUnit(nextInBuildOrder);
            }
            else if(nextInBuildOrder.mineralPrice() < player.minerals()){
                this.buildingService.trainUnit(nextInBuildOrder);
            }
//        }

        if(this.player.getUnits().size() > 15) {
                if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400 && !this.demandService.isOnDemandList(UnitType.Protoss_Pylon)) {
                    this.demandService.demandCreatingUnit(UnitType.Protoss_Pylon);
                }
                else if(this.buildingService.countBuildingsOfType(UnitType.Protoss_Gateway) > demandService.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon)){
//                    Random random = new Random();
//                    int randResult = random.nextInt(2);
//                    if (randResult == 0 && player.minerals() > 125 && player.gas() > 25) {

                    this.demandService.demandCreatingUnit(UnitType.Protoss_Dragoon);
                    System.out.println("Dragoons on demand list: " + demandService.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon));
//                    }
//                    if (randResult == 1 && player.minerals() > 100) {
//                        this.demandService.demandCreatingUnit(UnitType.Protoss_Zealot);
//                    }
//                    if (randResult == 1 && player.minerals() > 50) {
//                        this.demandService.demandCreatingUnit(UnitType.Protoss_Probe);
//                    }
                }
//                else if(this.demandService)
            }
        this.workerService.manage();
        this.demandService.manage();
    }

    public void onUnitCreate(Unit unit){
        if(this.demandService.isOnDemandList(unit.getType())){
            //doesn't work with assimilators
            this.demandService.fulfillDemandCreatingUnit(unit.getType());
        }
    }


    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.workerService.addWorker(unit);
            this.workerService.manage();

            if(this.workerService.getWorkerCount() > 30 * this.buildingService.countBuildingsOfType(UnitType.Protoss_Nexus)){
                this.demandService.demandCreatingUnit(UnitType.Protoss_Nexus);
                System.out.println("Nexus demanded");
            }
        }
        if(unit.getType().isBuilding()){
            this.workerService.freeBuilder();
            this.buildingService.addBuilding(unit);
        }
        if(unit.getType() == UnitType.Protoss_Assimilator){
            this.demandService.fulfillDemandCreatingUnit(unit.getType());
            this.workerService.freeWorkers(3);
            this.workerService.delegateWorkersToGatherGas(unit);
        }

        if(unit.getType() == UnitType.Protoss_Forge){
            this.demandService.demandUpgrade(UpgradeType.Protoss_Ground_Weapons);
        }

        if(unit.getType() == UnitType.Protoss_Cybernetics_Core){
            this.demandService.demandUpgrade(UpgradeType.Singularity_Charge);
        }
    }

    public void onUnitDestroy(Unit unit) {
        if(unit.getType().isBuilding()){
            this.buildingService.handleBuildingDestruction(unit);
        }
        if(unit.getType().isWorker()){
            this.workerService.handleWorkerDestruction(unit);
        }
    }

    public void setWorkerService(WorkerService workerService) {
        this.workerService = workerService;
    }
}
