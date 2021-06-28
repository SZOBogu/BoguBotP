package bots;

import bwapi.*;
import helpers.BuildOrder;
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

        System.out.println("BWClient: " + bwClient);
        System.out.println("Game: " + game);
        System.out.println("Player: " + player);

        WorkerService workerService = new WorkerService();
        workerService.setGame(game);
        workerService.setPlayer(player);

        this.setWorkerService(workerService);

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
        else{
            if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400  && !this.workerService.isWorkerDelegatedToBuild()) {
                this.workerService.demandBuilding(UnitType.Protoss_Pylon);
            }
            else{
                this.trainUnit(UnitType.Protoss_Dragoon);
            }
        }
        this.workerService.manage();
    }
    public void onUnitCreate(Unit unit){
        if(this.workerService.getBuildingsDemanded().contains(unit.getType())){
            this.workerService.fulfillDemandOnBuilding(unit.getType());
            //this.buildOrder.markAsBuilt();
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
