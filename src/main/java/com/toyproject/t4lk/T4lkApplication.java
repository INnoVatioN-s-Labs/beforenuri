package com.toyproject.t4lk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class T4lkApplication {

    public static void main(String[] args) {
        SpringApplication.run(T4lkApplication.class, args);
    }

}
