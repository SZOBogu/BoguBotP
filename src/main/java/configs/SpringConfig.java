package configs;

import bots.Bot;
import bwapi.BWClient;
import org.springframework.context.annotation.Bean;
import services.BuildingService;
import services.DemandService;
import services.WorkerService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public WorkerService workerService(){
        return new WorkerService();
    }

    @Bean
    public DemandService demandService() {
        return new DemandService();
    }

    @Bean
    public BuildingService buildingService() { return new BuildingService();}

    @Bean
    public Bot bot(){
        return new Bot();
    }

    @Bean
    public BWClient bwClient() {
        return new BWClient(this.bot());
    }
}
