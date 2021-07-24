package services;

import bwapi.*;
import enums.WorkerRole;
import helpers.CostCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import pojos.WorkerList;
import pojos.Worker;

import java.util.*;

//@Service
public class WorkerService implements IBroodWarManager{
//    @Autowired
    private Player player;
//    @Autowired
    private Game game;
    private WorkerList workers;
    private Worker builder;
    @Autowired
    private DemandService demandService;


    public WorkerService(){
        this.workers = new WorkerList();
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
        for(Worker workerEntity : this.workers.getWorkerList()){
            if(workerEntity.getWorker().isIdle()){
//                if(!(builder == null) && workerEntity.getUnit().equals(builder.getUnit()) && buildingsDemanded.isEmpty()) {
//                    builder = null;
//                    builder.setUnitState(UnitState.IDLE);
//                }
                if(workerEntity.getWorkerRole() == WorkerRole.IDLE){
                    idleWorkers.add(workerEntity);
                }
            }
        }
        return idleWorkers;
    }

    private void delegateWorkerToWork(Worker worker){
        if(builder == null && this.demandService.areBuildingsDemanded()) {
            this.delegateWorkerToBuild();
            builder.setWorkerRole(WorkerRole.BUILDING);
            this.tryToBuild();
        }
        else if(this.countRefineries() > 0 && this.workers.countWorkersWithState(WorkerRole.GAS_MINE) < this.countRefineries() * 3){
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
        this.builder.setWorkerRole(WorkerRole.BUILDING);
        TilePosition buildLocation = game.getBuildLocation(this.demandService.getFirstBuildingDemanded(), player.getStartLocation());
        this.builder.getWorker().build(this.demandService.getFirstBuildingDemanded(), buildLocation);
    }

    private void delegateWorkerToGatherMinerals(Worker worker){
        Unit closestMineralPatch = null;
        int minDistance = Integer.MAX_VALUE;
        for(Unit mineralPatch: this.game.getMinerals()){
            int tempDistance = worker.getWorker().getDistance(mineralPatch);
            if(tempDistance < minDistance){
                closestMineralPatch = mineralPatch;
                minDistance = tempDistance;
            }
        }
        worker.getWorker().gather(closestMineralPatch);
        worker.setWorkerRole(WorkerRole.MINERAL_MINE);
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

        List<Worker> mineralMiners = this.workers.getWorkersWithState(WorkerRole.MINERAL_MINE);
        if(this.workers.size() > workersLeftToAssign + 1){
            for(int i = 0; i < workersLeftToAssign; i++){
                Worker worker = mineralMiners.get(random.nextInt(mineralMiners.size()));
                mineralMiners.remove(worker);
                this.delegateWorkerToGatherGas(worker, refinery);
            }
        }
        System.out.println("Gas miners : " + this.workers.getWorkersWithState(WorkerRole.GAS_MINE));
    }

    private void delegateWorkerToGatherGas(Worker worker, Unit refinery){
        worker.getWorker().gather(refinery);
        worker.setWorkerRole(WorkerRole.GAS_MINE);
    }

    public boolean isThereABuilder(){
        return !(this.builder == null);
    }

    public void freeBuilder(){
        if(builder != null) {
            this.builder.setWorkerRole(WorkerRole.IDLE);
            this.builder = null;
        }
    }

    public Worker freeWorkerWithRole(WorkerRole role){
        Worker worker;
        Random random = new Random();
        List<Worker> workersWithState = this.workers.getWorkersWithState(role);
        if(!workersWithState.isEmpty()){
            worker = workersWithState.get(random.nextInt(workersWithState.size()));
            worker.setWorkerRole(WorkerRole.IDLE);
            return worker;
        }
        return null;
    }

    @Override
    public void manage() {
        this.freeWorkerWithRole(WorkerRole.IDLE);

        UnitType demandedBuilding = this.demandService.getFirstBuildingDemanded();

        if(demandedBuilding != null){
            if(this.builder != null && this.demandService.areBuildingsDemanded() && demandedBuilding.mineralPrice() <= this.player.minerals() && demandedBuilding.gasPrice() <= this.player.gas()){
                this.tryToBuild();
            }
        }

        for(Worker mineralMiner : this.workers.getWorkersWithState(WorkerRole.MINERAL_MINE)){
            if(!mineralMiner.getWorker().isGatheringMinerals()){
                this.delegateWorkerToGatherMinerals(mineralMiner);
            }
        }

        for(Worker gasMiner : this.workers.getWorkersWithState(WorkerRole.GAS_MINE)){
            if(!gasMiner.getWorker().isGatheringGas()){
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

    public List<Worker> freeWorkers(int howManyWorkersToFree){
        List<Worker> workers = new ArrayList<>();
        for(int i = 0; i < howManyWorkersToFree; i++){
            workers.add(this.makeWorkerAvailable());
        }
        return workers;
    }

    public Worker makeWorkerAvailable(){
        if(this.freeWorkerWithRole(WorkerRole.IDLE) != null){
            return this.freeWorkerWithRole(WorkerRole.IDLE);
        }
        else if(this.freeWorkerWithRole(WorkerRole.BUILDING) != null) {
            this.freeBuilder();
            return this.freeWorkerWithRole(WorkerRole.BUILDING);
        }
        else if(this.freeWorkerWithRole(WorkerRole.MINERAL_MINE) != null) {
            return this.freeWorkerWithRole(WorkerRole.MINERAL_MINE);
        }
        else if(this.freeWorkerWithRole(WorkerRole.GAS_MINE) != null) {
            return this.freeWorkerWithRole(WorkerRole.GAS_MINE);
        }
        else if(this.freeWorkerWithRole(WorkerRole.FIGHT) != null) {
            return this.freeWorkerWithRole(WorkerRole.FIGHT);
        }
        else if(this.freeWorkerWithRole(WorkerRole.OTHER) != null) {
            return this.freeWorkerWithRole(WorkerRole.OTHER);
        }
        else if(this.freeWorkerWithRole(WorkerRole.SCOUT) != null) {
            return this.freeWorkerWithRole(WorkerRole.SCOUT);
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
