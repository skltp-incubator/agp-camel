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

  String takContract;

  String eiServiceDomain;
  String eiCategorization;

  String serviceFactoryClass;

  int messageContentListQueryIndex = 1;

}
