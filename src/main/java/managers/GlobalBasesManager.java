package managers;

import bwapi.*;
import bwem.Base;
import exceptions.StarcraftException;
import helpers.MapHelper;
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

    //meant for searching form manager handling given nexus/assimilator
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
            BaseManager manager = this.getWorkerManagerByBase(this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition()));
            manager.setAssimilator(unit);
            manager.freeWorkers(3);
            manager.delegateWorkersToGatherGas(unit);
        }
        else if(unit.getType() == UnitType.Protoss_Probe){
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
        this.demandManager.demandCreatingUnit(UnitType.Protoss_Nexus);
        this.demandManager.demandCreatingUnit(UnitType.Protoss_Assimilator);
        this.militaryManager.tellScoutToSideStep();
    }

    public void transferProbes(BaseManager toBaseManager){
        BaseManager oversaturatedBase = this.baseManagers.stream().filter(BaseManager::isOversaturationCalled)
                .findFirst().orElse(null);

        if(oversaturatedBase != null) {
            List<Worker> workersToTransfer = oversaturatedBase.popWorkers(10);
            workersToTransfer.forEach(worker -> toBaseManager.add(worker.getWorker()));
            if(!oversaturatedBase.isOversaturated()){
                oversaturatedBase.setOversaturationCalled(false);
            }
        }
    }

    @Override
    public void manage() {
        this.baseManagers.forEach(BaseManager::manage);
    }

    public void addWorkerManager(BaseManager baseManager){
        this.baseManagers.add(baseManager);
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
}
