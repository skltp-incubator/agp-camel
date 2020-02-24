package se.skltp.aggregatingservices.constants;

public class AgpProperties {
  public static final String AGP_SERVICE_HANDLER = "AgpServiceFactory";
  public static final String AGP_ORIGINAL_QUERY = "QueryObject";
  public static final String AGP_PRODUCER_ROUTE_NAME = "ProducerRouteName";
  public static final String AGP_TAK_CONTRACT_NAME = "TakContractName";
  public static final String AGP_VP_SENDER_ID = "x-vp-sender-id";
  public static final String AGP_VP_INSTANCE_ID = "x-vp-instance-id";
  public static final String AGP_RIVTA_ORIGINAL_CONSUMER_ID = "x-rivta-original-serviceconsumer-hsaid";
  public static final String AGP_SKLTP_CORRELATION_ID = "x-skltp-correlation-id";
  //Move to HttpHeaders, if more items show up...
  public static final String HEADER_CONTENT_TYPE = "Content-type";
}
