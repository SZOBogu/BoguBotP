package managers;

import bwapi.TilePosition;
import bwapi.Unit;
import bwem.BWMap;
import bwem.Base;
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
    private Unit scout;
    private TilePosition rallyPoint;

    @Override
    public void add(Unit unit){
        this.militaryUnits.add(unit);
        unit.move(this.rallyPoint.toPosition());
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
        if(this.scout == null && !this.militaryUnits.isEmpty()){
            this.scout = this.militaryUnits.get(0);
            this.tellScoutToGetToNextBase();
        }
    }

    public void tellScoutToGetToNextBase(){
        Base nextBase = mapHelper.getBasesClosestToTilePosition(scout.getTilePosition()).get(this.indexOfLastScoutedBase + 1);

        if(!this.scout.isMoving()) {
            try {
                scout.move(nextBase.getCenter());
            } catch (ArrayIndexOutOfBoundsException ex) {
                this.indexOfLastScoutedBase = 0;
                this.tellScoutToGetToNextBase();
            }
            if(this.scout.getTilePosition().getApproxDistance(nextBase.getLocation()) < 5){
                this.indexOfLastScoutedBase++;
                //never seems to reach the base
                System.out.println("Base reached");
                this.scout.stop();
                this.tellScoutToGetToNextBase();
            }
        }
    }

    public void tellScoutToSideStep(){
        TilePosition temp = this.scout.getTilePosition();
        this.scout.move(new TilePosition(temp.x + 10, temp.y).toPosition());
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }

    public void setGlobalRallyPoint(){
        TilePosition temp = mapHelper.getListOfBases().get(1).getLocation();
        this.rallyPoint = new TilePosition(temp.x + 15, temp.y);
    }
}
