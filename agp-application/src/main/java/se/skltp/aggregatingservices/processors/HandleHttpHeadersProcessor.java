package se.skltp.aggregatingservices.processors;

import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_VP_INSTANCE_ID;
import static se.skltp.aggregatingservices.constants.AgpProperties.AGP_VP_SENDER_ID;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import se.skltp.aggregatingservices.config.VpConfig;

@Service
@Log4j2
public class HandleHttpHeadersProcessor implements Processor {

	@Autowired
	VpConfig vpConfig;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message in = exchange.getIn();
		
		in.removeHeader(CxfConstants.OPERATION_NAME);
        in.removeHeader(CxfConstants.OPERATION_NAMESPACE);
        in.removeHeader("SoapAction");
        in.setHeader(AGP_VP_SENDER_ID, vpConfig.getSenderId() );
        in.setHeader(AGP_VP_INSTANCE_ID, vpConfig.getInstanceId());
        
        /*
         * AGP_RIVTA_ORIGINAL_CONSUMER_ID and AGP_SKLTP_CORRELATION_ID
         * should already be on the incoming header.
         * We do nothing.
         */
        
        

		
	}

}
