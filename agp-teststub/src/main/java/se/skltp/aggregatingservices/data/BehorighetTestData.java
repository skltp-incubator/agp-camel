package se.skltp.aggregatingservices.data;

import static se.skltp.aggregatingservices.data.TestDataDefines.SAMPLE_ORIGINAL_CONSUMER_HSAID;
import static se.skltp.aggregatingservices.data.TestDataDefines.SAMPLE_SENDER_ID;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Service;
import se.skltp.agp.riv.vagvalsinfo.v2.AnropsBehorighetsInfoType;
import se.skltp.agp.riv.vagvalsinfo.v2.HamtaAllaAnropsBehorigheterResponseType;

@Log4j2
@Service
public class BehorighetTestData {


  private static final String[] receivers = {TestDataDefines.TEST_LOGICAL_ADDRESS_1, TestDataDefines.TEST_LOGICAL_ADDRESS_2,
      TestDataDefines.TEST_LOGICAL_ADDRESS_3, TestDataDefines.TEST_LOGICAL_ADDRESS_4, TestDataDefines.TEST_LOGICAL_ADDRESS_5,
      TestDataDefines.TEST_LOGICAL_ADDRESS_6};

  private HamtaAllaAnropsBehorigheterResponseType anropsBehorigheterResponse = new HamtaAllaAnropsBehorigheterResponseType();


  public void resetAnropsBehorigheterResponse(){
    anropsBehorigheterResponse = new HamtaAllaAnropsBehorigheterResponseType();
  }

  public HamtaAllaAnropsBehorigheterResponseType getAnropsBehorigheterResponse() {
    return anropsBehorigheterResponse;
  }

  public HamtaAllaAnropsBehorigheterResponseType generateBehorighetDefaultStubData(String targetNamespaceString) {
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
    type.setTomTidpunkt(xmlDateAddHours(type.getFromTidpunkt(), 1));
    return type;
  }

  private XMLGregorianCalendar xmlDate() {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) GregorianCalendar.getInstance());
    } catch (Exception err) {
      return null;
    }
  }

  private XMLGregorianCalendar xmlDateAddHours(XMLGregorianCalendar orgDate, int hours) {
    try {
      GregorianCalendar calendar = orgDate.toGregorianCalendar();
      calendar.add(Calendar.HOUR, hours);
      return (DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
    } catch (Exception err) {
      return null;
    }
  }

  @Data
  static class TargetNamespace {

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
