package managers;

import bwapi.*;
import bwem.Base;
import exceptions.StarcraftException;
import helpers.MapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExpansionManager implements IBroodWarManager{
    private MapHelper mapHelper;
    private Game game;
    private Player player;
    private LinkedHashMap<Base, Boolean> baseIsTakenMap;
    private final List<WorkerManager> basesWorkerManagerList = new ArrayList<>();
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

            WorkerManager workerManager = new WorkerManager.WorkerManagerBuilder(
                    this.player, this.game, this.mapHelper,
                    this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition())
            )
                    .demandManager(this.demandManager)
                    .expansionManager(this)
                    .nexus(unit)
                    .build();

            basesWorkerManagerList.add(workerManager);
        }
        else if(unit.getType() == UnitType.Protoss_Assimilator) {
            //TODO: investigate NullPointerException
            WorkerManager manager = this.getWorkerManagerByBase(this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition()));
            manager.setAssimilator(unit);
            manager.freeWorkers(3);
            manager.delegateWorkersToGatherGas(unit);
        }
        else if(unit.getType() == UnitType.Protoss_Probe){
            WorkerManager manager = this.getWorkerManagerByBase(this.mapHelper.getBaseClosestToTilePosition(unit.getTilePosition()));
            manager.add(unit);
        }
    }

    private WorkerManager getWorkerManagerByBase(Base base){
        WorkerManager targetManager = null;
        for(WorkerManager manager : this.basesWorkerManagerList){
            if(manager.getBase().equals(base)){
                targetManager = manager;
            }
        }
        return targetManager;
    }

    public void handleWorkerDestruction(Unit unit){
        for(WorkerManager workerManager: this.basesWorkerManagerList){
            workerManager.handleWorkerDestruction(unit);
        }
    }

    public void handleOversaturation(){
        this.demandManager.demandCreatingUnit(UnitType.Protoss_Nexus);
        this.demandManager.demandCreatingUnit(UnitType.Protoss_Assimilator);
        this.militaryManager.tellScoutToSideStep();
    }

    @Override
    public void manage() {
        this.basesWorkerManagerList.forEach(WorkerManager::manage);
    }

    public void addWorkerManager(WorkerManager workerManager){
        this.basesWorkerManagerList.add(workerManager);
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
