package com.example.speech.aiservice.vn;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);

        // Get `ShutdownHandler` from Spring Context and register it in JVM
        ShutdownHandler shutdownHandler = context.getBean(ShutdownHandler.class);
        Runtime.getRuntime().addShutdownHook(shutdownHandler);
    }
}

