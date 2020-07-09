package se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.v4;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import se.skltp.aggregatingservices.GLOOTestDataGenerator;
import se.skltp.aggregatingservices.api.AgpServiceFactory;
import se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedlaboratoryorderoutcome.GLOOAgpServiceConfiguration;
import se.skltp.aggregatingservices.riv.clinicalprocess.healthcond.actoutcome.getaggregatedlaboratoryorderoutcome.GLOOAgpServiceFactoryImpl;
import se.skltp.aggregatingservices.tests.CreateRequestListTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class GLOOCreateRequestListTest extends CreateRequestListTest {

  private static GLOOAgpServiceConfiguration configuration = new GLOOAgpServiceConfiguration();
  private static AgpServiceFactory<GetLaboratoryOrderOutcomeResponseType> agpServiceFactory = new GLOOAgpServiceFactoryImpl();
  private static GLOOTestDataGenerator testDataGenerator = new GLOOTestDataGenerator();

  public GLOOCreateRequestListTest() {
    super(testDataGenerator, agpServiceFactory, configuration);
  }

}