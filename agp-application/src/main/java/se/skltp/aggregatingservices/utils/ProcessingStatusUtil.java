package se.skltp.aggregatingservices.utils;

import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_CACHE;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_CACHE_SYNCH_FAILED;
import static se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum.DATA_FROM_SOURCE;

import java.util.Date;
import se.skltp.agp.riv.interoperability.headers.v1.CausingAgentEnum;
import se.skltp.agp.riv.interoperability.headers.v1.LastUnsuccessfulSynchErrorType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusRecordType;
import se.skltp.agp.riv.interoperability.headers.v1.StatusCodeEnum;

public class ProcessingStatusUtil {

	private static ThreadSafeSimpleDateFormat df = new ThreadSafeSimpleDateFormat("yyyyMMddHHmmss");

	public static ProcessingStatusRecordType createStatusRecord(String logicalAddress, StatusCodeEnum statusCode) {
		return createStatusRecord(logicalAddress, statusCode, null);
	}

	/**
	 * Implement the logic as described in the table:
	 * 
	 * +--------------------------+---------------------+-------------------+--------------------------+-----------------------+----------------------------+
	 * | Status Code              | isResponseFromCache | isResponseInSynch | lastSuccessfulSynch      | lastUnsuccessfulSynch | lastUnsuccessfulSynchError |
	 * +--------------------------+---------------------+-------------------+--------------------------+-----------------------+----------------------------+
	 * | DataFromSource           | False               | True              | Current timestamp        | Emtpy                 | Empty                      |
	 * | DataFromCache            | True                | True              | Last time for succ. call | Empty                 | Empty                      |
	 * | DataFromCacheSynchFailed | True                | False             | Last time for succ. call | Current timestamp     | Relevant error info        |
	 * | NoDataSynchFailed        | False               | False             | Empty                    | Current timestamp     | Relevant error info        |
	 * +--------------------------+---------------------+-------------------+--------------------------+-----------------------+----------------------------+
	 * */
	public static ProcessingStatusRecordType createStatusRecord(String logicalAddress, StatusCodeEnum statusCode,
			Throwable throwable) {
		ProcessingStatusRecordType status = new ProcessingStatusRecordType();

		status.setLogicalAddress(logicalAddress);
		status.setStatusCode(statusCode);
		status.setIsResponseFromCache(statusCode == DATA_FROM_CACHE || statusCode == DATA_FROM_CACHE_SYNCH_FAILED);
		status.setIsResponseInSynch(statusCode == DATA_FROM_SOURCE || statusCode == DATA_FROM_CACHE);

		if (statusCode == DATA_FROM_SOURCE) {
			status.setLastSuccessfulSynch(df.format(new Date()));
		}
		// TODO: DATA_FROM_CACHE/DATA_FROM_CACHE_SYNCH_FAILED: How to pickup time for last succ call from cache???

		if (status.isIsResponseInSynch()) {
			if (throwable != null) {
				throw new IllegalArgumentException("Error argument not allowed for state DATA_FROM_SOURCE and DATA_FROM_CACHE, must be null");
			}

		} else {

			// Ok, so the call failed. Fill in error info...
			status.setLastUnsuccessfulSynch(df.format(new Date()));
			status.setLastUnsuccessfulSynchError(createError(throwable));
		}
		return status;
	}

	public static LastUnsuccessfulSynchErrorType createError(Throwable e){

		String errorText = e.getMessage();
		if(e.getCause()!=null){
			errorText += ", " + e.getCause().getMessage();
		}

		LastUnsuccessfulSynchErrorType error = new LastUnsuccessfulSynchErrorType();
		error.setCausingAgent(CausingAgentEnum.VIRTUALIZATION_PLATFORM);
		// TODO What is this error code
		//		error.setCode(Integer.toString(ep.getCode()));
		error.setText(errorText);
		return error;
	}


}