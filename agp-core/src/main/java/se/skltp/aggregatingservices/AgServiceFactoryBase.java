package se.skltp.aggregatingservices;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.aggregatingservices.utility.FindContentUtil;
import se.skltp.aggregatingservices.utility.RequestListUtil;

@Log4j2
public abstract class AgServiceFactoryBase<E, T> implements AgpServiceFactory<E, T> {

  AgpServiceConfiguration agpServiceConfiguration;

  public abstract String getPatientId(E queryObject);

  public abstract String getSourceSystemHsaId(E queryObject);

  public abstract T aggregateResponse(List<T> aggregatedResponseList);

  @Override
  public void setAgpServiceConfiguration(AgpServiceConfiguration agpServiceConfiguration) {
    this.agpServiceConfiguration = agpServiceConfiguration;
  }


  @Override
  public FindContentType createFindContent(MessageContentsList messageContentsList) {
    int index = agpServiceConfiguration.getMessageContentListQueryIndex();
    E queryObject = (E) messageContentsList.get(index);
    String patientId = getPatientId(queryObject);

    return FindContentUtil.createFindContent(patientId, agpServiceConfiguration.getEiServiceDomain(),
        agpServiceConfiguration.getEiCategorization());
  }

  @Override
  public List<MessageContentsList> createRequestList(MessageContentsList messageContentsList, FindContentResponseType eiResp) {
    int index = agpServiceConfiguration.getMessageContentListQueryIndex();
    E queryObject = (E) messageContentsList.get(index);
    String filterOnCareUnit = getSourceSystemHsaId(queryObject);

    log.info("Got {} hits in the engagement index, filtering on {}...", eiResp.getEngagement().size(), filterOnCareUnit);

    List<MessageContentsList> reqList = RequestListUtil
        .createRequestMessageContentsLists(eiResp, queryObject, filterOnCareUnit);

    log.info("Calling {} source systems", reqList.size());

    return reqList;
  }

  @Override
  public T createAggregatedResponseObject(MessageContentsList originalQuery,
      List<MessageContentsList> aggregatedResponseList) {

    List<T> responseList = aggregatedResponseList.stream().map(object -> (T) object.get(0))
        .collect(Collectors.toList());

    return aggregateResponse(responseList);
  }

}
