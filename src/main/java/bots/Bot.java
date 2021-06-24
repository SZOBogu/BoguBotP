package bots;

import bwapi.*;
import configs.SpringConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import services.WorkerService;

@SpringBootApplication(scanBasePackages = {"services",  "helpers", "configs"})
public class Bot extends DefaultBWListener {
    private BWClient bwClient;

    @Autowired
    private WorkerService workerService;

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
        this.workerService.manage();
    }

    public void onUnitComplete(Unit unit){
        this.workerService.manage();
    }

    public void setBwClient(BWClient bwClient) {
        this.bwClient = bwClient;
    }

    public static void main(String[] args) {
//        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        Bot bot = new Bot();
        bot.bwClient = new BWClient(bot);
        bot.bwClient.startGame();
    }
}
