package services;

import bwapi.*;
import enums.WorkerRole;
import pojos.UnitList;
import pojos.WorkerRoleEntry;

import java.util.*;

//@Service
public class WorkerService implements IBroodWarManager{
//    @Autowired
    private Player player;
//    @Autowired
    private Game game;
    private UnitList workers;
    private WorkerRoleEntry builder;
    private List<UnitType> buildingsDemanded;

    public WorkerService(){
        this.workers = new UnitList();
//        this.builders = new ArrayList<>();
        this.buildingsDemanded = new ArrayList<>();
    }

    public void addWorker(Unit unit){
        workers.add(unit);
    }

    public void removeWorker(Unit unit){
        if(unit.getType().isWorker()){
            workers.remove(unit);
        }
    }

    private List<WorkerRoleEntry> getIdleWorkers(){
        List<WorkerRoleEntry> idleWorkers = new ArrayList<>();
        for(WorkerRoleEntry workerEntity : this.workers.getUnitList()){
            if(workerEntity.getUnit().isIdle()){
//                if(!(builder == null) && workerEntity.getUnit().equals(builder.getUnit()) && buildingsDemanded.isEmpty()) {
//                    builder = null;
//                    builder.setUnitState(UnitState.IDLE);
//                }
                if(workerEntity.getUnitState() == WorkerRole.IDLE){
                    idleWorkers.add(workerEntity);
                }
            }
//            workerEntity.setUnitState(UnitState.IDLE);
        }
        return idleWorkers;
    }

    private void delegateWorkerToWork(WorkerRoleEntry worker){
        if(builder == null && !this.buildingsDemanded.isEmpty()) {
            this.delegateWorkerToBuild();
            builder.setUnitState(WorkerRole.BUILDING);
            this.tryToBuild();
        }
        else if(this.countRefineries() > 0 && this.workers.countUnitsWithState(WorkerRole.GAS_MINE) < this.countRefineries() * 3){
            delegateWorkersToGatherGas(this.getRefinery());
//            worker.setUnitState(UnitState.GAS_MINE);
        }
        else{
            delegateWorkerToGatherMinerals(worker);
//            worker.setUnitState(UnitState.MINERAL_MINE);
        }
    }

    private void delegateWorkerToBuild(){
        List<WorkerRoleEntry> idleWorkers = this.getIdleWorkers();
        WorkerRoleEntry worker;

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
        TilePosition buildLocation = game.getBuildLocation(this.buildingsDemanded.get(0), player.getStartLocation());
        this.builder.getUnit().build(this.buildingsDemanded.get(0), buildLocation);
//        System.out.println("builder sent to build");
    }

    private void delegateWorkerToGatherMinerals(WorkerRoleEntry worker){
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
//        System.out.println("worker delegated to minerals");
    }

    public void delegateWorkersToGatherGas(Unit refinery){
        List<WorkerRoleEntry> idleWorkers = this.getIdleWorkers();
        Random random = new Random();
        int workersLeftToAssign = 3;

        for(WorkerRoleEntry workerEntry : idleWorkers){
            if(workersLeftToAssign > 0) {
                this.delegateWorkerToGatherGas(workerEntry, refinery);
                workersLeftToAssign--;
            }
            else
                break;
        }

        List<WorkerRoleEntry> mineralMiners = this.workers.getUnitsWithState(WorkerRole.MINERAL_MINE);
        if(this.workers.size() > workersLeftToAssign + 1){
            for(int i = 0; i < workersLeftToAssign; i++){
                WorkerRoleEntry worker = mineralMiners.get(random.nextInt(mineralMiners.size()));
                mineralMiners.remove(worker);
                this.delegateWorkerToGatherGas(worker, refinery);
            }
        }
        System.out.println("Gas miners : " + this.workers.getUnitsWithState(WorkerRole.GAS_MINE));
        //System.out.println("Workers allegedly sent to gas");
    }

    private void delegateWorkerToGatherGas(WorkerRoleEntry worker, Unit refinery){
//        if(this.builder.getUnit().equals(worker.getUnit())) {
//            this.builder = null;
//        }
//        worker.setUnitState(UnitState.GAS_MINE);
        worker.getUnit().gather(refinery);
        worker.setUnitState(WorkerRole.GAS_MINE);
//        System.out.println("Worker delegated to gas");
    }

    public void demandBuilding(UnitType buildingType){
        System.out.println("Building demanded " + buildingType);
        this.buildingsDemanded.add(buildingType);
        System.out.println(buildingsDemanded);
    }

    public void fulfillDemandOnBuilding(UnitType buildingType){
        System.out.println("Building demand fulfilled " + buildingType);
        this.buildingsDemanded.remove(buildingType);
        System.out.println(buildingsDemanded);
    }

    public void freeBuilder(){
        if(builder != null) {
            this.builder.setUnitState(WorkerRole.IDLE);
            this.builder = null;
        }
    }

    @Override
    public void manage() {
        List<WorkerRoleEntry> idleWorkers = this.getIdleWorkers();
        for(WorkerRoleEntry idleWorker : idleWorkers){
            delegateWorkerToWork(idleWorker);
        }
//        System.out.println(idleWorkers);

        if(this.builder != null && !this.buildingsDemanded.isEmpty() && this.buildingsDemanded.get(0).mineralPrice() <= this.player.minerals() && this.buildingsDemanded.get(0).gasPrice() <= this.player.gas()){
            this.tryToBuild();
        }

        for(WorkerRoleEntry mineralMiner : this.workers.getUnitsWithState(WorkerRole.MINERAL_MINE)){
            if(!mineralMiner.getUnit().isGatheringMinerals()){
                this.delegateWorkerToGatherMinerals(mineralMiner);
            }
        }

        for(WorkerRoleEntry gasMiner : this.workers.getUnitsWithState(WorkerRole.GAS_MINE)){
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

    public List<UnitType> getBuildingsDemanded() {
        return buildingsDemanded;
    }

    public boolean isWorkerDelegatedToBuild(){
        return this.builder != null;
    }
}
