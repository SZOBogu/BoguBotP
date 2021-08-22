package managers;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwem.Base;
import helpers.BaseInfoTracker;
import helpers.BaseState;
import helpers.MapHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers

    private MapHelper mapHelper;
    private Unit scout;
    private TilePosition rallyPoint;
    private Game game;
    private BaseInfoTracker baseInfoTracker;

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
            if(enemyBase != null && this.militaryUnits.size() > 20){
                this.militaryUnits.forEach(unit -> unit.attack(enemyBase.getLocation().toPosition()));
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
                }
            } else if (this.scout.isStuck()) {
                this.scout = this.militaryUnits.get(this.militaryUnits.size() - 1);
            }
        }
        else
            baseInfoTracker.markAllNeutralBasesAsUnknown();
    }

    public void tellScoutToSideStep(){
        TilePosition temp = this.scout.getTilePosition();
        this.scout.move(new TilePosition(temp.x + 10, temp.y).toPosition());
    }

    public void handleMilitaryDestruction(Unit unit) {
        if(unit == this.scout){
            this.scout = null;
        }
        this.remove(unit);
    }

    public void setGlobalRallyPoint(){
        TilePosition temp = mapHelper.getListOfBases().get(1).getLocation();
        this.rallyPoint = new TilePosition(temp.x + 15, temp.y);
        if(!this.game.isWalkable(this.rallyPoint.toWalkPosition())){
            this.rallyPoint = new TilePosition(temp.x - 15, temp.y);
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }

    public void setBaseInfoTracker(BaseInfoTracker baseInfoTracker) {
        this.baseInfoTracker = baseInfoTracker;
    }

    private void setScout(Unit scout) {
        this.scout = scout;
    }


}
