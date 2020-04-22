package se.skltp.aggregatingservices;

import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.feature.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.skltp.aggregatingservices.logging.CxfLogging;
import se.skltp.aggregatingservices.logging.PayloadLoggingFeature;

@Configuration
public class BeansConfiguration {

  @Autowired
  PayloadLoggingFeature payloadLoggingFeature;

  @Bean
  public CxfLogging messageLogger(){
    return new CxfLogging();
  }


  @Bean
  public List<Feature> loggingFeatures(){
     final List<Feature> features = new ArrayList<>();
    features.add(payloadLoggingFeature);

    return features;
  }


}
