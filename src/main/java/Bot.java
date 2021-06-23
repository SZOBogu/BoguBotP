import bwapi.*;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    @Override
    public void onFrame(){
        Game game = bwClient.getGame();
        Player player = game.self();
        game.drawTextScreen(20, 20, player.getName() +  " has " + player.minerals() + "minerals");
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
