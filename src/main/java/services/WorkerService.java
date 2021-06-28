package services;

import bwapi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Service
public class WorkerService implements IBroodWarManager{
//    @Autowired
    private Player player;
//    @Autowired
    private Game game;
    private List<Unit> workers;
    private List<Unit> mineralMiners;
    private List<Unit> gasMiners;
    private Unit builder;
    private List<UnitType> buildingsDemanded;

    public WorkerService(){
        this.workers = new ArrayList<>();
        this.mineralMiners = new ArrayList<>();
//        this.builders = new ArrayList<>();
        this.buildingsDemanded = new ArrayList<>();
        this.gasMiners = new ArrayList<>();
    }

    public void addWorker(Unit unit){
        workers.add(unit);
    }

    public void removeWorker(Unit unit){
        if(unit.getType().isWorker()){
            workers.remove(unit);
        }
    }

    private List<Unit> getIdleWorkers(){
        List<Unit> idleWorkers = new ArrayList<>();
        for(Unit worker : this.workers){
            if(worker.isIdle() && worker.equals(builder)){
                idleWorkers.add(worker);
                builder = null;
            }
            if(worker.isIdle()){
                idleWorkers.add(worker);
            }
        }
        return idleWorkers;
    }

    private void delegateWorkerToWork(Unit worker){
        if(builder == null && !this.buildingsDemanded.isEmpty()) {
            this.delegateWorkerToBuild(worker);
        }
//        else if(this.workers.size() > (this.countRefineries() * 2 + 1) && this.countRefineries()>1){
        else if(this.countRefineries() > 0 && this.gasMiners.size() < this.countRefineries() * 3){
            delegateWorkersToGatherGas(this.getRefinery());
        }
        else{
            delegateWorkerToGatherMinerals(worker);
        }
    }

    private void delegateWorkerToBuild(Unit worker){
        UnitType toBuild = player.getRace().getSupplyProvider();
        TilePosition buildLocation = game.getBuildLocation(toBuild, player.getStartLocation());
        worker.build(toBuild, buildLocation);
    }

    private void delegateWorkerToGatherMinerals(Unit worker){
        Unit closestMineralPatch = null;
        int minDistance = Integer.MAX_VALUE;
        for(Unit mineralPatch: this.game.getMinerals()){
            int tempDistance = worker.getDistance(mineralPatch);
            if(tempDistance < minDistance){
                closestMineralPatch = mineralPatch;
                minDistance = tempDistance;
            }
        }
        worker.gather(closestMineralPatch);
    }

    public void delegateWorkersToGatherGas(Unit refinery){
        List<Unit> idleWorkers = this.getIdleWorkers();
        Random random = new Random();
        int workersLeftToAssign = 3;

        for(Unit worker : idleWorkers){
            if(workersLeftToAssign > 0) {
                this.delegateWorkerToGatherGas(worker, refinery);
                workersLeftToAssign--;
            }
            else
                break;
        }

        if(this.mineralMiners.size() > workersLeftToAssign + 1){
            for(int i = 0; i < workersLeftToAssign; i++){
                Unit worker = this.mineralMiners.get(random.nextInt(this.mineralMiners.size()));
                this.mineralMiners.remove(worker);
                this.gasMiners.add(worker);
                this.delegateWorkerToGatherGas(worker, refinery);
            }
        }
        System.out.println("Gas miners : " + this.gasMiners);
        //System.out.println("Workers allegedly sent to gas");
    }

    private void delegateWorkerToGatherGas(Unit worker, Unit refinery){
        this.mineralMiners.remove(worker);
        if(this.builder.equals(worker))
            this.builder = null;
        this.gasMiners.add(worker);
        worker.gather(refinery);
        System.out.println("Worker delegated to gas");
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

    @Override
    public void manage() {
        List<Unit> idleWorkers = this.getIdleWorkers();
        for(Unit idleWorker : idleWorkers){
            delegateWorkerToWork(idleWorker);
        }

        Random random = new Random();
        Unit worker = this.workers.get(random.nextInt(this.workers.size()));

        if (!this.buildingsDemanded.isEmpty() && this.builder == null){
            builder = worker;
            TilePosition buildLocation = game.getBuildLocation(this.buildingsDemanded.get(0), player.getStartLocation());
            builder.build(this.buildingsDemanded.get(0), buildLocation);
        }
        else if(!this.buildingsDemanded.isEmpty()){
            TilePosition buildLocation = game.getBuildLocation(this.buildingsDemanded.get(0), player.getStartLocation());
            builder.build(this.buildingsDemanded.get(0), buildLocation);
        }

        for(Unit mineralMiner : this.mineralMiners){
            if(!mineralMiner.isGatheringMinerals()){
                this.delegateWorkerToGatherMinerals(mineralMiner);
            }
        }

        for(Unit gasMiner : this.gasMiners){
            if(!gasMiner.isGatheringGas()){
                gasMiner.gather(getRefinery());
                if(this.mineralMiners.contains(gasMiner)){
                    this.mineralMiners.remove(gasMiner);
                }
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<UnitType> getBuildingsDemanded() {
        return buildingsDemanded;
    }

    public void setBuildingsDemanded(List<UnitType> buildingsDemanded) {
        this.buildingsDemanded = buildingsDemanded;
    }

    public boolean isWorkerDelegatedToBuild(){
        return this.builder != null;
    }
}
