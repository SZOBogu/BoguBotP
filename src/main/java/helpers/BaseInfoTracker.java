package helpers;

import bwapi.TilePosition;
import bwem.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BaseInfoTracker {
    private final List<MyPair<Base, BaseState>> bases = new ArrayList<>();
    private MapHelper mapHelper;

    public void init(MapHelper mapHelper){
        List<Base> bases = mapHelper.getBasesClosestToTilePosition(mapHelper.getMainBase().getLocation());

        for(Base base : bases){
            MyPair<Base, BaseState> pair = new MyPair<>(base, BaseState.UNKNOWN);
            this.bases.add(pair);
        }

        this.mapHelper = mapHelper;
    }

    public void markBaseAsMine(Base base){
        this.changeBaseState(base, BaseState.MINE);
    }

    public void markBaseAsNeutral(Base base){
        this.changeBaseState(base, BaseState.NEUTRAL);
    }

    public void markBaseAsEnemy(Base base){
        this.changeBaseState(base, BaseState.ENEMY);
    }

    public void markBaseAsUnknown(Base base){
        this.changeBaseState(base, BaseState.UNKNOWN);
    }

    private void changeBaseState(Base base, BaseState state){
        this.bases.stream().filter(b -> b.getKey().equals(base)).forEach(b -> b.setValue(state));
    }

    public Base getClosestBaseWithState(BaseState state){
        return this.getClosestBaseWithState(this.bases.get(0).getKey().getLocation(), state);
    }

    public Base getClosestBaseWithState(TilePosition tilePosition, BaseState state){
        List<Base> basesClosest = this.mapHelper.getBasesClosestToTilePosition(tilePosition);

        for(Base base : basesClosest) {
            for (MyPair<Base, BaseState> pair : this.bases) {
                if (pair.getValue() == state && pair.getKey().equals(base)) {
                    return pair.getKey();
                }
            }
        }
        return null;
    }

    public List<Base> getClosestBasesWithState(TilePosition tilePosition, BaseState state){
        List<Base> basesClosest = this.mapHelper.getBasesClosestToTilePosition(tilePosition);
        List<Base> basesWithState = new ArrayList<>();

        for(Base base : basesClosest) {
            for (MyPair<Base, BaseState> pair : this.bases) {
                if (pair.getValue() == state && pair.getKey().equals(base)) {
                    basesWithState.add(pair.getKey());
                }
            }
        }
        return basesWithState;
    }

    public BaseState checkBaseState(Base base){
        for(MyPair<Base, BaseState> pair: this.bases){
            if(pair.getKey().equals(base)){
                return pair.getValue();
            }
        }
        return null;
    }

    public void markAllNeutralBasesAsUnknown(){
        List<Base> bases = this.bases.stream().map(MyPair::getKey).collect(Collectors.toList());
        bases.stream().filter(base -> this.checkBaseState(base) == BaseState.NEUTRAL).forEach(this::markBaseAsUnknown);
    }

    public boolean checkIfBaseIsTaken(Base base){
        return this.checkBaseState(base) == BaseState.ENEMY || this.checkBaseState(base) == BaseState.MINE;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }
}
