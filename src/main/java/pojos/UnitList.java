package pojos;

import bwapi.Unit;
import enums.UnitState;

import java.util.ArrayList;
import java.util.List;

public class UnitList {
    private List<UnitStateEntry> unitList;

    public UnitList() {
        this.unitList = new ArrayList<>();
    }

    public UnitStateEntry get(int index){
        return this.unitList.get(index);
    }

    public UnitStateEntry get(Unit unit){
        for(UnitStateEntry entry : this.unitList){
            if(entry.getUnit().equals(unit)){
                return entry;
            }
        }
        return null;
    }

    public void add(Unit unit){
        this.unitList.add(new UnitStateEntry(unit));
    }

    public void remove(Unit unit){
        this.unitList.removeIf(entry -> entry.getUnit().equals(unit));
    }

    public List<UnitStateEntry> getUnitsWithState(UnitState unitState){
        List<UnitStateEntry> entriesWithGivenState = new ArrayList<>();

        for(UnitStateEntry entry : this.unitList){
            if(entry.getUnitState() == unitState){
                entriesWithGivenState.add(entry);
            }
        }
        return entriesWithGivenState;
    }

    public int countUnitsWithState(UnitState state){
        return this.getUnitsWithState(state).size();
    }

    public List<UnitStateEntry> getUnitList() {
        return unitList;
    }

    public int size(){
        return this.unitList.size();
    }

    @Override
    public String toString() {
        return "UnitList{" +
                "unitList=" + unitList +
                '}';
    }
}
