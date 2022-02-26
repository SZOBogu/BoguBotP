package managers;

import bwapi.Game;
import bwapi.Unit;
import bwem.Base;
import helpers.BaseInfoTracker;
import helpers.BaseState;
import helpers.MapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoutingManager implements IBroodWarManager{
    private Game game;
    private GlobalBasesManager globalBasesManager;
    private MilitaryManager militaryManager;
    private MapHelper mapHelper;
    private BaseInfoTracker baseInfoTracker;
    private Unit scout;
    private boolean wasProbeSent = false;

    @Override
    public void manage() {
            if(this.scout != null){
                tellScoutToGetToNextBase();
            }
            else{
                this.scout = nominateScout();
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
                this.scout = null;
            }
        }
        else
            baseInfoTracker.markAllNeutralBasesAsUnknown();
    }

    public Unit nominateScout(){
        if(this.militaryManager.isScoutAvailable()){
            return this.militaryManager.getScout();
        }
        else if(this.globalBasesManager.isScoutAvailable() && !this.wasProbeSent){
            this.wasProbeSent = true;
            return this.globalBasesManager.getScout();
        }
        else return null;
    }

    public void removeScout(Unit unit) {
        if(unit.equals(this.scout)){
            if(this.mapHelper.getBaseClosestToTilePosition(this.scout.getTilePosition()).getCenter().getDistance(this.scout.getPosition()) < 5)
                this.baseInfoTracker.markBaseAsEnemy(this.mapHelper.getBaseClosestToTilePosition(this.scout.getTilePosition()));
            this.scout = null;
        }
    }

    @Autowired
    public void setGlobalBasesManager(GlobalBasesManager globalBasesManager) {
        this.globalBasesManager = globalBasesManager;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }

    @Autowired
    public void setBaseInfoTracker(BaseInfoTracker baseInfoTracker) {
        this.baseInfoTracker = baseInfoTracker;
    }

    @Autowired
    public void setMilitaryManager(MilitaryManager militaryManager) {
        this.militaryManager = militaryManager;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
