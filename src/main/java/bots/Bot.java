package bots;

import applicationContext.MyApplicationContext;
import bwapi.*;
import configs.SpringConfig;
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

    @Override
    public void onStart(){
        Game game = bwClient.getGame();
        Player player = game.self();

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
    }


    @Override
    public void onFrame(){
        Game game = bwClient.getGame();
        Player player = game.self();
        game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + " minerals");

        //TODO: check whether player.getUnits returns unit list of whole race or unit list player has at disposal
        for(Unit unit : player.getUnits()){
            UnitType unitType = unit.getType();
            if(unitType.isBuilding() && !unitType.buildsWhat().isEmpty()){
                UnitType unitTypeToTrain = unitType.buildsWhat().get(0);
                if(game.canMake(unitTypeToTrain, unit)){
                    unit.train(unitTypeToTrain);
                }
            }
        }
        this.workerService.manage();
    }

    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.workerService.addWorker(unit);
        }
        this.workerService.manage();
    }

    public void setBwClient(BWClient bwClient) {
        this.bwClient = bwClient;
    }

    public void setWorkerService(WorkerService workerService) {
        this.workerService = workerService;
    }
}
