package managers;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwem.BWMap;
import bwem.Base;
import helpers.BaseInfoTracker;
import helpers.BaseState;
import helpers.MapHelper;
import javafx.scene.control.ButtonBase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers
//    private int indexOfLastScoutedBase = 0;

    private MapHelper mapHelper;
    private Unit scout;
    private TilePosition rallyPoint;
    private Game game;
    private BaseInfoTracker baseInfoTracker;

//    private boolean isScoutSent = false;
//    private int frame = 0;

    @Override
    public void add(Unit unit){
        this.militaryUnits.add(unit);
        if(this.scout == null){
            this.setScout(unit);
            this.tellScoutToGetToNextBase();
        }
        else
            unit.move(rallyPoint.toPosition());
    }

    @Override
    public void remove(Unit unit){
        this.militaryUnits.remove(unit);
    }

    @Override
    public void manage() {
            if(this.scout != null) {
                this.tellScoutToGetToNextBase();
            }
    }

    public void tellScoutToGetToNextBase(){
        Base nextBase = this.baseInfoTracker.getClosestBaseWithState(this.scout.getTilePosition(), BaseState.UNKNOWN);

        if(nextBase != null) {
            if (!this.scout.isMoving() && !this.scout.isStuck() || this.scout.isIdle()) {
                System.out.println("Scout isn't moving nor stuck");
                if (!game.isVisible(nextBase.getLocation())) {
                    this.scout.move(nextBase.getCenter());
                } else {
                    if (this.baseInfoTracker.checkBaseState(nextBase) != BaseState.MINE) {
                        this.baseInfoTracker.markBaseAsNeutral(nextBase);
                        System.out.println("Base Discovered");
                    }
                }
            } else if (this.scout.isStuck()) {
                Random random = new Random();
                this.scout = this.militaryUnits.get(random.nextInt(this.militaryUnits.size()));
            }
        }
        else
            baseInfoTracker.markAllNeutralBasesAsUnknown();
    }

    public void tellScoutToSideStep(){
        TilePosition temp = this.scout.getTilePosition();
        this.scout.move(new TilePosition(temp.x + 10, temp.y).toPosition());
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
