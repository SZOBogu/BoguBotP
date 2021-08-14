package managers;

import bwapi.*;
import bwem.Base;
import bwem.Mineral;
import enums.WorkerRole;
import helpers.CostCalculator;
import helpers.MapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import pojos.WorkerList;
import pojos.Worker;

import java.util.*;

public class WorkerManager implements IUnitManager{
    private final Player player;
    private final Game game;
    private Worker builder;
    private DemandManager demandManager;
    private ExpansionManager expansionManager;
    private final MapHelper mapHelper;

    private Unit nexus;
    private Unit assimilator;
    private Base base;

    boolean isOverSaturationCalled;
    private final WorkerList workers;

    public static class WorkerManagerBuilder{
        private final Player player;
        private final Game game;
        private final MapHelper mapHelper;
        private Unit nexus;
        private Unit assimilator;
        private Base base;

        private DemandManager demandManager;
        private ExpansionManager expansionManager;

        public WorkerManagerBuilder(Player player, Game game, MapHelper mapHelper, Base base){
            this.player = player;
            this.game = game;
            this.mapHelper = mapHelper;
            this.base = base;
        }

        public WorkerManagerBuilder demandManager(DemandManager demandManager){
            this.demandManager = demandManager;
            return this;
        }

        public WorkerManagerBuilder expansionManager(ExpansionManager expansionManager){
            this.expansionManager = expansionManager;
            return this;
        }

        public WorkerManagerBuilder nexus(Unit nexus){
            this.nexus = nexus;
            return this;
        }

        public WorkerManagerBuilder assimilator(Unit assimilator){
            this.assimilator = assimilator;
            return this;
        }
        public WorkerManager build(){
            return new WorkerManager(this);
        }
    }

    public WorkerManager(WorkerManagerBuilder builder){
        this.player = builder.player;
        this.game = builder.game;
        this.mapHelper = builder.mapHelper;
        this.base = builder.base;

        this.nexus = builder.nexus;
        this.assimilator = builder.assimilator;

        this.isOverSaturationCalled = false;
        this.workers = new WorkerList();

        this.demandManager = builder.demandManager;
        this.expansionManager = builder.expansionManager;
    }

    @Override
    public void add(Unit unit){
        workers.add(unit);
    }

    @Override
    public void remove(Unit unit){
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
        this.remove(unit);
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
            if(this.builder.getWorkerRole() == WorkerRole.GAS_MINE && this.areGasMinersNeeded()){
                this.delegateWorkerToGatherGas(builder, this.assimilator);
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
        List<Mineral> mineralPatchesInMainBase = this.mapHelper.getMainBase().getMinerals();
        Random r = new Random();
        worker.getWorker().gather(mineralPatchesInMainBase.get(r.nextInt(mineralPatchesInMainBase.size())).getUnit());

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
        }
    }

    private TilePosition getTileToBuildOn(UnitType buildingType){
        if(buildingType == UnitType.Protoss_Nexus){
            return this.expansionManager.getNextNonTakenBase().getLocation();
        }
        else
            return game.getBuildLocation(buildingType, player.getStartLocation());
    }

    private void tryToBuild(){
        if(!this.builder.getWorker().isStuck()) {
            TilePosition buildLocation = this.getTileToBuildOn(this.demandManager.getFirstBuildingDemanded());
            this.builder.getWorker().build(this.demandManager.getFirstBuildingDemanded(), buildLocation);
        }
        else{
            this.delegateWorkerToGatherMinerals(this.builder);
            this.builder = null;
        }
    }

    private void delegateWorkerToWork(Worker worker){
        if(this.assimilator != null &&
                this.areGasMinersNeeded()){
            delegateWorkerToGatherGas(worker, this.assimilator);
        }
        //TODO: handling more than one assimilator
        else if(builder == null && this.demandManager.areBuildingsDemanded()) {
            this.delegateWorkerToBuild();
            this.tryToBuild();
        }
        else{
            delegateWorkerToGatherMinerals(worker);
        }
    }

    private boolean areGasMinersNeeded(){
        return (this.assimilator != null && this.workers.getWorkersWithState(WorkerRole.GAS_MINE).size() < 3);
    }

    private void forceGatheringGas(){
        List<Worker> gasMiners = this.workers.getWorkersWithState(WorkerRole.GAS_MINE);
        for(Worker worker: gasMiners){
            if(!worker.getWorker().isGatheringGas() && !worker.equals(this.builder) && !worker.getWorker().isCarryingGas()){
                this.delegateWorkerToGatherGas(worker, this.assimilator);
            }
        }
    }

    public boolean isOversaturated(){
        return (this.workers.size() > (this.base.getGeysers().size() + this.base.getMinerals().size()) * 3);
    }

    private void callOversaturation(){
        this.isOverSaturationCalled = true;
        this.expansionManager.handleOversaturation();
    }

    @Override
    public void manage() {
        List<Worker> idleWorkers = this.getIdleWorkers();

        UnitType demandedBuilding = this.demandManager.getFirstBuildingDemanded();

        this.forceGatheringGas();

        if(demandedBuilding != null && CostCalculator.canAfford(player, this.demandManager.getFirstBuildingDemanded())){
            if(this.builder == null){
                this.delegateWorkerToBuild();
            }
            if(this.builder != null && this.demandManager.areBuildingsDemanded()){
                this.tryToBuild();
            }
        }
        else
            this.freeBuilder();

        for(Worker worker : idleWorkers){
            this.delegateWorkerToWork(worker);
        }

        if(isOversaturated() && !isOverSaturationCalled){
            this.callOversaturation();
        }
    }

    public void setAssimilator(Unit assimilator) {
        this.assimilator = assimilator;
    }

    public Base getBase() {
        return base;
    }

    public void setDemandManager(DemandManager demandManager) {
        this.demandManager = demandManager;
    }

    public void setExpansionManager(ExpansionManager expansionManager) {
        this.expansionManager = expansionManager;
    }

    @Override
    public String toString() {
        return "WorkerManager";
    }
}