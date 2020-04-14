package se.skltp.aggregatingservices.logging;

import java.util.Map;
import lombok.Data;

@Data
public class LogEntry {
  public static final String LOG_EVENT_INFO = "logEvent-info";
  public static final String LOG_EVENT_ERROR = "logEvent-error";
  public static final String LOG_EVENT_DEBUG = "logEvent-debug";

  public static final String MSG_TYPE_LOG_REQ_IN = "req-in";
  public static final String MSG_TYPE_LOG_REQ_OUT = "req-out";
  public static final String MSG_TYPE_LOG_RESP_IN = "resp-in";
  public static final String MSG_TYPE_LOG_RESP_OUT = "resp-out";
  public static final String MSG_TYPE_ERROR = "error";

  private String logLevel;
  private String logMessage;
  private String serviceImpl;
  private String host;
  private String componentId;
  private String endpoint;
  private String messageId;
  private String businessCorrelationId;
  private String payload;
  private Map<String, String> extrainfo;

  @Override
  public String toString() {
    return LogEntryFormatter.format(this);
  }


}





