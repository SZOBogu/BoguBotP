package bots;

import applicationContext.MyApplicationContext;
import bwapi.*;
import configs.SpringConfig;
import helpers.BuildOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import services.WorkerService;

public class Bot extends DefaultBWListener {
//    @Autowired
    private BWClient bwClient;

//    @Autowired
    private WorkerService workerService;
    private BuildOrder buildOrder;

    private Game game;
    private Player player;

    @Override
    public void onStart(){
        this.game = bwClient.getGame();
        this.player = game.self();

        System.out.print("BWClient: " + bwClient);
        System.out.print("Game: " + game);
        System.out.print("Player: " + player);

        WorkerService workerService = new WorkerService();
        workerService.setGame(game);
        workerService.setPlayer(player);

        this.setWorkerService(workerService);

        for(Unit initialWorker : player.getUnits()){
            this.workerService.addWorker(initialWorker);
        }
        this.workerService.manage();

        this.buildOrder = new BuildOrder();
    }


    @Override
    public void onFrame(){
        this.game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + " minerals");

        UnitType nextInBuildOrder = this.buildOrder.getNextThingInBuildOrder();

//        for(Unit unit : player.getUnits()){
//            UnitType unitType = unit.getType();
//            if(unitType.isBuilding() && !unitType.buildsWhat().isEmpty()){
//                UnitType unitTypeToTrain = unitType.buildsWhat().get(0);
//                if(game.canMake(unitTypeToTrain, unit)){
//                    try {
//                        unit.train(unitTypeToTrain);
//                    }
//                    catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
//                        game.drawTextScreen(120, 120, "tried to queue 6th probe");
//                    }
//                }
//            }
//        }
        if (!buildOrder.isComplete()) {
            if(nextInBuildOrder.isBuilding() && (nextInBuildOrder.mineralPrice() < player.minerals())) {
                this.workerService.demandBuilding(nextInBuildOrder);
            }
            else if(nextInBuildOrder.mineralPrice() < player.minerals()){
                this.trainUnit(nextInBuildOrder);
                this.buildOrder.markAsBuilt();
            }
        }
        else{
            if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400) {
                this.workerService.demandBuilding(UnitType.Protoss_Pylon);
            }
            else{
                this.trainUnit(UnitType.Protoss_Dragoon);
            }
        }
        this.workerService.manage();
    }
    public void onUnitCreate(Unit unit){
        if(this.workerService.getBuildingDemanded() != null && this.workerService.getBuildingDemanded().equals(unit.getType())){
            this.workerService.setBuildingDemanded(null);
            this.buildOrder.markAsBuilt();
        }
    }


    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.workerService.addWorker(unit);
            this.workerService.manage();
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
