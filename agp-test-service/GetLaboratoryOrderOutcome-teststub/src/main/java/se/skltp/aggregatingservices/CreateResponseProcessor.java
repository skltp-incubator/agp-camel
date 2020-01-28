package se.skltp.aggregatingservices;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeType;
import se.skltp.aggregatingservices.producer.GetAggregatedLaboratoryOrderOutcomeTestProducerDb;

@Service
@Log4j2
public class CreateResponseProcessor implements Processor {

  @Autowired
  GetAggregatedLaboratoryOrderOutcomeTestProducerDb testDb;

  @Override
  public void process(Exchange exchange) throws Exception {
    MessageContentsList messageContentsList = exchange.getIn().getBody(MessageContentsList.class);


    String logicalAddress = (String) messageContentsList.get(0);
    GetLaboratoryOrderOutcomeType request = (GetLaboratoryOrderOutcomeType) messageContentsList.get(1);
    log.info("### Virtual service for GetLaboratoryOrderOutcome call the source system with logical address: {} and patientId: {}",
        logicalAddress, request.getPatientId().getId());

    GetLaboratoryOrderOutcomeResponseType response = (GetLaboratoryOrderOutcomeResponseType) testDb
        .processRequest(logicalAddress, request.getPatientId().getId());

  if (response == null) {
      log.info("nothing was found - returning an empty GetLaboratoryOrderOutcomeResponse");
      response = new GetLaboratoryOrderOutcomeResponseType();
    }

    exchange.getIn().setBody(response);
  }
}
