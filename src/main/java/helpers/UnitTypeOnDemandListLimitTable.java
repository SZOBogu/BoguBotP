package helpers;

import bwapi.UnitType;

import java.util.HashMap;

public class UnitTypeOnDemandListLimitTable {
    private static HashMap<UnitType, Integer> limitTable;

    public static void init(){
        //buildings
        limitTable.put(UnitType.Protoss_Nexus, 1);
        limitTable.put(UnitType.Protoss_Pylon, 1);
        limitTable.put(UnitType.Protoss_Assimilator, 1);
        limitTable.put(UnitType.Protoss_Gateway, 1);
        limitTable.put(UnitType.Protoss_Forge, 1);
        limitTable.put(UnitType.Protoss_Photon_Cannon, 0);
        limitTable.put(UnitType.Protoss_Cybernetics_Core, 0);
        limitTable.put(UnitType.Protoss_Shield_Battery, 0);
        limitTable.put(UnitType.Protoss_Robotics_Facility, 0);
        limitTable.put(UnitType.Protoss_Stargate, 0);
        limitTable.put(UnitType.Protoss_Citadel_of_Adun, 0);
        limitTable.put(UnitType.Protoss_Robotics_Support_Bay, 0);
        limitTable.put(UnitType.Protoss_Fleet_Beacon, 0);
        limitTable.put(UnitType.Protoss_Templar_Archives, 0);
        limitTable.put(UnitType.Protoss_Observatory, 0);
        limitTable.put(UnitType.Protoss_Arbiter_Tribunal, 0);
        //units
        limitTable.put(UnitType.Protoss_Probe, 0);
        limitTable.put(UnitType.Protoss_Zealot, 0);
        limitTable.put(UnitType.Protoss_Dragoon, 0);
        limitTable.put(UnitType.Protoss_High_Templar, 0);
        limitTable.put(UnitType.Protoss_Dark_Templar, 0);
        limitTable.put(UnitType.Protoss_Shuttle, 0);
        limitTable.put(UnitType.Protoss_Reaver, 0);
        limitTable.put(UnitType.Protoss_Observer, 0);
        limitTable.put(UnitType.Protoss_Scout, 0);
        limitTable.put(UnitType.Protoss_Carrier, 0);
        limitTable.put(UnitType.Protoss_Arbiter, 0);
        limitTable.put(UnitType.Protoss_Corsair, 0);
    }

    public static void updateLimits(UnitType type, int quantity){

    }

    public static void setRecord(UnitType type, int limit){
        limitTable.put(type, limit);
    }

    private static void nexus(int quantity){
        int oldValue = limitTable.get(UnitType.Protoss_Gateway);
        limitTable.put(UnitType.Protoss_Gateway, Math.max(1, 3 * oldValue));
        oldValue = limitTable.get(UnitType.Protoss_Nexus);
        limitTable.put(UnitType.Protoss_Probe, oldValue + 1);
    }
    private static void gateway(int quantity){
        limitTable.put(UnitType.Protoss_Zealot, quantity);
        limitTable.put(UnitType.Protoss_Dragoon, quantity);
        limitTable.put(UnitType.Protoss_High_Templar, quantity);
        limitTable.put(UnitType.Protoss_Dark_Templar, quantity);
    }
    private static void roboticsFacility(){

    }
    private static void stargate(){

    }
}