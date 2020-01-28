package se.skltp.aggregatingservices.configuration;

import lombok.Data;

@Data
public class AgpServiceConfiguration {
  String serviceName;

  String targetNamespace;

  String inboundServiceWsdl;
  String inboundServiceURL;
  String inboundServiceClass;

  String outboundServiceURL;
  String outboundServiceWsdl;
  String outboundServiceClass;

  String eiServiceDomain;
  String eiCategorization;

}
