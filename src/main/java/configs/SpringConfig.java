package configs;

import bots.Bot;
import bwapi.BWClient;
import bwapi.Game;
import helpers.BaseInfoTracker;
import managers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan({"main", "helpers", "aspects", "managers", "bots", "exceptions", "applicationContext"})
public class SpringConfig {
    @Bean
    public GlobalBasesManager expansionManager(){
        return new GlobalBasesManager();
    }

    @Bean
    public BuildingManager buildingManager() { return new BuildingManager();}

    @Bean
    public MilitaryManager militaryManager() { return new MilitaryManager();}

    @Bean
    public Bot bot(){
        return new Bot();
    }

    @Bean
    public BWClient bwClient() {
        return new BWClient(this.bot());
    }

    @Bean
    public BaseInfoTracker baseInfoTracker() {return new BaseInfoTracker();}
}
