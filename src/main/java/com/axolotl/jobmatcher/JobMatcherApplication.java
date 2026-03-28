package com.axolotl.jobmatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobMatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobMatcherApplication.class, args);
    }

}
