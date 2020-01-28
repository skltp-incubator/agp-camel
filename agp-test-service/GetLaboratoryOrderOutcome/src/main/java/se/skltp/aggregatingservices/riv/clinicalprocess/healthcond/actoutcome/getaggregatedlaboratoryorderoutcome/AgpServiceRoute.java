package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedlaboratoryorderoutcome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.AgpServiceBaseRoute;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;

@Component
//@Profile("GetLaboratoryOrderOutcome")
public class AgpServiceRoute extends AgpServiceBaseRoute {

  @Autowired
  public AgpServiceRoute(AgpServiceFactoryImpl agpServiceFactory, AgpServiceConfiguration serviceConfiguration) {
    super(agpServiceFactory, serviceConfiguration);
  }
}
