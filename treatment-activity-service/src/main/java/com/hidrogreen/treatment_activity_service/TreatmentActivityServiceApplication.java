package com.hidrogreen.treatment_activity_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
@EnableRabbit
public class TreatmentActivityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TreatmentActivityServiceApplication.class, args);
    }

}
