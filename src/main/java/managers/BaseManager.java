package managers;

import bwapi.*;
import bwem.Base;
import bwem.Mineral;
import enums.WorkerRole;
import helpers.*;
//import javafx.geometry.Pos;
import pojos.TextInGame;
import pojos.WorkerList;
import pojos.Worker;

import java.util.*;

public class BaseManager implements IUnitManager{
    private final Player player;
    private final Game game;
    private Worker builder;
    private IDemandManager demandManager;
    private final MapHelper mapHelper;

    private Unit nexus;
    private Unit assimilator;
    private Base base;

    private boolean isOversaturationCalled;
    private final WorkerList workers;

    public static class WorkerManagerBuilder{
        private final Player player;
        private final Game game;
        private final MapHelper mapHelper;
        private Unit nexus;
        private Unit assimilator;
        private Base base;

        private IDemandManager demandManager;

        public WorkerManagerBuilder(Player player, Game game, MapHelper mapHelper, Base base){
            this.player = player;
            this.game = game;
            this.mapHelper = mapHelper;
            this.base = base;
            System.out.println("WORKER MANAGER BUILDER: " + PositionPrinter.toString(base));
        }

        public WorkerManagerBuilder demandManager(IDemandManager demandManager){
            this.demandManager = demandManager;
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
        public BaseManager build(){
            return new BaseManager(this);
        }
    }

    public BaseManager(WorkerManagerBuilder builder){
        this.player = builder.player;
        this.game = builder.game;
        this.mapHelper = builder.mapHelper;
        this.base = builder.base;

        this.nexus = builder.nexus;
        this.assimilator = builder.assimilator;

        this.isOversaturationCalled = false;
        this.workers = new WorkerList();

        this.demandManager = builder.demandManager;
    }

    @Override
    public void add(Unit unit){
        Worker worker = new Worker(unit);
        this.workers.add(worker);
    }

    @Override
    public void remove(Unit unit){
        workers.remove(unit);
    }

    public void handleWorkerDestruction(Unit unit){
        if (this.builder != null && unit == this.builder.getWorker()) {
            this.builder = null;
        }
        this.remove(unit);
    }

    private List<Worker> getIdleWorkers(){
        List<Worker> idleWorkers = new ArrayList<>();
        for(Worker worker : this.workers.getWorkerList()){
            if(worker.getWorker().isIdle() || worker.getWorkerRole() == WorkerRole.IDLE){
                    idleWorkers.add(worker);
            }
        }
        return idleWorkers;
    }

    //Get and remove form this manager i workers. Used in transferring probes to another base
    public List<Worker> popWorkers(int i){
        List<Worker> workersPopped = new ArrayList<>(this.freeWorkers(i));
        for(Worker worker : workersPopped){
            this.remove(worker.getWorker());
        }
        workersPopped.forEach(worker -> this.remove(worker.getWorker()));

        return workersPopped;
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
            return worker;
        }
        return null;
    }

    public List<Worker> freeWorkers(int howManyWorkersToFree){
        List<Worker> workers = new ArrayList<>();
        for(;;){
            if(workers.size() < howManyWorkersToFree) {
                workers.add(this.makeWorkerAvailable());
            }
            else
                break;
        }
        System.out.println("FREED WORKERS : " + workers.size());
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
        List<Mineral> mineralPatchesInBase = this.base.getMinerals();
        Random r = new Random();
        worker.getWorker().gather(mineralPatchesInBase.get(r.nextInt(mineralPatchesInBase.size())).getUnit(), true);

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
    }

    private void delegateWorkerToGatherGas(Worker worker, Unit refinery){
        worker.getWorker().gather(refinery);
        worker.setWorkerRole(WorkerRole.GAS_MINE);
    }

