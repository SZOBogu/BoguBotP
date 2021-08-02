package managers;

import bwapi.*;
import enums.WorkerRole;
import org.springframework.beans.factory.annotation.Autowired;
import pojos.WorkerList;
import pojos.Worker;

import java.util.*;

public class WorkerManager implements IBroodWarManager{
    private Player player;
    private Game game;
    private final WorkerList workers;
    private Worker builder;
    private DemandManager demandManager;
    private BuildingManager buildingManager;


    public WorkerManager(){
        this.workers = new WorkerList();
    }

    public int getWorkerCount(){
        return this.workers.size();
    }

    public void addWorker(Unit unit){
        workers.add(unit);
    }

    public void removeWorker(Unit unit){
        if(unit.getType().isWorker()){
            workers.remove(unit);
        }
    }

    //TODO: reassign gas workers if one of them was killed
    public void handleWorkerDestruction(Unit unit){
        if(unit.equals(this.builder.getWorker())){
            this.builder = null;
        }
        this.removeWorker(unit);
    }

    public boolean isThereABuilder(){
        return !(this.builder == null);
    }

    private List<Worker> getIdleWorkers(){
        List<Worker> idleWorkers = new ArrayList<>();
        for(Worker workerEntity : this.workers.getWorkerList()){
            if(workerEntity.getWorker().isIdle()){
                if(workerEntity.getWorkerRole() == WorkerRole.IDLE){
                    idleWorkers.add(workerEntity);
                }
            }
        }
        return idleWorkers;
    }

    public void freeBuilder(){
        if(builder != null) {
            this.delegateWorkerToGatherMinerals(this.builder);
            this.builder = null;
        }
    }

    public Worker freeWorkerWithRole(WorkerRole role) {
        Worker worker;
        Random random = new Random();
        List<Worker> workersWithState = this.workers.getWorkersWithState(role);
        if (!workersWithState.isEmpty()) {
            worker = workersWithState.get(random.nextInt(workersWithState.size()));
            worker.setWorkerRole(WorkerRole.IDLE);
            worker.getWorker().stop();
            return worker;
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
        if(!this.builder.getWorker().isStuck()) {
            this.builder.setWorkerRole(WorkerRole.BUILDING);
            TilePosition buildLocation = game.getBuildLocation(this.demandManager.getFirstBuildingDemanded(), player.getStartLocation());
            this.builder.getWorker().build(this.demandManager.getFirstBuildingDemanded(), buildLocation);
        }
        else{
            this.delegateWorkerToGatherMinerals(this.builder);
            this.builder = null;
        }
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

    private void delegateWorkerToWork(Worker worker){
        //TODO: handling more than one assimilator
        if(builder == null && this.demandManager.areBuildingsDemanded()) {
            this.delegateWorkerToBuild();
            builder.setWorkerRole(WorkerRole.BUILDING);
            this.tryToBuild();
        }
        else if(this.buildingManager.countBuildingsOfType(UnitType.Protoss_Assimilator) > 0 &&
                this.workers.countWorkersWithState(WorkerRole.GAS_MINE) < this.buildingManager.countBuildingsOfType(UnitType.Protoss_Assimilator) * 3){
            delegateWorkersToGatherGas(this.buildingManager.getAssimilators().get(0));
        }
        else{
            delegateWorkerToGatherMinerals(worker);
        }
    }

    @Override
    public void manage() {
        List<Worker> idleWorkers = this.getIdleWorkers();

        UnitType demandedBuilding = this.demandManager.getFirstBuildingDemanded();

        if(demandedBuilding != null){
            if(this.builder != null && this.demandManager.areBuildingsDemanded() && demandedBuilding.mineralPrice() <= this.player.minerals() && demandedBuilding.gasPrice() <= this.player.gas()){
                this.tryToBuild();
            }
        }

        for(Worker worker : idleWorkers){
            this.delegateWorkerToWork(worker);
        }
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
    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }

    @Override
    public String toString() {
        return "WorkerManager";
    }
}
