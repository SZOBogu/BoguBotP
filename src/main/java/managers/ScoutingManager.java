package managers;

import bwapi.Game;
import bwapi.Unit;
import bwem.Base;
import helpers.BaseInfoTracker;
import helpers.BaseState;
import helpers.MapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojos.BaseInfoRecord;
import pojos.TextInGame;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                        List<Unit> buildingsNearScout = game.getUnitsInRadius(this.scout.getPosition(), 200).stream().filter(o -> o.getType().isBuilding()).collect(Collectors.toList());
                        List<Unit> mineBuildingsNearScout = buildingsNearScout.stream().filter(b -> game.self().getUnits().contains(b)).collect(Collectors.toList());
                        List<Unit> enemyBuildingsNearScout = buildingsNearScout.stream().filter(b -> game.enemy().getUnits().contains(b)).collect(Collectors.toList());

                        if(!buildingsNearScout.isEmpty() && !enemyBuildingsNearScout.isEmpty())
                            this.baseInfoTracker.markBaseAsEnemy(nextBase);
                        else if(!buildingsNearScout.isEmpty() && !mineBuildingsNearScout.isEmpty())
                            this.baseInfoTracker.markBaseAsMine(nextBase);
                        else
                            this.baseInfoTracker.markBaseAsNeutral(nextBase);

                    }
                    List<Base> unknownBases = this.baseInfoTracker.getClosestBasesWithState(this.scout.getTilePosition(), BaseState.UNKNOWN);
                    List<Base> enemyBases = this.baseInfoTracker.getClosestBasesWithState(this.scout.getTilePosition(), BaseState.ENEMY);
                    if(unknownBases.size() == 1 && enemyBases.size() < 1){
                        this.baseInfoTracker.markBaseAsEnemy(unknownBases.get(0));
                    }
                }
            }
            else if (this.scout.isStuck()) {
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
    @Override
    public List<TextInGame> getTextToWriteInGame(){
        List<TextInGame> textInGameList = new ArrayList<>();
        List<BaseInfoRecord> baseInfoRecords = this.baseInfoTracker.getBases();
        for(BaseInfoRecord record : baseInfoRecords){
            TextInGame text = new TextInGame.TextInGameBuilder(record.getBaseState().toString())
                    .position(record.getBase().getCenter())
                    .build();
            textInGameList.add(text);
        }
        if(this.scout != null){
            TextInGame scoutText = new TextInGame.TextInGameBuilder("SCOUT")
                    .position(this.scout.getPosition())
                    .build();
            textInGameList.add(scoutText);
        }
        return textInGameList;
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
