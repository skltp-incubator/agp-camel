package se.skltp.aggregatingservices.utils;

import static org.apache.camel.test.junit4.TestSupport.assertStringContains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_SOURCE;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.NO_DATA_SYNCH_FAILED;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.core.LogEvent;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

public class AssertLoggingUtil {

  private static final Pattern receiverPattern = Pattern.compile("-receiverid=(.*)");
  private static final String LOGGER_NAME_REQ_IN = "se.skltp.aggregatingservices.logging.GetLaboratoryOrderOutcomeResponderInterface.REQ_IN";
  private static final String LOGGER_NAME_REQ_OUT = "se.skltp.aggregatingservices.logging.GetLaboratoryOrderOutcomeResponderInterface.REQ_OUT";
  private static final String LOGGER_NAME_RESP_IN = "se.skltp.aggregatingservices.logging.GetLaboratoryOrderOutcomeResponderInterface.RESP_IN";
  private static final String LOGGER_NAME_ERROR_IN = "se.skltp.aggregatingservices.logging.GetLaboratoryOrderOutcomeResponderInterface.FAULT_IN";
  private static final String LOGGER_NAME_RESP_OUT = "se.skltp.aggregatingservices.logging.GetLaboratoryOrderOutcomeResponderInterface.RESP_OUT";
  private static final String LOGGER_NAME_EI_REQ_OUT = "se.skltp.aggregatingservices.logging.FindContentResponderInterface.REQ_OUT";
  private static final String LOGGER_NAME_EI_RESP_IN = "se.skltp.aggregatingservices.logging.FindContentResponderInterface.RESP_IN";

  // Utility class
  private AssertLoggingUtil() {
  }

  public static void assertLogging(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    assertEquals(1, testLogAppender.getNumEvents(LOGGER_NAME_REQ_IN));
    assertEquals(1, testLogAppender.getNumEvents(LOGGER_NAME_RESP_OUT));
    assertEquals(1, testLogAppender.getNumEvents(LOGGER_NAME_EI_REQ_OUT));
    assertEquals(1, testLogAppender.getNumEvents(LOGGER_NAME_EI_RESP_IN));

    assertEquals(expectedResponse.numProducers(), testLogAppender.getNumEvents(LOGGER_NAME_REQ_OUT));
    assertEquals(expectedResponse.getNumProducerCallsOk(), testLogAppender.getNumEvents(LOGGER_NAME_RESP_IN));

    assertReqIn(testLogAppender, expectedResponse);
    assertRespOut(testLogAppender, expectedResponse);
    assertReqOutFindContent(testLogAppender, expectedResponse);
    assertRespInFindContent(testLogAppender, expectedResponse);
    assertReqOut(testLogAppender, expectedResponse);
    assertMsgIn(testLogAppender, expectedResponse, LOGGER_NAME_RESP_IN);
    assertMsgIn(testLogAppender, expectedResponse, LOGGER_NAME_ERROR_IN);
//    assertRespIn(testLogAppender, expectedResponse);
//    assertErrorIn(testLogAppender, expectedResponse);

  }

  private static void assertErrorIn(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final List<LogEvent> events = testLogAppender.getEvents(LOGGER_NAME_ERROR_IN);
    final List<String> expectedProducers = expectedResponse.getProducers().stream().filter(producer -> expectedResponse.getStatusCode(producer).equals(
        NO_DATA_SYNCH_FAILED)).collect(Collectors.toList());
    final List<String> loggedProducers = new ArrayList();

    for (LogEvent logEvent : events) {
      String eventMessage = logEvent.getMessage().getFormattedMessage();
      assertEventMessageCommon(eventMessage, "error-in");
      assertStringContains(eventMessage,
          "-wsdl_namespace=urn:riv:clinicalprocess:healthcond:actoutcome:GetLaboratoryOrderOutcome:4:rivtabp21");

      Matcher matcher = receiverPattern.matcher(eventMessage);
      assertTrue("No -receiverid found in logentry: \n" + eventMessage, matcher.find());
      String producer = matcher.group(1);
      loggedProducers.add(producer);
      assertStringContains(eventMessage, String.format("-responseCode=%d", expectedResponse.getResponseCode(producer)));


    }
    assertEquals("Unexpected number receivers logged in err-in", expectedProducers.size(), loggedProducers.size());
    assertThat(expectedProducers, containsInAnyOrder(loggedProducers.toArray()));
  }

