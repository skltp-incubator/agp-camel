package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_ID_FAULT_INVALID_ID_IN_EI;
import static se.skltp.aggregatingservices.data.TestDataDefines.TEST_ID_FAULT_TIMEOUT_IN_EI;

import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.skltp.aggregatingservices.data.FindContentTestData;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.v1.EngagementType;

@Component
@Log4j2
public class FindContentResponseProcessor implements Processor {

  @Autowired
  FindContentTestData findContentTestData;

  @Override
  public void process(Exchange exchange) throws Exception {

    MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);
    FindContentType request = (FindContentType) messageContentsList.get(1);

    exchange.getIn().setBody(createResponse(request));
  }

  private  FindContentResponseType createResponse(FindContentType request){
    log.info("### Engagemengsindex.findContent() received a request for Registered Resident id: {}",
        request.getRegisteredResidentIdentification());

    String id = request.getRegisteredResidentIdentification();

    // Return an error-message if invalid id
    if (TEST_ID_FAULT_INVALID_ID_IN_EI.equals(id)) {
      throw new RuntimeException("Invalid Id: " + id);
    }

    // Force a timeout if zero Id
    if (TEST_ID_FAULT_TIMEOUT_IN_EI.equals(id)) {
      try {
        TimeUnit.SECONDS.sleep(35);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    // Lookup the response
    FindContentResponseType response = findContentTestData.getResponseForPatient(request.getRegisteredResidentIdentification());
    updateResponseWithDomainAndCategory(request, response);

    log.info("### Engagemengsindex return {} items", response.getEngagement().size());
    return response;
  }

  private void updateResponseWithDomainAndCategory(FindContentType request, FindContentResponseType response) {
    for(EngagementType engagementType : response.getEngagement()){
      engagementType.setCategorization(request.getCategorization());
      engagementType.setServiceDomain(request.getServiceDomain());
    }
  }

}
