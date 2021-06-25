package configs;

import bots.Bot;
import bwapi.BWClient;
import bwapi.Game;
import bwapi.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import services.WorkerService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public WorkerService workerService(){
        return new WorkerService();
    }

    @Bean
    public Bot bot(){
        return new Bot();
    }

//    @Bean
//    @Autowired
//    public Player player(Game game){
//        return game.self();
//    }
//
//    @Bean
//    @Autowired
//    public BWClient bwClient(Bot bot){
//        return new BWClient(bot);
//    }
//
//    @Bean
//    @Autowired
//    public Game game(BWClient bwClient){
//        return bwClient.getGame();
//    }
}
