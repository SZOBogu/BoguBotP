package pojos;

import bwapi.TechType;
import bwapi.UnitType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testTechDemandList {
    private final TechDemandList techDemandList = new TechDemandList();
    private final TechType techType = TechType.Disruption_Web;
    private final TechType techType2 = TechType.Dark_Archon_Meld;

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
}
