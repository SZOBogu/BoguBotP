package main;

import applicationContext.MyApplicationContext;
import bots.Bot;
import bwapi.BWClient;
import bwapi.Game;
import bwapi.Player;
import configs.SpringConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import services.WorkerService;

@SpringBootApplication(scanBasePackages = {"services",  "helpers", "configs", "bots", "applicationContext"})
public class Main {
    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext();
        context.setApplicationContext(new AnnotationConfigApplicationContext(SpringConfig.class));

        ApplicationContext staticContext = MyApplicationContext.getApplicationContext();
        Bot bot = (Bot)staticContext.getBean("bot");

        BWClient bwClient = new BWClient(bot);
        bot.setBwClient(bwClient);

        bwClient.startGame();
    }
}