  private static void assertRespIn(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final List<LogEvent> events = testLogAppender.getEvents(LOGGER_NAME_RESP_IN);
    final List<String> expectedProducers = expectedResponse.getProducers().stream().filter(producer -> expectedResponse.getStatusCode(producer).equals(
        DATA_FROM_SOURCE)).collect(Collectors.toList());
    final List<String> loggedProducers = new ArrayList();

    for (LogEvent logEvent : events) {
      String eventMessage = logEvent.getMessage().getFormattedMessage();
      assertEventMessageCommon(eventMessage, "resp-in");
      assertStringContains(eventMessage,
          "-wsdl_namespace=urn:riv:clinicalprocess:healthcond:actoutcome:GetLaboratoryOrderOutcome:4:rivtabp21");

      Matcher matcher = receiverPattern.matcher(eventMessage);
      assertTrue("No -receiverid found in logentry: \n" + eventMessage, matcher.find());
      String producer = matcher.group(1);
      loggedProducers.add(producer);
      assertStringContains(eventMessage, String.format("-responseCode=%d", expectedResponse.getResponseCode(producer)));
    }
    assertEquals("Unexpected number receivers logged in resp-in", expectedProducers.size(), loggedProducers.size());
    assertThat(expectedProducers, containsInAnyOrder(loggedProducers.toArray()));

  }

  private static void assertMsgIn(TestLogAppender testLogAppender, ExpectedResponse expectedResponse, String loggerName) {
    StatusCodeEnum statusCode = DATA_FROM_SOURCE;
    String logMessage = "resp-in";
    if(loggerName.equals(LOGGER_NAME_ERROR_IN)){
      statusCode = NO_DATA_SYNCH_FAILED;
      logMessage = "error-in";
    }

    final List<LogEvent> events = testLogAppender.getEvents(loggerName);
    final List<String> expectedProducers = getFilterProducers(expectedResponse, statusCode);
    final List<String> loggedProducers = new ArrayList();


    for (LogEvent logEvent : events) {
      String eventMessage = logEvent.getMessage().getFormattedMessage();
      assertEventMessageCommon(eventMessage, logMessage);
      assertStringContains(eventMessage,
          "-wsdl_namespace=urn:riv:clinicalprocess:healthcond:actoutcome:GetLaboratoryOrderOutcome:4:rivtabp21");

      Matcher matcher = receiverPattern.matcher(eventMessage);
      assertTrue("No -receiverid found in logentry: \n" + eventMessage, matcher.find());
      String producer = matcher.group(1);
      loggedProducers.add(producer);
      assertStringContains(eventMessage, String.format("-responseCode=%d", expectedResponse.getResponseCode(producer)));
    }
    assertEquals("Unexpected number receivers logged in "+logMessage, expectedProducers.size(), loggedProducers.size());
    assertThat(expectedProducers, containsInAnyOrder(loggedProducers.toArray()));

  }

  private static List<String> getFilterProducers(ExpectedResponse expectedResponse, final StatusCodeEnum statusCode) {
    return expectedResponse.getProducers().stream().filter(producer -> expectedResponse.getStatusCode(producer)==statusCode).collect(
        Collectors.toList());
  }



