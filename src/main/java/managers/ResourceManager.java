package managers;

import bwapi.Player;
import helpers.ProductionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojos.TextInGame;

import java.util.List;

@Component
public class ResourceManager implements IBroodWarManager{
    private Player player;
    private int totalMinerals;
    private int mineralsToLock;
    private int lockedMinerals;

    private int totalGas;
    private int gasToLock;
    private int lockedGas;

    private void updateResourceInfo(){
        this.totalMinerals = this.player.gatheredMinerals();
        this.totalGas = this.player.gatheredGas();

        this.lockedMinerals = Math.min(this.totalMinerals, this.mineralsToLock);
        this.lockedGas = Math.min(this.totalGas, this.gasToLock);
    }

    private void lockResources(ProductionOrder order){
        this.mineralsToLock += order.getUnitType().mineralPrice();
        this.gasToLock += order.getUnitType().gasPrice();
    }

    private int getNonLockedMinerals(){
        return Math.max(this.totalMinerals - this.mineralsToLock, 0);
    }

    private int getNonLockedGas(){
        return Math.max(this.totalGas - this.gasToLock, 0);
    }

    @Override
    public void manage() {
        this.updateResourceInfo();
    }

    @Override
    public List<TextInGame> getTextToWriteInGame() {
        return null;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
