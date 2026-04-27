package com.goggles.lecture_service;

import com.goggles.config.event.EventConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Import(EventConfig.class)
@SpringBootApplication(scanBasePackages = {"com.goggles"})
@EnableJpaAuditing
public class LectureServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(LectureServiceApplication.class, args);
  }
}
