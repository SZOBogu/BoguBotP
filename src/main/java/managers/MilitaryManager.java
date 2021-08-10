package managers;

import bwapi.Unit;
import bwem.BWMap;

import java.util.ArrayList;
import java.util.List;

public class MilitaryManager implements IUnitManager{
    private final List<Unit> militaryUnits = new ArrayList<>();
//    List<Unit> transports = new ArrayList<>();  //shuttles
//    List<Unit> mobileDetectors = new ArrayList<>(); //observers

    private BWMap map;

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

    }
}
