package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedlaboratoryorderoutcome.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcome.v4.rivtabp21.GetLaboratoryOrderOutcomeResponderInterface;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "getaggregatedlaboratoryorderoutcome.v4")
public class AgpServiceConfiguration extends se.skltp.aggregatingservices.configuration.AgpServiceConfiguration {

  public static final String SCHEMA_PATH = "/schemas/clinicalprocess-healthcond-actoutcome/interactions/GetLaboratoryOrderOutcomeInteraction/GetLaboratoryOrderOutcomeInteraction_4.0_RIVTABP21.wsdl";

  public AgpServiceConfiguration() {
    setServiceName("GetLaboratoryOrderOutcome.V4");
    setTargetNamespace("urn:riv:clinicalprocess:healthcond:actoutcome:GetLaboratoryOrderOutcome:4:rivtabp21");

    // Set inbound defaults
    setInboundServiceURL("http://localhost:8081/GetAggregatedLaboratoryOrderOutcome/service/v4");
    setInboundServiceWsdl(SCHEMA_PATH);
    setInboundServiceClass(GetLaboratoryOrderOutcomeResponderInterface.class.getName());

    // Set outbound defaults
    setOutboundServiceURL("http://localhost:8083/vp");
    setOutboundServiceWsdl(SCHEMA_PATH);
    setOutboundServiceClass(GetLaboratoryOrderOutcomeResponderInterface.class.getName());

    // FindContent
    setEiServiceDomain("riv:clinicalprocess:healthcond:actoutcome");
    setEiCategorization("und-kkm-ure");
  }


}
