package oit.is.team7.quiz_7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Quiz7Application {

  public static void main(String[] args) {
    SpringApplication.run(Quiz7Application.class, args);
  }

}
