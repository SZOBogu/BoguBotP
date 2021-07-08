package services;

import bwapi.*;
import enums.WorkerRole;
import org.springframework.beans.factory.annotation.Autowired;
import pojos.UnitList;
import pojos.Worker;

import java.util.*;

//@Service
public class WorkerService implements IBroodWarManager{
//    @Autowired
    private Player player;
//    @Autowired
    private Game game;
    private UnitList workers;
    private Worker builder;
    @Autowired
    private DemandService demandService;


    public WorkerService(){
        this.workers = new UnitList();
    }

    public void addWorker(Unit unit){
        workers.add(unit);
    }

    public void removeWorker(Unit unit){
        if(unit.getType().isWorker()){
            workers.remove(unit);
        }
    }

    private List<Worker> getIdleWorkers(){
        List<Worker> idleWorkers = new ArrayList<>();
        for(Worker workerEntity : this.workers.getUnitList()){
            if(workerEntity.getUnit().isIdle()){
//                if(!(builder == null) && workerEntity.getUnit().equals(builder.getUnit()) && buildingsDemanded.isEmpty()) {
//                    builder = null;
//                    builder.setUnitState(UnitState.IDLE);
//                }
                if(workerEntity.getUnitState() == WorkerRole.IDLE){
                    idleWorkers.add(workerEntity);
                }
            }
        }
        return idleWorkers;
    }

    private void delegateWorkerToWork(Worker worker){
        if(builder == null && this.demandService.areBuildingsDemanded()) {
            this.delegateWorkerToBuild();
            builder.setUnitState(WorkerRole.BUILDING);
            this.tryToBuild();
        }
        else if(this.countRefineries() > 0 && this.workers.countUnitsWithState(WorkerRole.GAS_MINE) < this.countRefineries() * 3){
            delegateWorkersToGatherGas(this.getRefinery());
        }
        else{
            delegateWorkerToGatherMinerals(worker);
        }
    }

    private void delegateWorkerToBuild(){
        List<Worker> idleWorkers = this.getIdleWorkers();
        Worker worker;

        if(idleWorkers.isEmpty()) {
            Random random = new Random();
            worker = this.workers.get(random.nextInt(this.workers.size()));
        }
        else{
            worker = idleWorkers.get(0);
        }
        if (this.builder == null){
            builder = worker;
        }
        System.out.println("New builder chosen and assigned to work: " + builder);
    }

    private void tryToBuild(){
        this.builder.setUnitState(WorkerRole.BUILDING);
        TilePosition buildLocation = game.getBuildLocation(this.demandService.getFirstBuildingDemanded(), player.getStartLocation());
        this.builder.getUnit().build(this.demandService.getFirstBuildingDemanded(), buildLocation);
    }

    private void delegateWorkerToGatherMinerals(Worker worker){
        Unit closestMineralPatch = null;
        int minDistance = Integer.MAX_VALUE;
        for(Unit mineralPatch: this.game.getMinerals()){
            int tempDistance = worker.getUnit().getDistance(mineralPatch);
            if(tempDistance < minDistance){
                closestMineralPatch = mineralPatch;
                minDistance = tempDistance;
            }
        }
        worker.getUnit().gather(closestMineralPatch);
        worker.setUnitState(WorkerRole.MINERAL_MINE);
    }

    public void delegateWorkersToGatherGas(Unit refinery){
        List<Worker> idleWorkers = this.getIdleWorkers();
        Random random = new Random();
        int workersLeftToAssign = 3;

        for(Worker workerEntry : idleWorkers){
            if(workersLeftToAssign > 0) {
                this.delegateWorkerToGatherGas(workerEntry, refinery);
                workersLeftToAssign--;
            }
            else
                break;
        }

        List<Worker> mineralMiners = this.workers.getUnitsWithState(WorkerRole.MINERAL_MINE);
        if(this.workers.size() > workersLeftToAssign + 1){
            for(int i = 0; i < workersLeftToAssign; i++){
                Worker worker = mineralMiners.get(random.nextInt(mineralMiners.size()));
                mineralMiners.remove(worker);
                this.delegateWorkerToGatherGas(worker, refinery);
            }
        }
        System.out.println("Gas miners : " + this.workers.getUnitsWithState(WorkerRole.GAS_MINE));
    }

    private void delegateWorkerToGatherGas(Worker worker, Unit refinery){
        worker.getUnit().gather(refinery);
        worker.setUnitState(WorkerRole.GAS_MINE);
    }

    public boolean isThereABuilder(){
        return !(this.builder == null);
    }

    public void freeBuilder(){
        if(builder != null) {
            this.builder.setUnitState(WorkerRole.IDLE);
            this.builder = null;
        }
    }

    @Override
    public void manage() {
        List<Worker> idleWorkers = this.getIdleWorkers();
        for(Worker idleWorker : idleWorkers){
            delegateWorkerToWork(idleWorker);
        }

        UnitType demandedBuilding = this.demandService.getFirstBuildingDemanded();

        if(demandedBuilding != null){
            if(this.builder != null && this.demandService.areBuildingsDemanded() && demandedBuilding.mineralPrice() <= this.player.minerals() && demandedBuilding.gasPrice() <= this.player.gas()){
                this.tryToBuild();
            }
        }

        for(Worker mineralMiner : this.workers.getUnitsWithState(WorkerRole.MINERAL_MINE)){
            if(!mineralMiner.getUnit().isGatheringMinerals()){
                this.delegateWorkerToGatherMinerals(mineralMiner);
            }
        }

        for(Worker gasMiner : this.workers.getUnitsWithState(WorkerRole.GAS_MINE)){
            if(!gasMiner.getUnit().isGatheringGas()){
                delegateWorkerToGatherGas(gasMiner, getRefinery());
            }
        }
    }

    public int countRefineries(){
        int count = 0;
        for(Unit unit : this.player.getUnits()){
            if(unit.getType().isRefinery() && unit.exists()){
                count++;
            }
        }
        return count;
    }

    public Unit getRefinery(){
        for(Unit unit : this.player.getUnits()){
            if(unit.getType().isRefinery()){
                return unit;
            }
        }
        return null;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
