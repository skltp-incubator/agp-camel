package se.skltp.aggregatingservices.utility;

import se.skltp.aggregatingservices.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;

public class FindContentUtil {

  // Static utility class
  private FindContentUtil() {
  }

  public static FindContentType createFindContent(String patientId, String serviceDomain, String categorization) {
    FindContentType fc = new FindContentType();
    fc.setRegisteredResidentIdentification(patientId);
    fc.setServiceDomain(serviceDomain);
    fc.setCategorization(categorization);
    return fc;
  }

}