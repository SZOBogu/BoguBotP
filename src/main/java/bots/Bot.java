package bots;

import applicationContext.MyApplicationContext;
import aspects.LoggingAspect;
import bwapi.*;
import bwem.Base;
import configs.SpringConfig;
import enums.ProductionPriority;
import helpers.*;
import managers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pojos.EnemyUnitRecord;

import java.util.List;
import java.util.stream.Collectors;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    private IDemandManager demandManager;
    private BuildingManager buildingManager;
    private MilitaryManager militaryManager;
    private GlobalBasesManager globalBasesManager;
    private ScoutingManager scoutingManager;

    private MapHelper mapHelper;
    private BaseInfoTracker baseInfoTracker;

    private Game game;
    private Player player;
    private Player enemy;

    private String buildOrderName;

    @Override
    public void onStart(){
        this.game = bwClient.getGame();
        this.player = game.self();
        this.enemy = game.enemy();
        this.mapHelper = new MapHelper(game);

        DemandManager demandManager = new DemandManager();
        demandManager.setGame(this.game);
        DemandManagerProxy proxyManager = new DemandManagerProxy();
        proxyManager.setDemandManager(demandManager);
        this.demandManager = proxyManager;
        proxyManager.setBuildingManager(this.buildingManager);
        this.buildingManager.setDemandService(this.demandManager);

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
            if(unit.getType() == UnitType.Protoss_Nexus){
                this.mapHelper.setMainNexus(unit);
                baseManager.setNexus(unit);
            }
        }
        this.baseInfoTracker.init(this.mapHelper);
        this.baseInfoTracker.setGame(this.game);

        this.globalBasesManager.setMapHelper(this.mapHelper);
        this.globalBasesManager.setGame(game);
        this.globalBasesManager.setPlayer(player);
        this.globalBasesManager.init();

        this.militaryManager.setMapHelper(this.mapHelper);
        this.militaryManager.setGame(this.game);
        this.militaryManager.setGlobalRallyPoint();
        this.militaryManager.setAttackRallyPoint();
        this.militaryManager.setDemandManager(this.demandManager);
        this.globalBasesManager.addWorkerManager(baseManager);


        BuildOrder buildOrder = BuildOrderChooser.getBuildOrder();
        this.buildOrderName = buildOrder.getName();
        for(ProductionOrder entry: buildOrder.getBuildOrder()) {
            this.demandManager.demandCreatingUnit(entry);
        }
        proxyManager.setManageSupplyPopulationMark(SupplyManagementPopulationMarkGetter.getPopulationMark(buildOrder));

        this.militaryManager.setBaseInfoTracker(baseInfoTracker);
        this.globalBasesManager.setBaseInfoTracker(baseInfoTracker);
        proxyManager.setGlobalBasesManager(this.globalBasesManager);
        this.scoutingManager = new ScoutingManager();
        this.scoutingManager.setMapHelper(this.mapHelper);
        this.scoutingManager.setGame(this.game);
        this.scoutingManager.setMilitaryManager(this.militaryManager);
        this.scoutingManager.setGlobalBasesManager(this.globalBasesManager);
        this.scoutingManager.setBaseInfoTracker(this.baseInfoTracker);
    }

    @Override
    public void onFrame(){
        this.game.drawTextScreen(20, 20, "Build Order: " + this.buildOrderName);
        this.globalBasesManager.manage();
        this.demandManager.manage();
        this.militaryManager.manage();
        this.scoutingManager.manage();
    }

    @Override
    public void onUnitCreate(Unit unit){
        if(this.demandManager.isOnDemandList(unit.getType())){
            //doesn't work with assimilators
            this.demandManager.fulfillDemandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(unit.getType()).build());
        }
        /*
        if(unit.getType() == UnitType.Protoss_Pylon){
            this.buildingManager.add(unit);
            this.demandManager.fulfillDemandCreatingUnitWithType(UnitType.Protoss_Pylon);
        }
         */
    }

    @Override
    public void onUnitComplete(Unit unit){
        if(unit.getType() == UnitType.Protoss_Nexus && player.getUnits().contains(unit) && this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Nexus) == this.globalBasesManager.amountOfWorkerManagers()){
            this.globalBasesManager.assignToAppropriateWorkerService(unit);
            Base base = this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition());
            baseInfoTracker.markBaseAsMine(base);
            this.globalBasesManager.transferProbes();
        }
        if(unit.getType().isWorker()){
            this.globalBasesManager.assignToAppropriateWorkerService(unit);
        }
        if(unit.getType().isBuilding() && player.getUnits().contains(unit)){
            this.buildingManager.add(unit);
        }
        if(unit.getType() == UnitType.Protoss_Assimilator){
            this.demandManager.fulfillDemandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).build());
            this.globalBasesManager.assignToAppropriateWorkerService(unit);
        }

        if(unit.getType() == UnitType.Protoss_Forge){
            this.demandManager.demandUpgrade(UpgradeType.Protoss_Ground_Weapons);
        }
        if(unit.getType() == UnitType.Protoss_Citadel_of_Adun){
            this.demandManager.demandUpgrade(UpgradeType.Leg_Enhancements);
        }
        if(unit.getType() == UnitType.Protoss_Cybernetics_Core) {
            this.demandManager.demandUpgrade(UpgradeType.Singularity_Charge);
        }
        if(MilitaryUnitChecker.checkIfUnitIsMilitary(unit) && player.getUnits().contains(unit)){
            this.militaryManager.add(unit);
        }
        if(this.game.enemy().getUnits().contains(unit) && unit.getType().isBuilding()){
            List<Base> allBases = this.mapHelper.getBasesClosestToTilePosition(unit.getTilePosition());
            Base enemyBase = allBases.stream().filter(base -> this.baseInfoTracker.checkBaseState(base) == BaseState.UNKNOWN).collect(Collectors.toList()).get(0);
            this.baseInfoTracker.markBaseAsEnemy(enemyBase);
        }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        if(unit.getPlayer().equals(this.player)){
            this.scoutingManager.removeScout(unit);
        }
        if(unit.getType().isBuilding()){
            this.buildingManager.handleBuildingDestruction(unit);
        }
        if(unit.getType().isWorker()){
            this.globalBasesManager.handleWorkerDestruction(unit);
        }
        if(MilitaryUnitChecker.checkIfUnitIsMilitary(unit)){
            this.militaryManager.handleMilitaryDestruction(unit);
        }
        if(this.enemy.equals(unit.getPlayer())){
            EnemyUnitRecord record = new EnemyUnitRecord();
            record.setUnit(unit);
            EnemyMilitaryInfoTracker.delete(record);
        }
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        Player enemy = this.enemy;
        if(enemy.equals(unit.getPlayer()) && !unit.getType().isWorker()){
            EnemyUnitRecord record = new EnemyUnitRecord(unit, game.elapsedTime(), unit.getPosition());
            EnemyMilitaryInfoTracker.add(record);
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

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "Bot";
    }
}