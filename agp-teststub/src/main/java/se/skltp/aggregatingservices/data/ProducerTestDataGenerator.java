package se.skltp.aggregatingservices.data;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;


@Log4j2
public abstract class ProducerTestDataGenerator {


  private long serviceTimeoutMs;

  public void setServiceTimeoutMs(long serviceTimeoutMs) {
    this.serviceTimeoutMs = serviceTimeoutMs;
  }


  public ProducerTestDataGenerator() {
    initDb();
  }

  public Object processRequest(MessageContentsList messageContentsList) {
  	String patientId = getPatientId(messageContentsList);
  	return processRequest((String)messageContentsList.get(0), patientId);
  }


  private Object processRequest(String logicalAddress, String registeredResidentId) {

    // TC5 - Invalid id - return error-message
    if (TestDataDefines.TEST_RR_ID_FAULT_INVALID_ID.equals(registeredResidentId)) {
      throw new RuntimeException("Invalid Id: " + registeredResidentId);
    }

    // TC6 - in EI, but not in TAK
    if (TestDataDefines.TEST_RR_ID_EJ_SAMVERKAN_I_TAK.equals(registeredResidentId)) {
      throw new RuntimeException("VP007 Authorization missing");
    }

    // Simulate some processing
    doSomeProcessingForSomeTime(logicalAddress);

    // Lookup the response
    Object object = retrieveFromDb(logicalAddress, registeredResidentId);
    return object;
  }

  private void doSomeProcessingForSomeTime(String logicalAddress) {
    long processingTime = getProcessingTime(logicalAddress);
    try {
      log.debug("## SLEEP FOR " + processingTime + " ms.");
      Thread.sleep(processingTime);
      log.debug("## SLEEP DONE.");
    } catch (InterruptedException e) {
    }
  }

  public long getProcessingTime(String logicalAddress) {
    long processingTime = 0;
		if (TestDataDefines.TEST_LOGICAL_ADDRESS_1.equals(logicalAddress)) {
			processingTime = 1000;                    // Normal 1 sec response time on system #1
		} else if (TestDataDefines.TEST_LOGICAL_ADDRESS_2.equals(logicalAddress)) {
			processingTime = serviceTimeoutMs - 1000; // Slow but below the timeout on system #2
		} else if (TestDataDefines.TEST_LOGICAL_ADDRESS_3.equals(logicalAddress)) {
			processingTime = serviceTimeoutMs + 1000; // Too slow on system #3, the timeout will kick in
		} else {
			processingTime = 500;                                                                     // 0.5 sec response time for the rest of the systems
		}
    return processingTime;
  }

  public abstract Object createResponse(Object... responseItems);

  public abstract String getPatientId(MessageContentsList messageContentsList);

  public abstract Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId,
      String time);

  //
  // Simplest possible memory db for business object instances from test-stubs for a number of source systems
  //
  private static Map<String, Object> DB = null;

  void initDb() {
    log.debug("### INIT-DB CALLED, DB == NULL? " + (DB == null));

    // Start with resetting the db from old values.
    resetDb();

    //
    // TC1 - Patient with three bookings spread over three logical-addresses, all with fast response times
    //
    Object response = createResponse(
        createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_4, TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS,
            TestDataDefines.TEST_BO_ID_MANY_HITS_1,
            TestDataDefines.TEST_DATE_MANY_HITS_1));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_4, TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS, response);

    response = createResponse(createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_5,
        TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS,
        TestDataDefines.TEST_BO_ID_MANY_HITS_2,
        TestDataDefines.TEST_DATE_MANY_HITS_2));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_5, TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS, response);

    response = createResponse(createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_6,
        TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS,
        TestDataDefines.TEST_BO_ID_MANY_HITS_3,
        TestDataDefines.TEST_DATE_MANY_HITS_3));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_6, TestDataDefines.TEST_RR_ID_MANY_HITS_NO_ERRORS, response);

    //
    // TC3 - Patient with one booking, id = TEST_RR_ID_ONE_HIT
    //       Second booking in engagement index does not exist in the producer
    //
    response = createResponse(
        createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_1, TestDataDefines.TEST_RR_ID_ONE_HIT,
            TestDataDefines.TEST_BO_ID_ONE_HIT, TestDataDefines.TEST_DATE_ONE_HIT));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_1, TestDataDefines.TEST_RR_ID_ONE_HIT, response);

    //
    // TC4 - Patient with four bookings spread over three logical-addresses, where one is on a slow system, i.e. that cause timeouts
    //
    response = createResponse(
        createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_1, TestDataDefines.TEST_RR_ID_MANY_HITS,
            TestDataDefines.TEST_BO_ID_MANY_HITS_1,
            TestDataDefines.TEST_DATE_MANY_HITS_1));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_1, TestDataDefines.TEST_RR_ID_MANY_HITS, response);

    response = createResponse(
        createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_2, TestDataDefines.TEST_RR_ID_MANY_HITS,
            TestDataDefines.TEST_BO_ID_MANY_HITS_2,
            TestDataDefines.TEST_DATE_MANY_HITS_2),
        createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_2, TestDataDefines.TEST_RR_ID_MANY_HITS,
            TestDataDefines.TEST_BO_ID_MANY_HITS_3,
            TestDataDefines.TEST_DATE_MANY_HITS_3));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_2, TestDataDefines.TEST_RR_ID_MANY_HITS, response);

    response = createResponse(
        createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_3, TestDataDefines.TEST_RR_ID_MANY_HITS,
            TestDataDefines.TEST_BO_ID_MANY_HITS_4,
            TestDataDefines.TEST_DATE_MANY_HITS_4));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_3, TestDataDefines.TEST_RR_ID_MANY_HITS, response);

    //
    // TC7 - Patient with one booking, id = TEST_RR_ID_TRADKLATTRING for test trädklättring
    //
    response = createResponse(createResponseItem(TestDataDefines.TEST_LOGICAL_ADDRESS_CHILD,
        TestDataDefines.TEST_RR_ID_TRADKLATTRING,
        TestDataDefines.TEST_BO_ID_TRADKLATTRING,
        TestDataDefines.TEST_DATE_TRADKLATTRING));
    storeInDb(TestDataDefines.TEST_LOGICAL_ADDRESS_CHILD, TestDataDefines.TEST_RR_ID_TRADKLATTRING, response);

  }

  public void resetDb() {
    DB = new HashMap<String, Object>();
  }

  public void refreshDb() {
    initDb();
  }

  public void storeInDb(String logicalAddress, String registeredResidentId, Object value) {
    DB.put(logicalAddress + "|" + registeredResidentId, value);
  }

  public Object retrieveFromDb(String logicalAddress, String registeredResidentId) {
    return DB.get(logicalAddress + "|" + registeredResidentId);
  }
}