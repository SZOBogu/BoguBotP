package pojos;

import bwapi.TechType;
import bwapi.UnitType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testTechDemandList {
    private final TechDemandList techDemandList = new TechDemandList();
    private final TechType techType = Mockito.mock(TechType.class);
    private final TechType techType2 = Mockito.mock(TechType.class);
    private final UnitType unitType = Mockito.mock(UnitType.class);

    @Test
    void isEmpty(){
        assertTrue(techDemandList.isEmpty());
    }

    @Test
    void demand(){
        assertTrue(techDemandList.isEmpty());
        techDemandList.demand(techType);
        assertFalse(techDemandList.isEmpty());
    }

    @Test
    void demandWrongType(){
        assertTrue(techDemandList.isEmpty());
        techDemandList.demand(unitType);
        assertTrue(techDemandList.isEmpty());
    }


    @Test
    void fulfillDemand(){
        assertTrue(techDemandList.isEmpty());
        techDemandList.demand(techType);
        assertFalse(techDemandList.isEmpty());
        techDemandList.fulfillDemand(techType);
        assertTrue(techDemandList.isEmpty());
    }

    @Test
    void fulfillDemandNotInList(){
        assertTrue(techDemandList.isEmpty());
        techDemandList.demand(techType);
        assertFalse(techDemandList.isEmpty());
        techDemandList.fulfillDemand(techType2);
        assertFalse(techDemandList.isEmpty());
    }

    @Test
    void fulfillDemandWrongType(){
        assertTrue(techDemandList.isEmpty());
        techDemandList.demand(techType);
        assertFalse(techDemandList.isEmpty());
        techDemandList.fulfillDemand(unitType);
        assertFalse(techDemandList.isEmpty());
    }
}
