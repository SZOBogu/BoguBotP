package bots;

import applicationContext.MyApplicationContext;
import bwapi.*;
import helpers.BuildOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import services.WorkerService;

import java.util.Random;

public class Bot extends DefaultBWListener {
//    @Autowired
    private BWClient bwClient;

    @Autowired
    private WorkerService workerService;
    private BuildOrder buildOrder;

    private Game game;
    private Player player;

    @Override
    public void onStart(){
        this.game = bwClient.getGame();
        this.player = game.self();

        ApplicationContext staticContext = MyApplicationContext.getApplicationContext();
        WorkerService workerService = (WorkerService)staticContext.getBean("workerService");

        this.setWorkerService(workerService);
        this.workerService.setGame(game);
        this.workerService.setPlayer(player);

        for(Unit unit : player.getUnits()){
            if(unit.getType().isWorker()) {
                this.workerService.addWorker(unit);
            }
            if(unit.getType() == UnitType.Protoss_Nexus){
                this.trainUnit(UnitType.Protoss_Probe);
            }
        }
        this.workerService.manage();

        this.buildOrder = new BuildOrder();
    }


    @Override
    public void onFrame(){
        this.game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + " minerals");

        UnitType nextInBuildOrder = this.buildOrder.getNextThingInBuildOrder();

        if (!buildOrder.isComplete()) {
            if(nextInBuildOrder.isBuilding()) {
                this.workerService.demandBuilding(nextInBuildOrder);
                buildOrder.markAsBuilt();
            }
            else if(nextInBuildOrder.mineralPrice() < player.minerals()){
                this.trainUnit(nextInBuildOrder);
                this.buildOrder.markAsBuilt();
            }
        }
//        else{
            //queues too many pylons
//            if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400  && !this.workerService.isWorkerDelegatedToBuild()) {
//                this.workerService.demandBuilding(UnitType.Protoss_Pylon);
//            }
//            else{
//                Random random = new Random();
//                int randResult = random.nextInt(3);
//                if(randResult == 0 && player.minerals() > 125 && player.gas() > 25){
//                    this.trainUnit(UnitType.Protoss_Dragoon);
//                }
//                if(randResult == 1 && player.minerals() > 100){
//                    this.trainUnit(UnitType.Protoss_Zealot);
//                }
//                if(randResult == 2 && player.minerals() > 50){
//                    this.trainUnit(UnitType.Protoss_Probe);
//                }
//            }
//        }
        this.workerService.manage();
    }
    public void onUnitCreate(Unit unit){
        if(this.workerService.getBuildingsDemanded().contains(unit.getType())){
            //doesn't work with assimilators
            this.workerService.fulfillDemandOnBuilding(unit.getType());
            //this.buildOrder.markAsBuilt();
        }
    }


    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.workerService.addWorker(unit);
            this.workerService.manage();
        }
        if(unit.getType().isBuilding()){
            this.workerService.freeBuilder();
        }
        if(unit.getType() == UnitType.Protoss_Assimilator){
            this.workerService.fulfillDemandOnBuilding(unit.getType());
            //this.workerService.delegateWorkersToGatherGas(unit);
        }
    }

    public void trainUnit(UnitType unitType){
        for (Unit unit : player.getUnits()) {
            if (unit.getType().isBuilding() && !unit.getType().buildsWhat().isEmpty()) {
                if (game.canMake(unitType, unit)) {
                    try {
                        unit.train(unitType);
                    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                        game.drawTextScreen(120, 120, "6th position in queue exception");
                    }
                }
            }
        }
    }

    public void setBwClient(BWClient bwClient) {
        this.bwClient = bwClient;
    }

    public void setWorkerService(WorkerService workerService) {
        this.workerService = workerService;
    }
}
