package managers;

import bwapi.*;
import bwem.Base;
import enums.WorkerRole;
import exceptions.StarcraftException;
import helpers.BaseInfoTracker;
import helpers.MapHelper;
import helpers.PositionPrinter;
import helpers.ProductionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojos.Worker;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GlobalBasesManager implements IBroodWarManager{
    private MapHelper mapHelper;
    private Game game;
    private Player player;
    private LinkedHashMap<Base, Boolean> baseIsTakenMap;
    private final List<BaseManager> baseManagers = new ArrayList<>();
    private DemandManager demandManager;
    private MilitaryManager militaryManager;
    private BaseInfoTracker baseInfoTracker;

    public void init(){
        this.baseIsTakenMap = new LinkedHashMap <>();
        List<Base> baseKeys = this.mapHelper.getListOfBases();
        baseKeys.forEach(
                i -> {
                    if(i.equals(this.mapHelper.getMainBase())){
                        this.baseIsTakenMap.put(i, true);
                    }
                    else{
                        this.baseIsTakenMap.put(i, false);
                    }
                }
        );
    }

    public Base getNextNonTakenBase(){
        List<Base> bases = Arrays.stream(baseIsTakenMap.keySet().toArray()).map(i -> (Base)i).collect(Collectors.toList());

        for(Base base: bases){
            if (!this.baseIsTakenMap.get(base))
                return base;
        }
        //TODO: global exception handler
        throw new StarcraftException("No non taken bases");
    }

    //meant for searching for manager handling given nexus/assimilator
    public void assignToAppropriateWorkerService(Unit unit){
        if(unit.getType() == UnitType.Protoss_Nexus){
            BaseManager baseManager = new BaseManager.WorkerManagerBuilder(
                    this.player, this.game, this.mapHelper,
                    this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition())
            )
                    .demandManager(this.demandManager)
                    .expansionManager(this)
                    .nexus(unit)
                    .build();

            baseManagers.add(baseManager);
        }
        else if(unit.getType() == UnitType.Protoss_Assimilator) {
            //TODO: investigate NullPointerException
//            Base closestBase = this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition());
//            System.out.println("assignToAppropriateWorkerService: closestBase x: " + closestBase.getLocation().x + " y: " + closestBase.getLocation().y);
//            BaseManager manager = this.getWorkerManagerByBase(closestBase);
//            System.out.println("assignToAppropriateWorkerService: manager: " + manager);

            BaseManager manager = this.baseManagers.get(this.baseManagers.size() - 1);
            manager.setAssimilator(unit);
            manager.freeWorkers(3);
            manager.delegateWorkersToGatherGas(unit);
        }
        else if(unit.getType() == UnitType.Protoss_Probe){
//            int index = Math.max(this.baseManagers.size() - 1, 0);
//            BaseManager manager = this.baseManagers.get(index);
            BaseManager manager = this.getWorkerManagerByBase(this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition()));
            manager.add(unit);
        }
    }

    public BaseManager getWorkerManagerByBase(Base base){
        BaseManager targetManager = null;
        for(BaseManager manager : this.baseManagers){
            if(manager.getBase().equals(base)){
                targetManager = manager;
            }
        }
        return targetManager;
    }

    public void handleWorkerDestruction(Unit unit){
        for(BaseManager baseManager : this.baseManagers){
            baseManager.handleWorkerDestruction(unit);
        }
    }

    public void handleOversaturation(){
        ProductionOrder nexusOrder = new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).build();
        ProductionOrder assimilatorOrder = new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).build();

        this.demandManager.demandCreatingUnit(nexusOrder);
        this.demandManager.demandCreatingUnit(assimilatorOrder);
    }

    public void transferProbes(){
        System.out.println("Probe transfer requested");
        BaseManager oversaturatedBaseManager = this.baseManagers.stream().filter(BaseManager::isOversaturationCalled)
                .findFirst().orElse(null);
        BaseManager toBaseManager = this.baseManagers.get(this.baseManagers.size() - 1);
        System.out.println("How many base managers: " + this.baseManagers.size());

        if(oversaturatedBaseManager != null) {
            System.out.println("FROM: " + PositionPrinter.toString(oversaturatedBaseManager.getBase()));
            System.out.println("TO: " + PositionPrinter.toString(toBaseManager.getBase()));

            System.out.println("Oversaturated Base: \n" + oversaturatedBaseManager);
            System.out.println("Base accepting probes: \n" + toBaseManager);

            List<Worker> workersToTransfer = oversaturatedBaseManager.popWorkers(oversaturatedBaseManager.getAmountOfSurplusWorkers() + 2);
            toBaseManager.acceptWorkerTransfer(workersToTransfer);

            if(!oversaturatedBaseManager.isOversaturated()){
                oversaturatedBaseManager.setOversaturationCalled(false);
            }
            else{
                List<Worker> moreWorkersToTransfer = oversaturatedBaseManager.popWorkers(1);
                toBaseManager.acceptWorkerTransfer(moreWorkersToTransfer);

            }
            System.out.println("Oversaturated Base: \n" + oversaturatedBaseManager);
            System.out.println("Base accepting probes: \n" + toBaseManager);
        }
    }

    public void handleBuildingOrder(ProductionOrder order){
        BaseManager targetBase = order.getBaseManager();

        if(targetBase != null)
            targetBase.build(order);
        else
            this.baseManagers.get(0).build(order);
    }

    @Override
    public void manage() {
        this.baseManagers.forEach(BaseManager::manage);
    }

    public void addWorkerManager(BaseManager baseManager){
        this.baseManagers.add(baseManager);
    }

    public int amountOfWorkerManagers(){
        return this.baseManagers.size();
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Autowired
    public void setDemandManager(DemandManager demandManager) {
        this.demandManager = demandManager;
    }

    @Autowired
    public void setMilitaryManager(MilitaryManager militaryManager) {
        this.militaryManager = militaryManager;
    }
    @Autowired
    public void setBaseInfoTracker(BaseInfoTracker baseInfoTracker) {
        this.baseInfoTracker = baseInfoTracker;
    }
}
