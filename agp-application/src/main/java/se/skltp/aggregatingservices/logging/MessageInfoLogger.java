package se.skltp.aggregatingservices.logging;

import static se.skltp.aggregatingservices.logging.LogEntry.LOG_EVENT_DEBUG;
import static se.skltp.aggregatingservices.logging.LogEntry.LOG_EVENT_INFO;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_REQ_IN;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_REQ_OUT;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_IN;
import static se.skltp.aggregatingservices.logging.LogEntry.MSG_TYPE_LOG_RESP_OUT;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MessageInfoLogger {


  public static final String REQ_IN = "se.skltp.aggregatingservices.logging.req.in";
  public static final String REQ_OUT = "se.skltp.aggregatingservices.logging.req.out";
  public static final String RESP_IN = "se.skltp.aggregatingservices.logging.resp.in";
  public static final String RESP_OUT = "se.skltp.aggregatingservices.logging.resp.out";
  public static final String REQ_ERROR = "se.skltp.aggregatingservices.logging.error";

  private static final Logger LOGGER_REQ_IN = LogManager.getLogger(REQ_IN);
  private static final Logger LOGGER_REQ_OUT = LogManager.getLogger(REQ_OUT);
  private static final Logger LOGGER_RESP_IN = LogManager.getLogger(RESP_IN);
  private static final Logger LOGGER_RESP_OUT = LogManager.getLogger(RESP_OUT);
  private static final Logger LOGGER_ERROR = LogManager.getLogger(REQ_ERROR);

  public static void logReqIn(Exchange exchange) {
    log(LOGGER_REQ_IN, exchange, MSG_TYPE_LOG_REQ_IN);
  }

  public static void logReqOut(Exchange exchange) {
    log(LOGGER_REQ_OUT, exchange, MSG_TYPE_LOG_REQ_OUT);
  }

  public static void logRespIn(Exchange exchange) {
    log(LOGGER_RESP_IN, exchange, MSG_TYPE_LOG_RESP_IN);
  }

  public static void logRespOut(Exchange exchange) {
    log(LOGGER_RESP_OUT, exchange, MSG_TYPE_LOG_RESP_OUT);
  }

//  public void logError(Exchange exchange, String stackTrace) {
//
//    try {
//      LogEntry logEntry = LogEntryBuilder.createLogEntry(MSG_TYPE_ERROR, exchange);
//      logEntry.getExtraInfo().put(LogExtraInfoBuilder.SOURCE, getClass().getName());
//      logEntry.getMessageInfo().setException(LogEntryBuilder.createMessageException(exchange, stackTrace));
//      String logMsg = LogMessageFormatter.format(LOG_EVENT_ERROR, logEntry);
//      LOGGER_ERROR.error(logMsg);
//
//    } catch (Exception e) {
//      LOGGER_ERROR.error("Failed log message: {}",MSG_TYPE_ERROR, e);
//    }
//  }

  public static void log(Logger log, Exchange exchange, String messageType) {
    try {
      if (log.isDebugEnabled()) {
        LogEntry logEntry = LogEntryBuilder.createLogEntry(LOG_EVENT_DEBUG, messageType, exchange);
        logEntry.setPayload(exchange.getIn().getBody(String.class));
        log.debug(logEntry);
      } else if (log.isInfoEnabled()) {
        LogEntry logEntry = LogEntryBuilder.createLogEntry(LOG_EVENT_INFO, messageType, exchange);
        log.info(logEntry);
      }

    } catch (Exception e) {
      log.error("Failed log message: {}", messageType, e);
    }
  }

}
