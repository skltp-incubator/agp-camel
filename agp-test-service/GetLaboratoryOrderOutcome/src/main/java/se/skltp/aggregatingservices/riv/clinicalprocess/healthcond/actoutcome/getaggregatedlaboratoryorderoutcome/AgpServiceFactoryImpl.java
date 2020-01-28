package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedlaboratoryorderoutcome;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeType;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedlaboratoryorderoutcome.configuration.AgpServiceConfiguration;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.aggregatingservices.utility.FindContentUtil;
import se.skltp.aggregatingservices.utility.RequestListUtil;

@Service
@Log4j2
public class AgpServiceFactoryImpl implements
    AgpServiceFactory<GetLaboratoryOrderOutcomeType, GetLaboratoryOrderOutcomeResponseType> {

  @Autowired
  AgpServiceConfiguration agpServiceConfiguration;

  @Override
  public FindContentType createFindContent(MessageContentsList messageContentsList) {
    GetLaboratoryOrderOutcomeType queryObject = (GetLaboratoryOrderOutcomeType) messageContentsList.get(1);
    String patientId = queryObject.getPatientId().getId();

    FindContentType findContentType = FindContentUtil.createFindContent(patientId, agpServiceConfiguration.getEiServiceDomain(),
        agpServiceConfiguration.getEiCategorization());

    return findContentType;
  }


  @Override
  public List<MessageContentsList> createRequestList(MessageContentsList messageContentsList, FindContentResponseType eiResp) {
    GetLaboratoryOrderOutcomeType originalRequest = (GetLaboratoryOrderOutcomeType) messageContentsList.get(1);
    String filterOnCareUnit = originalRequest.getSourceSystemHSAId();

    log.info("Got {} hits in the engagement index, filtering on {}...", eiResp.getEngagement().size(), filterOnCareUnit);

    List<MessageContentsList> reqList = RequestListUtil
        .createRequestMessageContentsLists(eiResp, originalRequest, filterOnCareUnit);

    log.info("Calling {} source systems", reqList.size());

    return reqList;
  }

  @Override
  public GetLaboratoryOrderOutcomeResponseType createAggregatedResponseObject(MessageContentsList originalQuery,
      List<MessageContentsList> aggregatedResponseList) {
    GetLaboratoryOrderOutcomeResponseType aggregatedResponse = new GetLaboratoryOrderOutcomeResponseType();

    for (MessageContentsList contentsList : aggregatedResponseList) {
      GetLaboratoryOrderOutcomeResponseType response = (GetLaboratoryOrderOutcomeResponseType) contentsList.get(0);
      aggregatedResponse.getLaboratoryOrderOutcome().addAll(response.getLaboratoryOrderOutcome());
    }

    GetLaboratoryOrderOutcomeType getLaboratoryOrderOutcomeType = (GetLaboratoryOrderOutcomeType) originalQuery.get(1);
    String subjectOfCareId = getLaboratoryOrderOutcomeType.getPatientId().getId();

    log.info("Returning {} aggregated laboratory order outcome for subject of care id {}",
        aggregatedResponse.getLaboratoryOrderOutcome().size(), subjectOfCareId);

    return aggregatedResponse;
  }


}
