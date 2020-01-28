package se.skltp.aggregatingservices.data;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.configuration.AgpServiceConfiguration;
import se.skltp.agp.riv.vagvalsinfo.v2.AnropsBehorighetsInfoType;
import se.skltp.agp.riv.vagvalsinfo.v2.HamtaAllaAnropsBehorigheterResponseType;

@Log4j2
@Service
public class TakTestDataService {
  public static final String SAMPLE_SENDER_ID               = "sample-sender-id";
  public static final String SAMPLE_ORIGINAL_CONSUMER_HSAID = "sample-original-consumer-hsaid";

  private static final String[] receivers = {TestProducerDb.TEST_LOGICAL_ADDRESS_1, TestProducerDb.TEST_LOGICAL_ADDRESS_2,
      TestProducerDb.TEST_LOGICAL_ADDRESS_3, TestProducerDb.TEST_LOGICAL_ADDRESS_4, TestProducerDb.TEST_LOGICAL_ADDRESS_5,
      TestProducerDb.TEST_LOGICAL_ADDRESS_6};

  private HamtaAllaAnropsBehorigheterResponseType anropsBehorigheterResponse = new HamtaAllaAnropsBehorigheterResponseType();

    @Autowired
  public TakTestDataService(List<AgpServiceConfiguration> serviceConfigurationList) {
    if (serviceConfigurationList != null) {
      for (AgpServiceConfiguration configuration : serviceConfigurationList) {
        generateBehorighetStubData(configuration.getTargetNamespace());
      }
    }
  }


  public HamtaAllaAnropsBehorigheterResponseType getAnropsBehorigheterResponse() {
    return anropsBehorigheterResponse;
  }

  public HamtaAllaAnropsBehorigheterResponseType generateBehorighetStubData(String targetNamespaceString) {
    final HamtaAllaAnropsBehorigheterResponseType type = anropsBehorigheterResponse;

    TargetNamespace targetNamespace = new TargetNamespace(targetNamespaceString);
    // Permissions for AbstractTestConsumer.SAMPLE_SENDER_ID

    for (int i = 0; i < 6; i++) {
      type.getAnropsBehorighetsInfo()
          .add(anropsBehorighetsInfoType(targetNamespace.targetNamespace, receivers[i], SAMPLE_SENDER_ID));
    }

    // Permissions for AbstractTestConsumer.SAMPLE_ORIGINAL_CONSUMER_HSAID
    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.targetNamespace, "HSA-ID-1",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));
    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.targetNamespace, "HSA-ID-77",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));

    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.majorVersionOne, "HSA-ID-11",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));
    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.majorVersionOne, "HSA-ID-12",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));
    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.majorVersionOne, "HSA-ID-31",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));
    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.majorVersionOne, "HSA-ID-32",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));
    type.getAnropsBehorighetsInfo().add(anropsBehorighetsInfoType(targetNamespace.majorVersionOne, "HSA-ID-1",
        SAMPLE_ORIGINAL_CONSUMER_HSAID));

    // Some faulty random permissions

    type.getAnropsBehorighetsInfo()
        .add(anropsBehorighetsInfoType(UUID.randomUUID().toString(), "HSA-ID-FEL", "TK_" + "HSA-ID-FEL"));
    type.getAnropsBehorighetsInfo()
        .add(anropsBehorighetsInfoType(UUID.randomUUID().toString(), "HSA-ID-FEL", "TK_" + "HSA-ID-FEL"));

    return type;
  }

  protected AnropsBehorighetsInfoType anropsBehorighetsInfoType(final String ns, final String rId, final String sId) {
    final AnropsBehorighetsInfoType type = new AnropsBehorighetsInfoType();
    type.setTjansteKontrakt(ns);
    type.setReceiverId(rId);
    type.setSenderId(sId);
    type.setFromTidpunkt(xmlDate());
    type.setTomTidpunkt(type.getFromTidpunkt());
    return type;
  }

  private XMLGregorianCalendar xmlDate() {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) GregorianCalendar.getInstance());
    } catch (Exception err) {
      return null;
    }
  }

  @Data
  class TargetNamespace {

    private String targetNamespace;
    private String majorVersionOne;
    private String majorVersionTwo;

    public TargetNamespace(String targetNamespace) {
      setTargetNamespace(targetNamespace);
    }

    public void setTargetNamespace(String n) {
      this.targetNamespace = n;
      if (targetNamespace == null || targetNamespace.isEmpty()) {
        throw new BeanInitializationException("targetNamespace is mandatory");
      } else if (!targetNamespace.matches("^.+?\\d$")) {
        throw new BeanInitializationException("targetNamespace must end with a numeric");
      } else {
        try {
          int i = Integer.parseInt(targetNamespace.substring(targetNamespace.length() - 1));
          if (i < 1) {
            majorVersionOne = targetNamespace.substring(0, targetNamespace.length() - 1) + "1";
            majorVersionTwo = targetNamespace.substring(0, targetNamespace.length() - 1) + "2";
          } else {
            majorVersionOne = targetNamespace.substring(0, targetNamespace.length() - 1) + (i + 1);
            majorVersionTwo = targetNamespace.substring(0, targetNamespace.length() - 1) + (i + 2);
          }
        } catch (NumberFormatException nn) {
          throw new BeanInitializationException("targetNamespace last character not numeric? " + nn.getLocalizedMessage());
        }
      }
    }
  }
}
