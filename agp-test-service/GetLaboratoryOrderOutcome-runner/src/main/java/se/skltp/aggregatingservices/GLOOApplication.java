package se.skltp.aggregatingservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"se.skltp.aggregatingservices","se.skltp.takcache"})
public class GLOOApplication {

  public static void main(String[] args) {
    SpringApplication.run(GLOOApplication.class, args);
  }

}
