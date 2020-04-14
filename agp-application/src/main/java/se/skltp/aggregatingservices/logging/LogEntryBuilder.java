package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.constants.AgpHeaders.X_SKLTP_CORRELATION_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_COMPONENT_ID;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_REQ_IN;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_REQ_OUT;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_IN;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_OUT;

import java.net.InetAddress;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.logging.log4j.ThreadContext;

@Log4j2
public class LogEntryBuilder {

  // Static utility class
  private LogEntryBuilder() {
  }

  protected static String hostName = "UNKNOWN (UNKNOWN)";

  static {
    try {
      // Let's give it a try, fail silently...
      InetAddress host = InetAddress.getLocalHost();
      hostName = String.format("%s (%s)",
          host.getCanonicalHostName(),
          host.getHostAddress());
    } catch (Exception ex) {
      log.warn("Failed get runtime values for logging", ex);
    }
  }



  public static LogEntry createLogEntry(String logLevel, String messageType, Exchange exchange) {
    LogEntry logEntry = new LogEntry();

    String businessCorrelationId = exchange.getIn().getHeader(X_SKLTP_CORRELATION_ID, "", String.class);
    ThreadContext.put("corr.id", String.format("[%s]", businessCorrelationId));
    String messageId = exchange.getMessage().getMessageId();
    String componentId = exchange.getProperty(AGP_SERVICE_COMPONENT_ID, String.class);

    logEntry.setLogLevel(logLevel);
    logEntry.setLogMessage(messageType);
    logEntry.setServiceImpl(exchange.getFromRouteId());
    logEntry.setHost(hostName);
    logEntry.setComponentId(componentId);
    logEntry.setEndpoint(getEndpoint(messageType, exchange));
    logEntry.setMessageId(messageId);
    logEntry.setBusinessCorrelationId(businessCorrelationId);
    logEntry.setExtrainfo(LogExtraInfoBuilder.createExtraInfo(exchange));
    return logEntry;
  }

  private static String getEndpoint( String messageType, Exchange exchange){
    switch(messageType){
      case MSG_TYPE_LOG_REQ_IN:
      case MSG_TYPE_LOG_RESP_OUT:
          return ((CxfEndpoint)exchange.getFromEndpoint()).getAddress();
      case MSG_TYPE_LOG_REQ_OUT:
      case MSG_TYPE_LOG_RESP_IN:
          return getToEndpoint(exchange);
      default:
        return "Unknown";
    }
  }

  private static String getToEndpoint(Exchange exchange){
    final String toEndpoint = exchange.getProperty(Exchange.TO_ENDPOINT, String.class);
    if(toEndpoint.startsWith("cxf://")){
      int end = toEndpoint.indexOf("?");
      if(end > 6) {
        return toEndpoint.substring(6, toEndpoint.indexOf("?"));
      }
      return toEndpoint.substring(6);
    } else{
      return toEndpoint;
    }

  }

  /*
  ** logEvent-info.start ***********************************************************
  IntegrationScenarioId=
  ContractId=
  LogMessage=resp-out
      ServiceImpl=cache-flow
  Host=ind-ttjp-agp1.ind1.sth.basefarm.net (10.252.7.132)
  ComponentId=GetAggregatedCareContacts-v1
      Endpoint=vm://reply
  MessageId=57650267-72eb-11ea-9abb-005056a105f5
      BusinessCorrelationId=2d3cd3d6-0442-4b98-ae2f-7984697abed0
      BusinessContextId=
      ExtraInfo=
          -processingStatus=[{"logicalAddress":"LOAD-MOCKS","statusCode":"DataFromSource"}]
      -processingStatusCountFail=0
      -processingStatusCountTot=1
      -originalServiceconsumerHsaid=TSTNMT2321000156-B02
      Payload=
** logEvent-info.end *************************************************************
*/

}
