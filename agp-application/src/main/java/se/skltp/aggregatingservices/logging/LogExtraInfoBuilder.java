package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.constants.AgpHeaders.X_RIVTA_ORIGINAL_SERVICE_CONSUMER_HSA_ID;
import static se.skltp.aggregatingservices.constants.AgpHeaders.X_VP_SENDER_ID;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_OUT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import se.skltp.aggregatingservices.constants.AgpProperties;
import se.skltp.aggregatingservices.utils.EngagementProcessingStatusUtil;

public class LogExtraInfoBuilder {

  public static final String SENDER_ID ="senderid";
  public static final String RECEIVER_ID = "receiverid";
  public static final String ENGAGEMENT_PROCESSING_RESULT = "processingStatus";
  public static final String SERVICECONTRACT_NAMESPACE = "servicecontract_namespace";
//  public static final String WSDL_NAMESPACE = "wsdl_namespace";
  public static final String ORIGINAL_SERVICE_CONSUMER_HSA_ID = "originalServiceconsumerHsaid";
  public static final String TIME_ELAPSED = "time.elapsed";
  public static final String MESSAGE_LENGTH = "message.length";


  private LogExtraInfoBuilder() {
    // Static utility class
  }

  public static Map<String, String> createExtraInfo(Exchange exchange, String messageType) {
    ExtraInfoMap<String, String> extraInfo = new ExtraInfoMap<>();

    extraInfo.put(SENDER_ID, exchange.getIn().getHeader(X_VP_SENDER_ID, String.class));
    extraInfo.putNotNull(RECEIVER_ID, exchange.getProperty(AgpProperties.LOGICAL_ADDRESS, String.class));
    extraInfo.put(ORIGINAL_SERVICE_CONSUMER_HSA_ID, exchange.getIn().getHeader(X_RIVTA_ORIGINAL_SERVICE_CONSUMER_HSA_ID, String.class));

//    String serviceContractNS = exchange.getProperty(VPExchangeProperties.SERVICECONTRACT_NAMESPACE, String.class);
//    extraInfo.put(SERVICECONTRACT_NAMESPACE, serviceContractNS);
//    extraInfo.put(WSDL_NAMESPACE, createWsdlNamespace(serviceContractNS, rivVersion));
    if( messageType.equals(MSG_TYPE_LOG_RESP_OUT)){
      extraInfo.put(ENGAGEMENT_PROCESSING_RESULT, EngagementProcessingStatusUtil.logFormat(exchange));
    }

    extraInfo.put(TIME_ELAPSED, getElapsedTime(exchange).toString());
    return extraInfo;
  }


  private static String createWsdlNamespace(String serviceContractNS, String profile) {
    //  Convert from interaction target namespace
    //    urn:${domänPrefix}:${tjänsteDomän}:${tjänsteInteraktion}${roll}:${m}
    //  to wsdl target namespace
    //    urn:riv:${tjänsteDomän}:${tjänsteInteraktion}:m:${profilKortnamn}
    // See https://riv-ta.atlassian.net/wiki/spaces/RTA/pages/99593635/RIV+Tekniska+Anvisningar+Tj+nsteschema
    //   and https://riv-ta.atlassian.net/wiki/spaces/RTA/pages/77856888/RIV+Tekniska+Anvisningar+Basic+Profile+2.1
    if (serviceContractNS == null || profile == null) {
      return null;
    }
    return serviceContractNS.replace("Responder", "").concat(":").concat(profile);
  }

  private static Long getElapsedTime(Exchange exchange) {
    Date created = exchange.getProperty(Exchange.CREATED_TIMESTAMP, Date.class);

    return created==null ?  0 : new Date().getTime() - created.getTime();
  }

  private static String nullValue2Blank(String s) {
    return (s == null) ? "" : s;
  }


  private static class ExtraInfoMap<K, V> extends HashMap<K, V> {

    public V putNotNull(K key, V value) {
      return value == null ? null : put(key, value);
    }

    public V putNotEmpty(K key, V value) {
      return (value == null || ((String) value).isEmpty()) ? null : put(key, value);
    }
  }
}
