package services;

import bwapi.Unit;
import bwapi.UnitType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BuildingService {
    List<Unit> buildings = new ArrayList<>();

    public int countBuildingsOfType(UnitType demandedBuildingType){
        return (int)this.buildings.stream().filter(Objects::nonNull).filter(i -> i.getType().equals(demandedBuildingType)).count();
    }

    public void addBuilding(Unit unit){
        this.buildings.add(unit);
    }

    public void removeBuilding(Unit unit){
        this.buildings.remove(unit);
    }
}
