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
    private List<UnitType> buildOrder;
    private int currentBuildOrderPosition;

    public BuildOrder(){
        this.buildOrder = new ArrayList<>(Arrays.asList(
//                UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Probe, //initial 4 workers
                UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Probe,
                UnitType.Protoss_Pylon, UnitType.Protoss_Probe, UnitType.Protoss_Probe,
                UnitType.Protoss_Gateway, UnitType.Protoss_Probe, UnitType.Protoss_Assimilator,
                UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Cybernetics_Core,
                UnitType.Protoss_Probe, UnitType.Protoss_Probe, UnitType.Protoss_Gateway, UnitType.Protoss_Dragoon
        ));
        this.currentBuildOrderPosition = 0;
        System.out.println("Position in build order: " + this.currentBuildOrderPosition + " next thing in build order: " + this.getNextThingInBuildOrder());
    }

    public List<UnitType> getBuildOrder() {
        return buildOrder;
    }

    public void markAsBuilt(){
        this.currentBuildOrderPosition++;
        System.out.println("Position in build order: " + this.currentBuildOrderPosition + " next thing in build order: " + this.getNextThingInBuildOrder());
    }

    public UnitType getNextThingInBuildOrder(){
        try{
           return this.buildOrder.get(this.currentBuildOrderPosition);
        }
        catch(IndexOutOfBoundsException indexOutOfBoundsException){
            return UnitType.Protoss_Dragoon;
        }
    }

    public boolean isComplete(){
        return this.currentBuildOrderPosition > (this.buildOrder.size() - 1);
    }

    @Override
    public String toString() {
        return "BuildOrder{" +
                "buildOrder=" + buildOrder +
                ", currentBuildOrderPosition=" + currentBuildOrderPosition +
                '}';
    }
}
