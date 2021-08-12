package helpers;

import bwapi.Unit;
import bwapi.UnitType;

import java.util.Arrays;
import java.util.List;

public class MilitaryUnitChecker {
    private final static List<UnitType> protossMilitaryUnitTypeList = Arrays.asList(UnitType.Protoss_Zealot, UnitType.Protoss_Dragoon,
            UnitType.Protoss_High_Templar, UnitType.Protoss_Dark_Templar, UnitType.Protoss_Archon, UnitType.Protoss_Dark_Archon,
            UnitType.Protoss_Reaver, UnitType.Protoss_Scout, UnitType.Protoss_Corsair, UnitType.Protoss_Carrier, UnitType.Protoss_Arbiter,
            UnitType.Protoss_Scarab);

    private final static List<UnitType> terranMilitaryUnitTypeList = Arrays.asList(UnitType.Terran_Marine, UnitType.Terran_Firebat,
            UnitType.Terran_Medic, UnitType.Terran_Ghost, UnitType.Terran_Vulture, UnitType.Terran_Siege_Tank_Tank_Mode,
            UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Goliath, UnitType.Terran_Wraith, UnitType.Terran_Valkyrie,
            UnitType.Terran_Science_Vessel, UnitType.Terran_Battlecruiser, UnitType.Terran_Vulture_Spider_Mine);

    private final static List<UnitType> zergMilitaryUnitTypeList = Arrays.asList(UnitType.Zerg_Broodling, UnitType.Zerg_Zergling,
            UnitType.Zerg_Hydralisk, UnitType.Zerg_Lurker, UnitType.Zerg_Mutalisk, UnitType.Zerg_Scourge,
            UnitType.Zerg_Queen, UnitType.Zerg_Devourer, UnitType.Zerg_Guardian, UnitType.Zerg_Ultralisk, UnitType.Zerg_Defiler);

    public static boolean checkIfUnitIsMilitary(Unit unit){
        return protossMilitaryUnitTypeList.contains(unit.getType()) || terranMilitaryUnitTypeList.contains(unit.getType()) || zergMilitaryUnitTypeList.contains(unit.getType());
    }
}
