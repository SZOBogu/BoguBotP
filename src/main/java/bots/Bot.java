package bots;

import applicationContext.MyApplicationContext;
import aspects.LoggingAspect;
import bwapi.*;
import bwem.Base;
import helpers.*;
import managers.*;
import org.springframework.beans.factory.annotation.Autowired;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    private DemandManager demandManager;
    private BuildingManager buildingManager;
    private MilitaryManager militaryManager;
    private GlobalBasesManager globalBasesManager;

    private BuildOrder buildOrder;
    private MapHelper mapHelper;
    private BaseInfoTracker baseInfoTracker;

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

        BaseManager baseManager = new BaseManager.WorkerManagerBuilder(
                this.player, this.game, this.mapHelper,
                this.mapHelper.getBaseClosestToTilePosition(player.getUnits().get(0).getTilePosition())
        )
                .demandManager(this.demandManager)
                .expansionManager(this.globalBasesManager)
                .build();

        for(Unit unit : player.getUnits()){
            if(unit.getType().isWorker()) {
                baseManager.add(unit);
            }
            if(unit.getType() == UnitType.Protoss_Nexus){
                this.buildingManager.add(unit);
                this.buildingManager.trainUnit(UnitType.Protoss_Probe);
                this.mapHelper.setMainNexus(unit);
            }
        }
        this.baseInfoTracker.init(this.mapHelper);

        this.militaryManager.setMapHelper(this.mapHelper);
        this.militaryManager.setGame(this.game);
        this.militaryManager.setGlobalRallyPoint();

        this.globalBasesManager.setMapHelper(this.mapHelper);
        this.globalBasesManager.setGame(game);
        this.globalBasesManager.setPlayer(player);
        this.globalBasesManager.init();
        this.globalBasesManager.addWorkerManager(baseManager);

        baseManager.setExpansionManager(this.globalBasesManager);
        baseManager.setDemandManager(this.demandManager);

        this.buildOrder = new BuildOrder();

        for(BuildOrderEntry entry: this.buildOrder.getBuildOrder()) {
            this.demandManager.demandCreatingUnit(entry.getUnitType());
        }

        this.militaryManager.setGlobalRallyPoint();
        this.militaryManager.setBaseInfoTracker(baseInfoTracker);
        this.globalBasesManager.setBaseInfoTracker(baseInfoTracker);
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
                }
                else if(this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Gateway) > demandManager.howManyUnitsOnDemandList(UnitType.Protoss_Dragoon)){
                    this.demandManager.demandCreatingUnit(UnitType.Protoss_Dragoon);
                }
            }
        this.globalBasesManager.manage();
        this.demandManager.manage();
        this.militaryManager.manage();
    }

    @Override
    public void onUnitCreate(Unit unit){
        if(this.demandManager.isOnDemandList(unit.getType())){
            //doesn't work with assimilators
            this.demandManager.fulfillDemandCreatingUnit(unit.getType());
        }
    }

    @Override
    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            this.globalBasesManager.assignToAppropriateWorkerService(unit);
        }
        if(unit.getType().isBuilding() && player.getUnits().contains(unit)){
            this.buildingManager.add(unit);
        }
        if(unit.getType() == UnitType.Protoss_Assimilator){
            this.demandManager.fulfillDemandCreatingUnit(unit.getType());
            this.globalBasesManager.assignToAppropriateWorkerService(unit);
        }

        if(unit.getType() == UnitType.Protoss_Forge){
            this.demandManager.demandUpgrade(UpgradeType.Protoss_Ground_Weapons);
        }

        if(unit.getType() == UnitType.Protoss_Cybernetics_Core){
            this.demandManager.demandUpgrade(UpgradeType.Singularity_Charge);
        }
        if(unit.getType() == UnitType.Protoss_Nexus && player.getUnits().contains(unit)){
            Base base = this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition());
            baseInfoTracker.markBaseAsMine(base);
            this.globalBasesManager.transferProbes();
        }

        if(MilitaryUnitChecker.checkIfUnitIsMilitary(unit) && player.getUnits().contains(unit)){
            this.militaryManager.add(unit);
        }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        if(unit.getType().isBuilding()){
            this.buildingManager.handleBuildingDestruction(unit);
        }
        if(unit.getType().isWorker()){
            this.globalBasesManager.handleWorkerDestruction(unit);
        }
        if(MilitaryUnitChecker.checkIfUnitIsMilitary(unit)){
            this.militaryManager.handleMilitaryDestruction(unit);
        }
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        if(unit.getType().isBuilding() && this.game.enemy().getUnits().contains(unit)){
            Base enemyBase = this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition());
            this.baseInfoTracker.markBaseAsEnemy(enemyBase);
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
    public void setExpansionManager(GlobalBasesManager globalBasesManager) {
        this.globalBasesManager = globalBasesManager;
    }
    @Autowired
    public void setBaseInfoTracker(BaseInfoTracker baseInfoTracker) {
        this.baseInfoTracker = baseInfoTracker;
    }

    @Override
    public String toString() {
        return "Bot";
    }
}
