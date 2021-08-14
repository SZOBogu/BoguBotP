package managers;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwem.BWMap;
import bwem.Base;
import helpers.MapHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers
    private int indexOfLastScoutedBase = 0;

    private MapHelper mapHelper;
    private Unit scout;
    private TilePosition rallyPoint;
    private Game game;

    private int frame = 0;

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
        if(frame % 10 == 0) {
            if (this.scout == null && !this.militaryUnits.isEmpty()) {
                this.scout = this.militaryUnits.get(0);
                this.tellScoutToGetToNextBase();
            }
        }

        this.frame++;
    }

    public void tellScoutToGetToNextBase(){
        Base nextBase = mapHelper.getBasesClosestToTilePosition(scout.getTilePosition()).get(this.indexOfLastScoutedBase + 1);

        if(!this.scout.isMoving() && !this.scout.isStuck()) {
            System.out.println("Scout isn't moving nor stuck");
            try {
                scout.move(nextBase.getCenter());
                System.out.println("Scout got move command");
            } catch (ArrayIndexOutOfBoundsException ex) {
                this.indexOfLastScoutedBase = 0;
                this.tellScoutToGetToNextBase();
            }
            if(this.scout.getTilePosition().getDistance(nextBase.getLocation()) < 10){
                this.indexOfLastScoutedBase++;
                //never seems to reach the base
                System.out.println("Base reached");
//                this.scout.stop();
            }
        }
        else if(this.scout.isStuck()){
            Random random = new Random();
            this.scout = this.militaryUnits.get(random.nextInt(this.militaryUnits.size()));
        }
    }

    public void tellScoutToSideStep(){
        TilePosition temp = this.scout.getTilePosition();
        this.scout.move(new TilePosition(temp.x + 10, temp.y).toPosition());
    }

    public void setGlobalRallyPoint(){
        TilePosition temp = mapHelper.getListOfBases().get(1).getLocation();
        this.rallyPoint = new TilePosition(temp.x + 15, temp.y);
        if(!this.rallyPoint.isValid(this.game)){
            this.rallyPoint = new TilePosition(temp.x - 15, temp.y);
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMapHelper(MapHelper mapHelper) {
        this.mapHelper = mapHelper;
    }







}
