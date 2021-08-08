package managers;

import bwapi.*;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import enums.WorkerRole;
import helpers.CostCalculator;
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
    //TODO: throws NullPointerException
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
                    idleWorkers.add(workerEntity);
            }
        }
        return idleWorkers;
    }

    public void freeBuilder(){
        if(builder != null) {
            if(this.builder.getWorkerRole() == WorkerRole.GAS_MINE && !this.areThereEnoughGasMiners()){
                this.delegateWorkerToGatherGas(builder, this.buildingManager.getAssimilators().get(0));
            }
            else{
                this.delegateWorkerToGatherMinerals(builder);
            }

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
        System.out.println("Worker delegated to minerals");
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
        System.out.println("Worker delegated to gas");
    }

    private void delegateWorkerToBuild(){
        List<Worker> idleWorkers = this.getIdleWorkers();
        Worker worker;

        List<Worker> mineralMiners = this.workers.getWorkersWithState(WorkerRole.MINERAL_MINE);
        List<Worker> gasMiners = this.workers.getWorkersWithState(WorkerRole.MINERAL_MINE);

        if(idleWorkers.isEmpty()) {
            Random random = new Random();
            if(!mineralMiners.isEmpty())
                worker = mineralMiners.get(random.nextInt(mineralMiners.size()));
            else if(!gasMiners.isEmpty())
                worker = gasMiners.get(random.nextInt(gasMiners.size()));
            else
                worker = workers.get(random.nextInt(this.workers.size()));
        }
        else{
            worker = idleWorkers.get(0);
        }
        if (this.builder == null){
            builder = worker;
//            this.builder.setWorkerRole(WorkerRole.BUILDING);
        }
        System.out.println("New builder chosen and assigned to work: " + builder);
    }

    private void tryToBuild(){
        if(!this.builder.getWorker().isStuck()) {
            TilePosition buildLocation = game.getBuildLocation(this.demandManager.getFirstBuildingDemanded(), player.getStartLocation());
            this.builder.getWorker().build(this.demandManager.getFirstBuildingDemanded(), buildLocation);
        }
        else{
            this.delegateWorkerToGatherMinerals(this.builder);
            this.builder = null;
        }
    }

    private void delegateWorkerToWork(Worker worker){
        //TODO: handling more than one assimilator
        if(builder == null && this.demandManager.areBuildingsDemanded()) {
            this.delegateWorkerToBuild();
//            builder.setWorkerRole(WorkerRole.BUILDING);
            this.tryToBuild();
        }
        else if(this.buildingManager.countCompletedBuildingsOfType(UnitType.Protoss_Assimilator) > 0 &&
                !this.areThereEnoughGasMiners()){
            delegateWorkerToGatherGas(worker, this.buildingManager.getAssimilators().get(0));
        }
        else{
            delegateWorkerToGatherMinerals(worker);
        }
    }

    private boolean areThereEnoughGasMiners(){
        return this.workers.getWorkersWithState(WorkerRole.GAS_MINE).size() >= this.buildingManager.getAssimilators().size() * 3;
    }

    @Override
    public void manage() {
        List<Worker> idleWorkers = this.getIdleWorkers();

        UnitType demandedBuilding = this.demandManager.getFirstBuildingDemanded();

        if(demandedBuilding != null && CostCalculator.canAfford(player, demandedBuilding)){
            if(this.builder == null){
                this.delegateWorkerToBuild();
            }
            if(this.builder != null && this.demandManager.areBuildingsDemanded()){
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