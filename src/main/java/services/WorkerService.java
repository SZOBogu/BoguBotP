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
    private Unit builder;
    private List<UnitType> buildingsDemanded;

    public WorkerService(){
        this.workers = new ArrayList<>();
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
        delegateWorkerToGatheringResources(worker);
    }

    private void delegateWorkerToBuild(){
        UnitType toBuild = player.getRace().getSupplyProvider();
        TilePosition buildLocation = game.getBuildLocation(toBuild, player.getStartLocation());
        workers.get(0).build(toBuild, buildLocation);
    }

    private void delegateWorkerToGatheringResources(Unit worker){
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

    public void demandBuilding(UnitType buildingType){
        System.out.printf("Building demanded " + buildingType);
        System.out.printf(String.valueOf(buildingsDemanded));
        this.buildingsDemanded.add(buildingType);
    }

    public void fulfillDemandOnBuilding(UnitType buildingType){
        System.out.printf("Building demand fulfilled " + buildingType);
        System.out.printf(String.valueOf(buildingsDemanded));
        this.buildingsDemanded.remove(buildingType);
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
