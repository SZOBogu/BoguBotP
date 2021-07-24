package configs;

import bots.Bot;
import org.springframework.context.annotation.Bean;
import pojos.MyBWClient;
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
    public Bot bot(){
        return new Bot();
    }

    @Bean
    public MyBWClient bwClient() {
        return new MyBWClient(this.bot());
    }
}
