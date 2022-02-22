package managers;

import bwapi.*;
import bwem.Base;
import helpers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers

    List<Unit> attackers = new ArrayList<>();

    private MapHelper mapHelper;
    private Unit scout;
    private TilePosition rallyPoint;
    private Position attackRallyPoint;
    private Game game;
    private BaseInfoTracker baseInfoTracker;
    private boolean isAttackSent;
    private UnitType unitToProduceConstantly;
    private DemandManager demandManager;

    int frames = 0;

    @Override
    public void add(Unit unit){
        this.militaryUnits.add(unit);
        unit.move(rallyPoint.toPosition());
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

        if(enemyBase != null && this.militaryUnits.size() > 20 && !this.isAttackSent){
            this.isAttackSent = true;
            this.attackers = this.militaryUnits.stream().filter(unit -> unit != this.scout).collect(Collectors.toList());
        }
        if(isAttackSent){
            this.manageAttack();
        }
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
        if(isAttackSent && !this.areAllAttackersInPlace()) {
            getAttackersInOnePlace();
        }
        if(this.areAllAttackersInPlace()){
            this.attack();
        }
    }

    private void getAttackersInOnePlace(){
        Position centerTile = this.mapHelper.getMap().getCenter();
        this.attackers.forEach(unit -> unit.attack(centerTile));
    }

    private boolean areAllAttackersInPlace(){
        if(this.frames > 1000){
            this.frames = 0;
            return true;
        }
        else
            this.frames++;
        for(Unit attacker : this.attackers){
            if(attacker.getDistance(this.attackRallyPoint) < 15){
                return false;
            }
        }
        return true;
    }

    private void attack(){
        Base base = this.baseInfoTracker.getClosestBaseWithState(BaseState.ENEMY);

        this.attackers.forEach(unit -> unit.attack(AwayFromPositionGetter.getPositionAwayFromCenter(this.mapHelper.getMap(), base.getCenter(), 2, 2)));
    }

    public void handleMilitaryDestruction(Unit unit) {
        if(unit == this.scout){
            this.scout = null;
        }
        this.attackers.remove(unit);
        if(this.attackers.isEmpty()){
            this.isAttackSent = false;
        }
        this.remove(unit);
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
}
