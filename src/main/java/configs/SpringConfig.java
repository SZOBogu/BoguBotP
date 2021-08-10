package configs;

import bots.Bot;
import bwapi.BWClient;
import managers.MilitaryManager;
import org.springframework.context.annotation.Bean;
import managers.BuildingManager;
import managers.DemandManager;
import managers.WorkerManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public WorkerManager workerManager(){
        return new WorkerManager();
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
