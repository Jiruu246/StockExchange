package com.jiruu.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Main {
  public static void main(String[] args) {
    System.out.println("Starting Order Service");
    SpringApplication.run(Main.class, args);
  }
}
