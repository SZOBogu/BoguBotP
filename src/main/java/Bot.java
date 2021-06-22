import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;

public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    @Override
    public void onFrame(){
        Game game = bwClient.getGame();
        game.drawTextScreen(100, 100, "Hello world");
    }

    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.bwClient = new BWClient(bot);
        bot.bwClient.startGame();
    }
}
