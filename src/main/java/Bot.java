import bwapi.*;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    @Override
    public void onFrame(){
        Game game = bwClient.getGame();
        Player player = game.self();
        game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + "minerals");

        //TODO: check whether player.getUnits returns unit list of whole race or unit list player has at disposal
        for(Unit unit : player.getUnits()){
            UnitType unitType = unit.getType();
            if(unitType.isBuilding() && !unitType.buildsWhat().isEmpty()){
                UnitType unitTypeToTrain = unitType.buildsWhat().get(0);
                if(game.canMake(unitTypeToTrain, unit)){
                    unit.train(unitTypeToTrain);
                }
            }
        }

        UnitType toBuild = player.getRace().getSupplyProvider();

        //Supply doubled cause Zerglings
        if (player.supplyTotal() - player.supplyUsed() <= 2 && player.supplyTotal() <= 400) {
            Unit builder = null;
            for (Unit unit : player.getUnits()) {
                if (unit.getType().isWorker() && (unit.isIdle() || unit.isGatheringMinerals())) {
                    builder = unit;
                    break;
                }
            }
            TilePosition buildLocation = game.getBuildLocation(toBuild, player.getStartLocation());
            builder.build(toBuild, buildLocation);
        }
    }

    public void onUnitComplete(Unit unit){
        if(unit.getType().isWorker()){
            Unit closestMineralPatch = null;
            int minDistance = Integer.MAX_VALUE;
            for(Unit mineralPatch: bwClient.getGame().getMinerals()){
                int tempDistance = unit.getDistance(mineralPatch);
                if(tempDistance < minDistance){
                    closestMineralPatch = mineralPatch;
                    minDistance = tempDistance;
                }
            }
            unit.gather(closestMineralPatch);
        }
    }

    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.bwClient = new BWClient(bot);
        bot.bwClient.startGame();
    }
}
