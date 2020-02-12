package se.skltp.aggregatingservices.aggregate;

import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_SOURCE;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.NO_DATA_SYNCH_FAILED;

import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.utils.ProcessingStatusUtil;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;

@Service
@Log4j2
public class AgpAggregationStrategy implements AggregationStrategy {


  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

    if(oldExchange==null){
      AggregatedResponseResults aggregatedResponseResults = new AggregatedResponseResults();
      updateAggregatedResponse(newExchange, aggregatedResponseResults);
      newExchange.getIn().setBody(aggregatedResponseResults);
      return newExchange;
    } else {
      AggregatedResponseResults aggregatedResponseResults = oldExchange.getIn().getBody(AggregatedResponseResults.class);
      updateAggregatedResponse(newExchange, aggregatedResponseResults);
      return oldExchange;
    }
  }

  private void updateAggregatedResponse(Exchange newExchange, AggregatedResponseResults aggregatedResponseResults) {
    final ProcessingStatusRecordType statusRecord;
    final String logicalAddress = newExchange.getProperty("LogicalAddress", String.class);
    if(newExchange.isFailed() || newExchange.getException() != null){
      final Exception exception = newExchange.getException();
      // TODO log nice message here
      log.warn("Failed!!!!!", exception);
      statusRecord = ProcessingStatusUtil.createStatusRecord(logicalAddress, NO_DATA_SYNCH_FAILED, exception);
      aggregatedResponseResults.getProcessingStatus().getProcessingStatusList().add(statusRecord);

    } else {
      statusRecord = ProcessingStatusUtil.createStatusRecord(logicalAddress, DATA_FROM_SOURCE);
      aggregatedResponseResults.getProcessingStatus().getProcessingStatusList().add(statusRecord);
      aggregatedResponseResults.getResponseObjects().add(newExchange.getIn().getBody());
    }
  }

}
