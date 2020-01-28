package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_ORIGINAL_QUERY;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_SERVICE_HANDLER;

import java.io.StringReader;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.apache.cxf.headers.Header.Direction;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.staxutils.StaxUtils;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.aggregate.AggregatedResponseResults;
import se.skltp.aggregatingservices.utils.JaxbUtil;
import se.skltp.agp.riv.interoperability.headers.v1.ObjectFactory;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;

@Service
@Log4j2
public class CreateResponseProcessor implements Processor {

  private static final ObjectFactory OF_HEADERS = new ObjectFactory();
  private static final JaxbUtil jaxbUtil = new JaxbUtil(ProcessingStatusType.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    AggregatedResponseResults aggregatedResponseResults = exchange.getIn().getBody(AggregatedResponseResults.class);
    AgpServiceFactory agpServiceProcessor = exchange.getProperty(AGP_SERVICE_HANDLER, AgpServiceFactory.class);
    MessageContentsList originalRequest = exchange.getProperty(AGP_ORIGINAL_QUERY, MessageContentsList.class);

    Object responseObject = agpServiceProcessor
        .createAggregatedResponseObject(originalRequest, aggregatedResponseResults.getResponseObjects());
    exchange.getIn().setBody(responseObject);

    insertProcessingStatusHeader(exchange, aggregatedResponseResults.getProcessingStatus());
  }

  private void insertProcessingStatusHeader(Exchange exchange, ProcessingStatusType processingStatus) throws XMLStreamException {
    String xmlStatus = jaxbUtil.marshal(OF_HEADERS.createProcessingStatus(processingStatus));
    log.info("processingStatus:\n{}", xmlStatus);

    SoapHeader newHeader = new SoapHeader(new QName("urn:riv:interoperability:headers:1", "ProcessingStatus")
        , StaxUtils.read(new StringReader(xmlStatus)).getDocumentElement());
    newHeader.setDirection(Direction.DIRECTION_OUT);
    List<SoapHeader> soapHeaders = (List) exchange.getIn().getHeader(Header.HEADER_LIST);
    soapHeaders.add(newHeader);
  }
}
