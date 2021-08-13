package managers;

import bwapi.Unit;
import bwem.BWMap;
import helpers.MapHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers
    private int indexOfLastScoutedBase = 0;

    private MapHelper mapHelper;

    @Override
    public void add(Unit unit){
        this.militaryUnits.add(unit);
    }

    @Override
    public void remove(Unit unit){
        this.militaryUnits.remove(unit);
    }

    @Override
    public void manage() {
//        Unit scout = this.militaryUnits.get(0);
//        scout.move(mapHelper.getBasesClosestToTilePosition(scout.getTilePosition()).get(1).getCenter());
//        this.indexOfLastScoutedBase++;
    }

    public void tellScoutToGetToNextBase(){
        Unit scout = this.militaryUnits.get(0);
        scout.move(mapHelper.getBasesClosestToTilePosition(scout.getTilePosition()).get(this.indexOfLastScoutedBase).getCenter());
        this.indexOfLastScoutedBase++;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }
}
