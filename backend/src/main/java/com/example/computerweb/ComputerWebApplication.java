package com.example.computerweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.example.computerweb.mapper")
@SpringBootApplication
public class ComputerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComputerWebApplication.class, args);
    }
} 