  private static void assertReqOut(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final List<LogEvent> events = testLogAppender.getEvents(LOGGER_NAME_REQ_OUT);
    final List<String> expectedProducers = new ArrayList<> (expectedResponse.getProducers());
    final List<String> loggedProducers = new ArrayList();


    for (LogEvent logEvent : events) {
      String eventMessage = logEvent.getMessage().getFormattedMessage();
      assertEventMessageCommon(eventMessage, "req-out");
      assertEventMessageSenders(eventMessage, expectedResponse.getLogSenderId(), expectedResponse.getLogOriginalSenderId());
      assertStringContains(eventMessage,
          "-wsdl_namespace=urn:riv:clinicalprocess:healthcond:actoutcome:GetLaboratoryOrderOutcome:4:rivtabp21");

      Matcher matcher = receiverPattern.matcher(eventMessage);
      assertTrue("No -receiverid found in logentry: \n" + eventMessage, matcher.find());
      loggedProducers.add(matcher.group(1));
    }
    assertEquals("Unexpected number receivers logged in req-out", expectedProducers.size(), loggedProducers.size());
    assertThat(expectedProducers, containsInAnyOrder(loggedProducers.toArray()));

  }


  private static void assertRespOut(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final String eventMessage = testLogAppender.getEventMessage(LOGGER_NAME_RESP_OUT, 0);
    assertEventMessageCommon(eventMessage, "resp-out");
    assertStringContains(eventMessage, String.format("-responseCode=%d", expectedResponse.responseCode));
    assertProcessingStatusLog(expectedResponse, eventMessage);
  }

  private static void assertReqIn(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final String eventMessage = testLogAppender.getEventMessage(LOGGER_NAME_REQ_IN, 0);
    assertEventMessageCommon(eventMessage, "req-in");
    assertEventMessageSenders(eventMessage, expectedResponse.getLogSenderId(), expectedResponse.getLogOriginalSenderId());
  }

  private static void assertReqOutFindContent(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final String eventMessage = testLogAppender.getEventMessage(LOGGER_NAME_EI_REQ_OUT, 0);
    assertEventMessageCommon(eventMessage, "req-out");
    assertEventMessageSenders(eventMessage, expectedResponse.getLogSenderIdEI(), expectedResponse.getLogOriginalSenderId());
    assertStringContains(eventMessage, "-receiverid=556500000");
    assertStringContains(eventMessage, "-wsdl_namespace=urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21");
  }

  private static void assertRespInFindContent(TestLogAppender testLogAppender, ExpectedResponse expectedResponse) {
    final String eventMessage = testLogAppender.getEventMessage(LOGGER_NAME_EI_RESP_IN, 0);
    assertEventMessageCommon(eventMessage, "resp-in");
    assertStringContains(eventMessage, "-responseCode=200");
    assertStringContains(eventMessage, "-receiverid=556500000");
    assertStringContains(eventMessage, "-wsdl_namespace=urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21");
  }

  private static void assertEventMessageCommon(String eventMessage, String logMessage) {
    assertStringContains(eventMessage, String.format("LogMessage=%s", logMessage));
    assertStringContains(eventMessage, "ComponentId=aggregating-services");
    assertStringContains(eventMessage, "ServiceImpl=GetLaboratoryOrderOutcome.V4");
    assertStringContains(eventMessage, "BusinessCorrelationId=test-corr-id");
  }

  private static void assertEventMessageSenders(String eventMessage, String senderId, String orgSenderId) {
    assertStringContains(eventMessage, String.format("-senderid=%s", senderId));
    assertStringContains(eventMessage, String.format("-originalServiceconsumerHsaid=%s", orgSenderId));
  }

  private static void assertProcessingStatusLog(ExpectedResponse expectedResponse, String eventMessage) {
    assertStringContains(eventMessage,
        String.format("-processingStatusCountFail=%d", expectedResponse.getNumProducerCallsFailed()));
    assertStringContains(eventMessage, String.format("-processingStatusCountTot=%d", expectedResponse.numProducers()));

    for (String producer : expectedResponse.getProducers()) {
      if (expectedResponse.getStatusCode(producer) == DATA_FROM_SOURCE) {
        assertStringContains(eventMessage,
            String.format("{\"logicalAddress\":\"%s\",\"statusCode\":\"DataFromSource\"}", producer));
      } else {
        assertStringContains(eventMessage,
            String.format("{\"logicalAddress\":\"%s\",\"statusCode\":\"NoDataSynchFailed\"", producer));
        assertStringContains(eventMessage, expectedResponse.getErrTxtPart(producer));
      }
    }
  }


}
