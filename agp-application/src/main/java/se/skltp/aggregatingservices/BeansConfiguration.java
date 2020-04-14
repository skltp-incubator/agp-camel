package se.skltp.aggregatingservices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.skltp.aggregatingservices.logging.CxfLogging;

@Configuration
public class BeansConfiguration {

  @Bean
  public CxfLogging messageLogger(){
    return new CxfLogging();
  }

}
