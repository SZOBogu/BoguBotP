package pojos;

import bwapi.TechType;
import bwapi.UnitType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class testUnitDemandList {
    private final UnitDemandList unitDemandList = new UnitDemandList();
    private final UnitType unitType = UnitType.Powerup_Mineral_Cluster_Type_2;
    private final UnitType unitType2 = UnitType.Protoss_Nexus;

    @Test
    void isEmpty(){
        assertTrue(unitDemandList.isEmpty());
    }

    @Test
    void demand() {
        assertTrue(unitDemandList.isEmpty());
        unitDemandList.demand(unitType);
        assertFalse(unitDemandList.isEmpty());
    }

    @Test
    void fulfillDemand(){
        assertTrue(unitDemandList.isEmpty());
        unitDemandList.demand(unitType);
        assertFalse(unitDemandList.isEmpty());
        unitDemandList.fulfillDemand(unitType);
        assertTrue(unitDemandList.isEmpty());
    }

    @Test
    void fulfillDemandNotInList(){
        assertTrue(unitDemandList.isEmpty());
        unitDemandList.demand(unitType);
        assertFalse(unitDemandList.isEmpty());
        unitDemandList.fulfillDemand(unitType2);
        assertFalse(unitDemandList.isEmpty());
    }
}
