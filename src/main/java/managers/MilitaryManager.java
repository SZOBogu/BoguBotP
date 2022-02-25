package managers;

import bwapi.*;
import bwem.Base;
import helpers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static bwapi.UnitType.*;

@Component
public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
    private boolean isAttackCommandIssued = false;
    private List<List<Unit>> militaryGroups = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers

//    List<Unit> attackers = new ArrayList<>();

    private MapHelper mapHelper;
    private Unit scout;
    private TilePosition rallyPoint;
    private Position attackRallyPoint;
    private Game game;
    private BaseInfoTracker baseInfoTracker;
    private boolean isAttackSent;
    private UnitType unitToProduceConstantly = Protoss_Zealot;
    private DemandManager demandManager;
    private BuildingManager buildingManager;
    int targetMilitaryGroupSize = 24;
    int frames = 0;

    @Override
    public void add(Unit unit){
        this.militaryUnits.add(unit);
        unit.attack(rallyPoint.toPosition());
    }

    @Override
    public void remove(Unit unit){
        this.militaryUnits.remove(unit);
    }

    @Override
    public void manage() {
        if(this.scout == null && !this.militaryUnits.isEmpty()) {
            this.scout = this.militaryUnits.get(this.militaryUnits.size() - 1);
        }
        if(this.scout != null) {
            this.tellScoutToGetToNextBase();
        }
        Base enemyBase = this.baseInfoTracker.getClosestBaseWithState(BaseState.ENEMY);

        if(this.unitToProduceConstantly != null && !this.demandManager.isOnDemandList(this.unitToProduceConstantly)){
            this.demandManager.demandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(this.unitToProduceConstantly).build());
        }

        if(enemyBase != null && this.militaryUnits.size() > this.targetMilitaryGroupSize && !this.isAttackSent){
            this.isAttackSent = true;
            this.formMilitaryGroup();
        }
        if(isAttackSent){
            this.manageAttack();
        }

        List<List<Unit>> militaryGroupsToClear = this.militaryGroups.stream().filter(List::isEmpty).collect(Collectors.toList());
        this.militaryGroups.removeAll(militaryGroupsToClear);

        this.demandMilitaryProduction();
    }

    public void tellScoutToGetToNextBase(){
        Base nextBase = this.baseInfoTracker.getClosestBaseWithState(this.scout.getTilePosition(), BaseState.UNKNOWN);

        if(nextBase != null) {
            if (!this.scout.isMoving() && !this.scout.isStuck() || this.scout.isIdle()) {
                if (!game.isVisible(nextBase.getLocation())) {
                    this.scout.move(nextBase.getCenter());
                }
                else {
                    if (this.baseInfoTracker.checkBaseState(nextBase) != BaseState.MINE) {
                        this.baseInfoTracker.markBaseAsNeutral(nextBase);
                    }
                    List<Base> unknownBases = this.baseInfoTracker.getClosestBasesWithState(this.scout.getTilePosition(), BaseState.UNKNOWN);
                    List<Base> enemyBases = this.baseInfoTracker.getClosestBasesWithState(this.scout.getTilePosition(), BaseState.UNKNOWN);
                    if(unknownBases.size() == 1 && enemyBases.size() < 1){
                        this.baseInfoTracker.markBaseAsEnemy(unknownBases.get(0));
                    }
                }
            } else if (this.scout.isStuck()) {
                this.scout = this.militaryUnits.get(this.militaryUnits.size() - 1);
            }
        }
        else
            baseInfoTracker.markAllNeutralBasesAsUnknown();
    }

    public void setGlobalRallyPoint(){
        TilePosition temp = mapHelper.getListOfBases().get(1).getLocation();
        this.rallyPoint = new TilePosition(temp.x + 15, temp.y);
        if(!this.game.isWalkable(this.rallyPoint.toWalkPosition())){
            this.rallyPoint = new TilePosition(temp.x - 15, temp.y);
        }
    }

    public void setAttackRallyPoint(){
        this.attackRallyPoint = mapHelper.getMap().getCenter();
    }

    private void manageAttack(){
       // if(!isAttackCommandIssued) {
            if (isAttackCommandIssued && !this.areAllAttackersInPlace()) {
                getAttackersInOnePlace();
            }
            if (this.areAllAttackersInPlace()) {
                this.attack();
            }
        //}
    }

    private void getAttackersInOnePlace(){
        if(!this.isAttackCommandIssued){
            this.militaryGroups.get(0).forEach(unit -> unit.attack(this.attackRallyPoint));
            this.isAttackCommandIssued = true;
        }
    }

    private void attack(){
        Base base = this.baseInfoTracker.getClosestBaseWithState(BaseState.ENEMY);
        if(!this.isAttackCommandIssued) {
            for(List<Unit> militaryGroup : this.militaryGroups)
                militaryGroup.forEach(unit -> unit.attack(AwayFromPositionGetter.getPositionAwayFromCenter(this.mapHelper.getMap(), base.getCenter(), 6, 6)));
            this.isAttackCommandIssued = true;
        }
    }

    private boolean areAllAttackersInPlace(){
        if(this.frames > 100){
            this.frames = 0;
            return true;
        }
        else
            this.frames++;
        for(List<Unit> militaryGroup : this.militaryGroups) {
            for (Unit attacker : militaryGroup) {
                if (attacker.getDistance(this.attackRallyPoint) > 15) {
                    return false;
                }
            }
        }
        return true;
    }

    public void formMilitaryGroup(){
        this.militaryGroups.add(this.militaryUnits.stream().filter(unit -> unit != this.scout).collect(Collectors.toList()));
    }

    public void handleMilitaryDestruction(Unit unit) {
        if(unit.equals(this.scout) && this.scout != null){
            if(this.mapHelper.getBaseClosestToTilePosition(this.scout.getTilePosition()).getCenter().getDistance(this.scout.getPosition()) < 5)
                this.baseInfoTracker.markBaseAsEnemy(this.mapHelper.getBaseClosestToTilePosition(this.scout.getTilePosition()));
            this.scout = null;
        }
        for(List<Unit> militaryGroup : this.militaryGroups){
            if(militaryGroup.contains(unit)){
                militaryGroup.remove(unit);
                if(militaryGroup.size() < this.targetMilitaryGroupSize/2){
                    this.reassessStrategy();
                }
                if(militaryGroup.isEmpty()){
                    this.isAttackSent = false;
                }
                this.remove(unit);
            }
        }

    }

    public void reassessStrategy(){
        if(this.unitToProduceConstantly == UnitType.Protoss_Zealot){
            if(!game.self().hasUnitTypeRequirement(UnitType.Protoss_Dragoon)){
                Map<UnitType, Integer> requirements = UnitType.Protoss_Dragoon.requiredUnits();
                for(UnitType type : requirements.keySet()){
                    if(!demandManager.isOnDemandList(type) && this.buildingManager.countAllBuildingsOfType(type) > 0)
                        demandManager.demandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(type).build());
                }
            }
            this.unitToProduceConstantly = UnitType.Protoss_Dragoon;
        }
        else{
            this.unitToProduceConstantly = UnitType.Protoss_Zealot;
        }
    }

    public void demandMilitaryProduction(){
        List<UnitType> factories = Arrays.asList(UnitType.Protoss_Gateway, UnitType.Protoss_Robotics_Facility, UnitType.Protoss_Stargate);
        UnitType factory = factories.stream().filter(f -> f.buildsWhat().contains(this.unitToProduceConstantly)).findFirst().get();

        if(this.buildingManager.countCompletedBuildingsOfType(factory) > this.demandManager.howManyUnitsOnDemandList(this.unitToProduceConstantly)){
            this.demandManager.demandCreatingUnit(new ProductionOrder.ProductionOrderBuilder(this.unitToProduceConstantly).build());
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }
    @Autowired
    public void setBaseInfoTracker(BaseInfoTracker baseInfoTracker) {
        this.baseInfoTracker = baseInfoTracker;
    }

    private void setScout(Unit scout) {
        this.scout = scout;
    }

    @Autowired
    public void setDemandManager(DemandManager demandManager) {
        this.demandManager = demandManager;
    }

    @Autowired
    public void setBuildingManager(BuildingManager buildingManager) {
        this.buildingManager = buildingManager;
    }
}
