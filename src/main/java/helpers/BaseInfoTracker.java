package helpers;

import bwapi.Game;
import bwapi.TilePosition;
import bwem.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojos.BaseInfoRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BaseInfoTracker {
    private Game game;
    private final List<BaseInfoRecord> bases = new ArrayList<>();
    private MapHelper mapHelper;

    public void init(MapHelper mapHelper){
        List<Base> bases = mapHelper.getBasesClosestToTilePosition(mapHelper.getMainBase().getLocation());

        for(Base base : bases){
            BaseInfoRecord record = new BaseInfoRecord(base, BaseState.UNKNOWN, 0);
            this.bases.add(record);
        }

        this.mapHelper = mapHelper;
    }

    public void markBaseAsMine(Base base){
        this.changeBaseState(base, BaseState.MINE, game.elapsedTime());
    }

    public void markBaseAsNeutral(Base base){
        this.changeBaseState(base, BaseState.NEUTRAL, game.elapsedTime());
    }

    public void markBaseAsEnemy(Base base){
        this.changeBaseState(base, BaseState.ENEMY, game.elapsedTime());
    }

    public void markBaseAsUnknown(Base base){
        this.changeBaseState(base, BaseState.UNKNOWN, game.elapsedTime());
    }

    private void changeBaseState(Base base, BaseState state, int timestamp){
        this.bases.stream().filter(b -> b.getBase().equals(base)).forEach(b -> {b.setBaseState(state); b.setTimeStamp(this.game.elapsedTime());});
    }

    public Base getClosestBaseWithState(BaseState state){
        return this.getClosestBaseWithState(this.bases.get(0).getBase().getLocation(), state);
    }

    public Base getClosestBaseWithState(TilePosition tilePosition, BaseState state){
        List<Base> basesClosest = this.mapHelper.getBasesClosestToTilePosition(tilePosition);

        for(Base base : basesClosest) {
            for (BaseInfoRecord record : this.bases) {
                if (record.getBaseState() == state && record.getBase().equals(base)) {
                    return record.getBase();
                }
            }
        }
        return null;
    }

    public List<Base> getClosestBasesWithState(TilePosition tilePosition, BaseState state){
        List<Base> basesClosest = this.mapHelper.getBasesClosestToTilePosition(tilePosition);
        List<Base> basesWithState = new ArrayList<>();

        for(Base base : basesClosest) {
            for (BaseInfoRecord record : this.bases) {
                if (record.getBaseState() == state && record.getBase().equals(base)) {
                    basesWithState.add(record.getBase());
                }
            }
        }
        return basesWithState;
    }

    public BaseState checkBaseState(Base base){
        for(BaseInfoRecord record: this.bases){
            if(record.getBase().equals(base)){
                return record.getBaseState();
            }
        }
        return null;
    }

    public void markAllNeutralBasesAsUnknown(){
        List<Base> bases = this.bases.stream().map(BaseInfoRecord::getBase).collect(Collectors.toList());
        bases.stream().filter(base -> this.checkBaseState(base) == BaseState.NEUTRAL).forEach(this::markBaseAsUnknown);
    }

    public boolean checkIfBaseIsTaken(Base base){
        return this.checkBaseState(base) == BaseState.ENEMY || this.checkBaseState(base) == BaseState.MINE;
    }

    public List<BaseInfoRecord> getBases() {
        return bases;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
