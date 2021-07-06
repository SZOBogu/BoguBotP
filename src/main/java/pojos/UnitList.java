package pojos;

import bwapi.Unit;
import enums.WorkerRole;

import java.util.ArrayList;
import java.util.List;

public class UnitList {
    private List<WorkerRoleEntry> unitList;

    public UnitList() {
        this.unitList = new ArrayList<>();
    }

    public WorkerRoleEntry get(int index){
        return this.unitList.get(index);
    }

    public WorkerRoleEntry get(Unit unit){
        for(WorkerRoleEntry entry : this.unitList){
            if(entry.getUnit().equals(unit)){
                return entry;
            }
        }
        return null;
    }

    public void add(Unit unit){
        this.unitList.add(new WorkerRoleEntry(unit));
    }

    public void remove(Unit unit){
        this.unitList.removeIf(entry -> entry.getUnit().equals(unit));
    }

    public List<WorkerRoleEntry> getUnitsWithState(WorkerRole workerRole){
        List<WorkerRoleEntry> entriesWithGivenState = new ArrayList<>();

        for(WorkerRoleEntry entry : this.unitList){
            if(entry.getUnitState() == workerRole){
                entriesWithGivenState.add(entry);
            }
        }
        return entriesWithGivenState;
    }

    public int countUnitsWithState(WorkerRole state){
        return this.getUnitsWithState(state).size();
    }

    public List<WorkerRoleEntry> getUnitList() {
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
