package main;

import applicationContext.MyApplicationContext;
import bwapi.BWClient;
import configs.SpringConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication(scanBasePackages = {"managers",  "helpers", "configs", "bots", "applicationContext"})
public class Main {
    public static void main(String[] args) {
        MyApplicationContext context = new MyApplicationContext();
        context.setApplicationContext(new AnnotationConfigApplicationContext(SpringConfig.class));

        BWClient bwClient = (BWClient) MyApplicationContext.getBean("bwClient");
        bwClient.startGame();
    }
}
