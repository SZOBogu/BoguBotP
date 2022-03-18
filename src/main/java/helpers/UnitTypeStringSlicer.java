package helpers;

import bwapi.UnitType;

public class UnitTypeStringSlicer {
    private UnitTypeStringSlicer() {}

    public static String getNeatName(UnitType type){
        String s = type.toString().substring(0, 4);
        if(s.equals("Prot") || s.equals("Neut")){
            return UnitTypeStringSlicer.getNeatProtossOrNeutralName(type);
        }
        else if(s.equals("Terr")){
            return UnitTypeStringSlicer.getNeatTerranName(type);
        }
        else if(s.equals("Zerg")){
            return UnitTypeStringSlicer.getNeatZergName(type);
        }
        else{
            return type.toString();
        }
    }

    private static String getNeatProtossOrNeutralName(UnitType type){
        return type.toString().substring(8);
    }

    private static String getNeatTerranName(UnitType type){
        return type.toString().substring(7);
    }

    private static String getNeatZergName(UnitType type){
        return type.toString().substring(5);
    }
}
