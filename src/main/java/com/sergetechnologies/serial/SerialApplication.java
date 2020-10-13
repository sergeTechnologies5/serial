package com.sergetechnologies.serial;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SerialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SerialApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(PortRead portRead) {
        return args -> {
            portRead.initialize();
            //portRead.write("#");
            //portRead.write("9");
        };

    }
}
