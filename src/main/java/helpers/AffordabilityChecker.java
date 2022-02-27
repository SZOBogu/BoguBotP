package helpers;

import bwapi.*;

public class AffordabilityChecker {
    public static boolean canAfford(Player player, UnitType unit){
        return player.minerals() >= unit.mineralPrice() && player.gas() >= unit.gasPrice();
    }

    public static boolean canAfford(Player player, TechType tech){
        return player.minerals() >= tech.mineralPrice() && player.gas() >= tech.gasPrice();
    }

    public static boolean canAfford(Player player, UpgradeType upgrade){
        return player.minerals() >= upgrade.mineralPrice() && player.gas() >= upgrade.gasPrice();
    }
}
