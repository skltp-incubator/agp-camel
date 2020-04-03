package se.skltp.aggregatingservices.logging;

import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.slf4j.helpers.MessageFormatter;


@Log4j2
public class LogMessageFormatter {

  private static final String MSG_ID = "skltp-messages";
  private static final String LOG_STRING = MSG_ID +
      "\n** {}.start ***********************************************************" +
      "\nLogMessage={}\nServiceImpl={}\nHost={}\nComponentId={}\nEndpoint={}\nMessageId={}\nBusinessCorrelationId={}\nExtraInfo={}\nPayload={}" +
      "{}" + // Placeholder for stack trace info if an error is logged
      "\n** {}.end *************************************************************";


  private LogMessageFormatter() {
    // Static utility class
  }



  protected static String format(String logEventName, LogEntry logEntry) {

    String logMessage              = logEntry.getLogMessage();
    String serviceImplementation   = logEntry.getServiceImpl();
    String host                    = logEntry.getHost();
    String componentId             = logEntry.getComponentId();
    String endpoint                = logEntry.getEndpoint();
    String messageId               = logEntry.getMessageId();
    String businessCorrelationId   = logEntry.getBusinessCorrelationId();
    String payload                 = logEntry.getPayload();
    String extraInfoString         = extraInfoToString(logEntry.getExtrainfo());

    //@TODO
    StringBuilder stackTrace = new StringBuilder();
//    LogMessageExceptionType lmeException = logEntry.getMessageInfo().getException();
//    if (lmeException != null) {
//      stackTrace.append('\n').append("Stacktrace=").append(lmeException.getStackTrace());
//    }
    return MessageFormatter
        .arrayFormat(LOG_STRING, new String[] {logEventName, logMessage, serviceImplementation,
           host, componentId, endpoint, messageId, businessCorrelationId, extraInfoString, payload, stackTrace.toString(), logEventName}).getMessage();
  }


  private static String extraInfoToString(Map extraInfo) {

    if (extraInfo == null) {
      return "";
    }

    StringBuilder extraInfoString = new StringBuilder();
    extraInfo.forEach((k,v)->extraInfoString.append("\n-").append(k).append("=").append(v));
    return extraInfoString.toString();
  }

}
