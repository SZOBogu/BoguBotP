package helpers;

import bwapi.UnitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildOrder {
    /*
        8/9 — Pylon
        10/17 — Gateway
        11/17 — Assimilator
        13/17 — Cybernetics Core
        15/17 — Gateway
     */
    private List<BuildOrderEntry> buildOrder;

    public BuildOrder(){
        this.buildOrder = new ArrayList<>(Arrays.asList(
//                UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Probe, //initial 4 workers
                new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Probe),
                new BuildOrderEntry(UnitType.Protoss_Pylon), new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Probe),
                new BuildOrderEntry(UnitType.Protoss_Gateway), new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Assimilator),
                new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Cybernetics_Core),
                new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Probe), new BuildOrderEntry(UnitType.Protoss_Gateway),
                new BuildOrderEntry(UnitType.Protoss_Dragoon)
        ));
        System.out.println("Position in build order: " + this.getBuildOrderPosition() + " next thing in build order: " + this.getNextThingInBuildOrder());
    }

    public List<BuildOrderEntry> getBuildOrder() {
        return buildOrder;
    }

    public void markAsBuilt(){
        System.out.println("Position in build order: " + this.getBuildOrderPosition() + " next thing in build order: " + this.getNextThingInBuildOrder());
        int index = this.getBuildOrderPosition();
        this.buildOrder.get(index).setChecked(true);
    }

    public UnitType getNextThingInBuildOrder(){
        try{
           return this.buildOrder.get(this.getBuildOrderPosition()).getUnitType();
        }
        catch(IndexOutOfBoundsException indexOutOfBoundsException){
            return UnitType.Protoss_Dragoon;
        }
    }

    public boolean isComplete(){
        return this.getBuildOrderPosition() > (this.buildOrder.size() - 1);
    }

    public int getBuildOrderPosition(){
        for(int i = 0; i < this.buildOrder.size(); i++){
            if(!this.buildOrder.get(i).isChecked()){
                return i;
            }
        }
        //TODO: test
        return buildOrder.size();
    }

    @Override
    public String toString() {
        return "BuildOrder{" +
                "buildOrder=" + buildOrder +
                ", currentBuildOrderPosition=" + getBuildOrderPosition() +
                '}';
    }
}
