package helpers;

import bwapi.Player;
import bwapi.UnitType;

import java.util.*;

public class DemandLimitMap {
    private static final HashMap<UnitType, Integer> limitTable = new HashMap<>();
    private static Player player;

    public static void init(){
        //buildings
        limitTable.put(UnitType.Protoss_Nexus, 1);
        limitTable.put(UnitType.Protoss_Pylon, 1);
        limitTable.put(UnitType.Protoss_Assimilator, 1);
        limitTable.put(UnitType.Protoss_Gateway, 0);
        limitTable.put(UnitType.Protoss_Forge, 0);
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

    public static void updateLimits(UnitType type){
        updateLimits(type, false);
    }

    public static void updateLimits(UnitType type, boolean remove){
        if(type == UnitType.Protoss_Nexus){
            nexus();
        }
        else if(type == UnitType.Protoss_Gateway){
            gateway();
        }
        else if(type == UnitType.Protoss_Forge){
            forge();
        }
        else if(type == UnitType.Protoss_Cybernetics_Core){
            cyberneticsCore();
        }
        else if(type == UnitType.Protoss_Robotics_Facility){
            roboticsFacility();
        }
        else if(type == UnitType.Protoss_Stargate){
            stargate();
        }
        else if(type == UnitType.Protoss_Citadel_of_Adun){
            citadelOfAdun();
        }
        else if(type == UnitType.Protoss_Templar_Archives){
            templarArchives();
        }
        else if(type == UnitType.Protoss_Robotics_Support_Bay){
            roboticsSupportBay();
        }
        else if(type == UnitType.Protoss_Observatory){
            observatory();
        }
        else if(type == UnitType.Protoss_Fleet_Beacon){
            fleetBeacon();
        }
        else if(type == UnitType.Protoss_Arbiter_Tribunal){
            arbiterTribunal();
        }

    }
    public static void setRecord(UnitType type, int limit){
        limitTable.put(type, limit);
    }

    public static int getLimit(UnitType type){
        return limitTable.get(type);
    }

    private static void nexus(){
        int gets = limitTable.get(UnitType.Protoss_Gateway);
        limitTable.put(UnitType.Protoss_Gateway, Math.max(1, 3 * gets));
        int nexuses = limitTable.get(UnitType.Protoss_Nexus);
        limitTable.put(UnitType.Protoss_Probe, nexuses * 2);
        limitTable.put(UnitType.Protoss_Forge, 2);
    }
    private static void gateway(){
        int gateways = limitTable.get(UnitType.Protoss_Zealot) + 1;
        limitTable.put(UnitType.Protoss_Zealot, gateways);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_Dragoon))
            limitTable.put(UnitType.Protoss_Dragoon, gateways);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_High_Templar))
            limitTable.put(UnitType.Protoss_High_Templar, gateways);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_Dark_Templar))
            limitTable.put(UnitType.Protoss_Dark_Templar, gateways);
        limitTable.put(UnitType.Protoss_Cybernetics_Core, 1);
        limitTable.put(UnitType.Protoss_Shield_Battery, 1);
        pylon();

    }
    private static void forge(){
        int nexuses = Math.max(5, 3 * limitTable.get(UnitType.Protoss_Nexus));
        limitTable.put(UnitType.Protoss_Photon_Cannon, nexuses);
    }

    private static void cyberneticsCore(){
        int gateways = limitTable.get(UnitType.Protoss_Gateway);
        int nexuses = limitTable.get(UnitType.Protoss_Nexus);
        limitTable.put(UnitType.Protoss_Dragoon, gateways);
        limitTable.put(UnitType.Protoss_Citadel_of_Adun, 1);
        limitTable.put(UnitType.Protoss_Stargate, nexuses);
        limitTable.put(UnitType.Protoss_Robotics_Facility, 2);
    }

    private static void roboticsFacility(){
        int robos = limitTable.get(UnitType.Protoss_Shuttle) + 1;
        limitTable.put(UnitType.Protoss_Shuttle, robos);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_Reaver))
            limitTable.put(UnitType.Protoss_Reaver, robos);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_Observer))
            limitTable.put(UnitType.Protoss_Stargate, robos);
        limitTable.put(UnitType.Protoss_Robotics_Support_Bay, 1);
        limitTable.put(UnitType.Protoss_Observatory, 1);
        pylon();
    }

    private static void stargate(){
        int stargates = limitTable.get(UnitType.Protoss_Scout) + 1;
        limitTable.put(UnitType.Protoss_Scout, stargates);
        limitTable.put(UnitType.Protoss_Corsair, stargates);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_Carrier))
            limitTable.put(UnitType.Protoss_Carrier, stargates);
        if(player.hasUnitTypeRequirement(UnitType.Protoss_Arbiter))
            limitTable.put(UnitType.Protoss_Arbiter, stargates);
        limitTable.put(UnitType.Protoss_Fleet_Beacon, 1);
        pylon();

    }

    private static void citadelOfAdun(){
        limitTable.put(UnitType.Protoss_Templar_Archives, 1);
    }

    private static void templarArchives(){
        int gateways = limitTable.get(UnitType.Protoss_Gateway);
        limitTable.put(UnitType.Protoss_High_Templar, gateways);
        limitTable.put(UnitType.Protoss_Dark_Templar, gateways);
        limitTable.put(UnitType.Protoss_Archon, gateways);
        limitTable.put(UnitType.Protoss_Dark_Archon, gateways);
    }

    private static void roboticsSupportBay(){
        int robos = limitTable.get(UnitType.Protoss_Robotics_Facility);
        limitTable.put(UnitType.Protoss_Reaver, robos);
    }

    private static void observatory(){
        int robos = limitTable.get(UnitType.Protoss_Robotics_Facility);
        limitTable.put(UnitType.Protoss_Observer, robos);
    }

    private static void fleetBeacon(){
        int stargates = limitTable.get(UnitType.Protoss_Stargate);
        limitTable.put(UnitType.Protoss_Carrier, stargates);
    }

    private static void arbiterTribunal(){
        int stargates = limitTable.get(UnitType.Protoss_Stargate);
        limitTable.put(UnitType.Protoss_Arbiter, stargates);
    }

    private static void pylon(){
        int gates = limitTable.get(UnitType.Protoss_Zealot);
        int robos = limitTable.get(UnitType.Protoss_Shuttle);
        int stars = limitTable.get(UnitType.Protoss_Scout);

        List<Integer> list = new ArrayList<>(Arrays.asList(1, gates, robos, stars));
        int pylonLimit = Collections.max(list);
        limitTable.put(UnitType.Protoss_Pylon, pylonLimit);
    }

    public static void setPlayer(Player player) {
        DemandLimitMap.player = player;
    }
}