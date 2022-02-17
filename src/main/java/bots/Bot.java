package bots;

import applicationContext.MyApplicationContext;
import aspects.LoggingAspect;
import bwapi.*;
import bwem.Base;
import configs.SpringConfig;
import helpers.*;
import managers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    private DemandManager demandManager;
    private BuildingManager buildingManager;
    private MilitaryManager militaryManager;
    private GlobalBasesManager globalBasesManager;

    private MapHelper mapHelper;
    private BaseInfoTracker baseInfoTracker;

    private Game game;
    private Player player;

    @Override
    public void onStart(){
        MyApplicationContext context = new MyApplicationContext();
        context.setApplicationContext(new AnnotationConfigApplicationContext(SpringConfig.class));
        SpringConfig springConfig = (SpringConfig) MyApplicationContext.getBean("springConfig");
        springConfig.setGame(bwClient.getGame());

        this.game = bwClient.getGame();
        this.player = game.self();
        this.mapHelper = new MapHelper(game);

        this.demandManager.setGame(this.game);

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

        this.globalBasesManager.setMapHelper(this.mapHelper);
        this.globalBasesManager.setGame(game);
        this.globalBasesManager.setPlayer(player);
        this.globalBasesManager.init();

        this.militaryManager.setMapHelper(this.mapHelper);
        this.militaryManager.setGame(this.game);
        this.militaryManager.setGlobalRallyPoint();
        this.militaryManager.setAttackRallyPoint();
        this.globalBasesManager.addWorkerManager(baseManager);

        BuildOrder buildOrder = new BuildOrder();
        for(ProductionOrder entry: buildOrder.getBuildOrder()) {
            this.demandManager.demandCreatingUnit(entry);
        }

        this.militaryManager.setBaseInfoTracker(baseInfoTracker);
        this.globalBasesManager.setBaseInfoTracker(baseInfoTracker);
    }

    @Override
    public void onFrame(){
        this.game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + " minerals");

        if(this.player.getUnits().size() > 15) {
                if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400 && !this.demandManager.isOnDemandList(UnitType.Protoss_Pylon)) {
                    this.demandManager.demandCreatingUnit(ProductionOrderFactory.createPylonOrder());
                }
                else if(this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Gateway) > demandManager.howManyUnitsOnDemandList(UnitType.Protoss_Zealot)){
                    this.demandManager.demandCreatingUnit(ProductionOrderFactory.createZealotOrder());
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
            this.demandManager.fulfillDemandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(unit.getType()).build());
        }
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