package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_REQ_IN;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_REQ_OUT;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_IN;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_OUT;

import javax.xml.namespace.QName;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.ext.logging.event.EventType;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;


public class PayloadLogEventSender implements LogEventSender {

  private static final Marker PAYLOAD_MARKER = MarkerManager.getMarker("PAYLOAD");

  @Override
  public void send(LogEvent event) {
    Logger log = getLogger(event);
    log.info(PAYLOAD_MARKER, getLogMessage(event));
  }

  protected Logger getLogger(final LogEvent event) {
    final String cat = "se.skltp.aggregatingservices.logging.payload." + event.getPortTypeName().getLocalPart() + "." + event.getType();
    return LogManager.getLogger(cat);
  }

  private static String localPart(QName name) {
    return name == null ? null : name.getLocalPart();
  }

  public static String getLogMessage(LogEvent event) {
    StringBuilder b = new StringBuilder();
    b.append("skltp-payload").append('\n');
    write(b, "LogMessage", type2LogMessage(event.getType()));
    write(b, "Address", event.getAddress());
    write(b, "HttpMethod", event.getHttpMethod());
    write(b, "Content-Type", event.getContentType());
    write(b, "ResponseCode", event.getResponseCode());
    write(b, "ExchangeId", event.getExchangeId());
    if (event.getServiceName() != null) {
      write(b, "ServiceName", localPart(event.getServiceName()));
      write(b, "PortName", localPart(event.getPortName()));
      write(b, "PortTypeName", localPart(event.getPortTypeName()));
    }

    if (event.getFullContentFile() != null) {
      write(b, "FullContentFile", event.getFullContentFile().getAbsolutePath());
    }

    write(b, "Headers", event.getHeaders().toString());
    if (!StringUtils.isEmpty(event.getPayload())) {
      write(b, "Payload", event.getPayload());
    }

    return b.toString();
  }

  private static String type2LogMessage(EventType type) {
    switch(type){
      case REQ_IN:
        return MSG_TYPE_LOG_REQ_IN;

      case REQ_OUT:
        return MSG_TYPE_LOG_REQ_OUT;

      case RESP_IN:
        return MSG_TYPE_LOG_RESP_IN;

      case RESP_OUT:
        return MSG_TYPE_LOG_RESP_OUT;

      default:
        return type.name();
    }
  }


  protected static void write(StringBuilder b, String key, String value) {
    if (value != null) {
      b.append("    ").append(key).append(": ").append(value).append("\n");
    }

  }
}
