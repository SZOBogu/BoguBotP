package services;

import bwapi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

//@Service
public class WorkerService implements IBroodWarManager{
//    @Autowired
    private Player player;
//    @Autowired
    private Game game;
    private ArrayList<Unit> workers;
    private ArrayList<Unit> builders;

    public WorkerService(){
        this.workers = new ArrayList<>();
        this.builders = new ArrayList<>();
    }

    public void addWorker(Unit unit){
        if(unit.getType().isWorker()){
            workers.add(unit);
        }
    }

    public void removeWorker(Unit unit){
        if(unit.getType().isWorker()){
            workers.remove(unit);
        }
    }

    private List<Unit> getIdleWorkers(){
        List<Unit> idleWorkers = new ArrayList<>();
        for(Unit worker : this.workers){
            if(worker.isIdle()){
                idleWorkers.add(worker);
            }
        }
        return idleWorkers;
    }

    private void delegateWorkerToWork(Unit worker){
        if (builders.isEmpty() && (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400)) {
            this.builders.add(worker);
            this.delegateWorkerToBuild();
        }
        else{
            delegateWorkerToGatheringResources(worker);
        }
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

    @Override
    public void manage() {
        List<Unit> idleWorkers = this.getIdleWorkers();
        for(Unit idleWorker : idleWorkers){
            delegateWorkerToWork(idleWorker);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
