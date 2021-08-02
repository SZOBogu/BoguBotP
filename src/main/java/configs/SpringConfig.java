package configs;

import bots.Bot;
import bwapi.BWClient;
import org.springframework.context.annotation.Bean;
import managers.BuildingManager;
import managers.DemandManager;
import managers.WorkerManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public WorkerManager workerService(){
        return new WorkerManager();
    }

    @Bean
    public DemandManager demandService() {
        return new DemandManager();
    }

    @Bean
    public BuildingManager buildingService() { return new BuildingManager();}

    @Bean
    public Bot bot(){
        return new Bot();
    }

    @Bean
    public BWClient bwClient() {
        return new BWClient(this.bot());
    }
}