    private void delegateWorkerToBuild(){
        List<Worker> idleWorkers = this.getIdleWorkers();
        Worker worker;

        List<Worker> mineralMiners = this.workers.getWorkersWithState(WorkerRole.MINERAL_MINE);
        List<Worker> gasMiners = this.workers.getWorkersWithState(WorkerRole.GAS_MINE);

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
            List<Base> closestBases = this.mapHelper.getBasesClosestToTilePosition(base.getLocation());
            for(Base base : closestBases){
                if(!base.equals(this.base))
                    return base.getLocation();
            }
            return closestBases.get(0).getLocation();
        }
        else
            return game.getBuildLocation(buildingType, player.getStartLocation());
    }

    private void tryToBuild(ProductionOrder order){
        if(!this.builder.getWorker().isStuck()) {
            TilePosition buildLocation = this.getTileToBuildOn(order.getUnitType());
            this.builder.getWorker().build(order.getUnitType(), buildLocation);
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
        return (this.workers.size() > (this.base.getGeysers().size() + this.base.getMinerals().size()) * 2.5);
    }

    public void acceptWorkerTransfer(List<Worker> workerTrain){
        System.out.println("Worker transfer allegedly received: " + workerTrain.size());
        Position position = new Position(this.base.getCenter().x + 2, this.base.getCenter().y);

        for(Worker worker : workerTrain){
            this.workers.getWorkerList().add(worker);
            worker.setWorkerRole(WorkerRole.IDLE);
            worker.getWorker().move(position);
            this.delegateWorkerToGatherMinerals(worker);
        }
    }

    public int getAmountOfSurplusWorkers(){
        return this.base.getGeysers().size() * 3 + this.base.getMinerals().size() * 2;
    }

    public void orderNewProbe(){
        if(this.nexus != null &&
                AffordabilityChecker.canAfford(player, UnitType.Protoss_Probe) &&
                !this.nexus.isTraining() &&
                !this.isOversaturated()){
            this.demandManager.demandCreatingUnit(ProductionOrderFactory.createProbeOrder(this));
        }
    }

    public void build(ProductionOrder order){
        if(AffordabilityChecker.canAfford(player, order.getUnitType())){
            if(this.builder == null){
                this.delegateWorkerToBuild();
            }
            if(this.builder != null && this.demandManager.areBuildingsDemanded()){
                this.tryToBuild(order);
            }
        }
        else
            this.freeBuilder();
    }

    public boolean isScoutAvailable(){
        return this.workers.size() > 4;
    }

    public Unit getScout(){
        Worker scout = this.freeWorkerWithRole(WorkerRole.MINERAL_MINE);
        scout.setWorkerRole(WorkerRole.SCOUT);
        this.workers.remove(scout.getWorker());
        return scout.getWorker();
    }

    @Override
    public void manage() {
            if(this.nexus.isCompleted() && this.nexus != null){
                this.forceGatheringGas();
                List<Worker> idleWorkers = this.getIdleWorkers();

                for(Worker worker : idleWorkers){
                    this.delegateWorkerToWork(worker);
                }

                if(isOversaturated() && !isOversaturationCalled){
                    this.isOversaturationCalled = true;
                    System.out.println("Oversaturation called");
                }

                this.orderNewProbe();

                this.game.drawTextMap(this.nexus.getPosition().getX(), this.nexus.getPosition().getY() -10,"Probes: " + this.workers.getWorkerList().size(), Text.Default);
                this.game.drawTextMap(this.nexus.getPosition().getX(), this.nexus.getPosition().getY(),"Mineral miners: " + this.workers.countWorkersWithState(WorkerRole.MINERAL_MINE), Text.Cyan);
                this.game.drawTextMap(this.nexus.getPosition().getX(), this.nexus.getPosition().getY() + 10,"Gas miners: " + this.workers.countWorkersWithState(WorkerRole.GAS_MINE), Text.Green);
            }
        }

    @Override
    public List<TextInGame> getTextToWriteInGame() {
        List<TextInGame> textInGameList = new ArrayList<>();
        for(Worker worker : this.workers.getWorkerList()){
            TextInGame text = new TextInGame.TextInGameBuilder(worker.getWorkerRole().toString())
                    .position(worker.getWorker().getPosition())
                    .build();
            textInGameList.add(text);
        }

        return textInGameList;
    }

    public void setAssimilator(Unit assimilator) {
        this.assimilator = assimilator;
    }

    public Base getBase() {
        return base;
    }

    public void setDemandManager(IDemandManager demandManager) {
        this.demandManager = demandManager;
    }

    public void setNexus(Unit nexus) {
        this.nexus = nexus;
    }

    @Override
    public String toString() {
        return "WorkerManager\t Workers Managed: " + workers.size();
    }
}