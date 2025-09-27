package com.hirehack.hirehack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate; // Import this

@SpringBootApplication
@EnableScheduling
public class HirehackApplication {

    public static void main(String[] args) {
        SpringApplication.run(HirehackApplication.class, args);
    }

    @Bean // This annotation tells Spring to manage this object
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}