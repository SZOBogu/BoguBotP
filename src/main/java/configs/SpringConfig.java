package configs;

import bots.Bot;
import bwapi.BWClient;
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
    public ExpansionManager expansionManager(){
        return new ExpansionManager();
    }

    @Bean
    public DemandManager demandManager() {
        return new DemandManager();
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
}
