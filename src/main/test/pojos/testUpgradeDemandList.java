package pojos;

import bwapi.TechType;
import bwapi.UpgradeType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testUpgradeDemandList {
    private final UpgradeDemandList upgradeDemandList = new UpgradeDemandList();
    private final UpgradeType upgradeType = Mockito.mock(UpgradeType.class);
    private final UpgradeType upgradeType2 = Mockito.mock(UpgradeType.class);
    private final TechType techType = Mockito.mock(TechType.class);

    @Test
    void isEmpty(){
        assertTrue(upgradeDemandList.isEmpty());
    }

    @Test
    void demand(){
        assertTrue(upgradeDemandList.isEmpty());
        upgradeDemandList.demand(upgradeType);
        assertFalse(upgradeDemandList.isEmpty());
    }

    @Test
    void demandWrongType(){
        assertTrue(upgradeDemandList.isEmpty());
        upgradeDemandList.demand(techType);
        assertTrue(upgradeDemandList.isEmpty());
    }


    @Test
    void fulfillDemand(){
        assertTrue(upgradeDemandList.isEmpty());
        upgradeDemandList.demand(upgradeType);
        assertFalse(upgradeDemandList.isEmpty());
        upgradeDemandList.fulfillDemand(upgradeType);
        assertTrue(upgradeDemandList.isEmpty());
    }

    @Test
    void fulfillDemandNotInList(){
        assertTrue(upgradeDemandList.isEmpty());
        upgradeDemandList.demand(upgradeType);
        assertFalse(upgradeDemandList.isEmpty());
        upgradeDemandList.fulfillDemand(upgradeType2);
        assertFalse(upgradeDemandList.isEmpty());
    }

    @Test
    void fulfillDemandWrongType(){
        assertTrue(upgradeDemandList.isEmpty());
        upgradeDemandList.demand(upgradeType);
        assertFalse(upgradeDemandList.isEmpty());
        upgradeDemandList.fulfillDemand(techType);
        assertFalse(upgradeDemandList.isEmpty());
    }
}
