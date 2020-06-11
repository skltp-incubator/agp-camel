/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.aggregatingservices;

import lombok.extern.log4j.Log4j2;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Service;
import riv.clinicalprocess.healthcond.actoutcome._4.AccessControlHeaderType;
import riv.clinicalprocess.healthcond.actoutcome._4.HeaderType;
import riv.clinicalprocess.healthcond.actoutcome._4.HealthcareProfessionalType;
import riv.clinicalprocess.healthcond.actoutcome._4.IIType;
import riv.clinicalprocess.healthcond.actoutcome._4.LaboratoryOrderOutcomeBodyType;
import riv.clinicalprocess.healthcond.actoutcome._4.LaboratoryOrderOutcomeType;
import riv.clinicalprocess.healthcond.actoutcome._4.OrgUnitType;
import riv.clinicalprocess.healthcond.actoutcome._4.PatientType;
import riv.clinicalprocess.healthcond.actoutcome._4.PatientinformationType;
import riv.clinicalprocess.healthcond.actoutcome._4.PersonIdType;
import riv.clinicalprocess.healthcond.actoutcome._4.SourceType;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeResponseType;
import riv.clinicalprocess.healthcond.actoutcome.getlaboratoryorderoutcomeresponder.v4.GetLaboratoryOrderOutcomeType;
import se.skltp.aggregatingservices.data.TestDataGenerator;


@Log4j2
@Service
public class GLOOTestDataGenerator extends TestDataGenerator {

	@Override
	public String getPatientId(MessageContentsList messageContentsList) {
		GetLaboratoryOrderOutcomeType request = (GetLaboratoryOrderOutcomeType) messageContentsList.get(1);
		return request.getPatientId().getId();
	}

	@Override
	public Object createResponse(Object... responseItems) {
		log.info("Creating a response with {} items", responseItems.length);
		GetLaboratoryOrderOutcomeResponseType response = new GetLaboratoryOrderOutcomeResponseType();
		for (int i = 0; i < responseItems.length; i++) {
			response.getLaboratoryOrderOutcome().add((LaboratoryOrderOutcomeType)responseItems[i]);
		}

		log.info("response.toString:" + response.toString());

		return response;
	}

	@Override
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {

        log.debug("Created LaboratoryOrderOutcomeType for logical-address {}, registeredResidentId {} and businessObjectId {}",
				new Object[] {logicalAddress, registeredResidentId, businessObjectId});

		LaboratoryOrderOutcomeType labOrderOutcome = new LaboratoryOrderOutcomeType();

		HeaderType header = new HeaderType();
		labOrderOutcome.setLaboratoryOrderOutcomeHeader(header);
		LaboratoryOrderOutcomeBodyType body = new LaboratoryOrderOutcomeBodyType();
		labOrderOutcome.setLaboratoryOrderOutcomeBody(body);
		
		SourceType source = new SourceType();
		IIType systemId = new IIType();
		systemId.setRoot(logicalAddress);
		systemId.setExtension("1.2.3");
		source.setSystemId(systemId);
		header.setSource(source);

		PatientinformationType pinfo = new PatientinformationType();
		body.setPatientinformation(pinfo);	
		pinfo.setFodelsetidpunkt("12:12");
		
		HealthcareProfessionalType hp = new HealthcareProfessionalType();
		hp.setName("Dr Hpro");
		hp.setId(logicalAddress);

		OrgUnitType orgUnitType = new OrgUnitType();
		orgUnitType.setName("Organisation 1");
		IIType id = new IIType();
		id.setRoot(logicalAddress);
		id.setExtension("1.2.3");
		orgUnitType.setId(id);

		AccessControlHeaderType ac = new AccessControlHeaderType();
		header.setAccessControlHeader(ac);
		
		IIType patient = new IIType();
		patient.setRoot(registeredResidentId);
		patient.setExtension("1.2.752.129.2.1.3.1");

		PatientType person = new PatientType();
		person.getId().add(patient);
		ac.setPatient(person);

		//Body start
		body.setResultatkommentar("kommentar");
		body.setResultatrapport("OK");

		//Body end

		return labOrderOutcome;
	}

	@Override
	public Object createRequest(String patientId, String sourceSystemHSAId) {
		GetLaboratoryOrderOutcomeType outcomeType = new GetLaboratoryOrderOutcomeType();

		PersonIdType personIdType = new PersonIdType();
		personIdType.setId(patientId);
		personIdType.setType("1.2.752.129.2.1.3.1");

		outcomeType.setPatientId(personIdType);
		if (sourceSystemHSAId != null) {
			IIType sourceSystem = new IIType();
			sourceSystem.setExtension(sourceSystemHSAId);
			sourceSystem.setRoot("1.2.3");
			outcomeType.setSourceSystemHSAId(sourceSystemHSAId);
		}

		return outcomeType;
	}

}
