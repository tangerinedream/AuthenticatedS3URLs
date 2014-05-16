/**
 * 
 */
package com.gman.signedurl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

/**
 * @author Gary Silverman
 *
 */
public class SignedUrlGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Get input data - Properties file to start with, possibly a db later
		
		// Create one of me
		SignedUrlGenerator driver= new SignedUrlGenerator();
		
		Properties inputParams = new Properties();
		try {
			inputParams.load(SignedUrlGenerator.class.getResourceAsStream("/SignedURLInputFile.properties"));
			
			String bucketName=inputParams.getProperty(bucketName_);
			String fileName=inputParams.getProperty(fileName_);

			// Create a generator spec
			GeneratorSpec spec=new GeneratorSpec();
			spec.setBucketName(bucketName);  
			spec.setObjectName(fileName);  
			spec.setTtl(driver.calcTimeToLive(inputParams));
			String protocol=null;
			
			//Determine desired protocols
			// HTTP
			protocol=inputParams.getProperty(http_);
			if(protocol.compareTo("1")==0)
				spec.setHttp(true);
			
			// HTTPS
			protocol=inputParams.getProperty(https_);
			if(protocol.compareTo("1")==0)
				spec.setHttps(true);
			
			// Validate the URL Target
			String validateTarget=inputParams.getProperty(vt_);
			if(validateTarget.compareTo("1")==0)
				spec.setValidateTarget(true);

			// Generate a Signed URL for all protocols requested
			String signedURL=null;
			SigningGenerator gen=new SigningGenerator();
			List<String> urls=gen.setEndpointsAndGenerate(spec);
			for(int i=0; i<urls.size(); /* number of URL protocols generated */ i++) {
				signedURL=urls.get(i);	
				if(signedURL !=null)
					System.out.println(signedURL);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	

	private Date calcTimeToLive(Properties inputParams) {
		Calendar expiration=new GregorianCalendar();
		//System.out.println("Current time is "+expiration.getTime().toString());

		
		for(int i=ttlMMNum_; i<(ttlMinNum_+1); i++){
			addTimeToLiveValue(i, inputParams, expiration);
		}
		System.out.println("URL expiration date is "+expiration.getTime().toString());
		
		return(expiration.getTime());
	}
	
	private void addTimeToLiveValue(int key, Properties inputParams, Calendar expiration) {
		// Add the value to calculate expiration
		String valueStr=null;
		int calendarField=0;
		switch(key) {
			case ttlMMNum_:
				valueStr=inputParams.getProperty(ttlMM_);
				calendarField=Calendar.MONTH;
			break;
			case ttlDDNum_:
				valueStr=inputParams.getProperty(ttlDD_);
				calendarField=Calendar.DATE;
			break;
			case ttlHHNum_:
				valueStr=inputParams.getProperty(ttlHH_);
				calendarField=Calendar.HOUR;
			break;
			case ttlMinNum_:
				valueStr=inputParams.getProperty(ttlMin_);
				calendarField=Calendar.MINUTE;
			break;			
		}
		if(valueStr != null) {
			try {
				int value=new Integer(valueStr).intValue();
				expiration.add(calendarField, value);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private static final String bucketName_="bucketName";
	private static final String fileName_="objectName";
	
	private static final String ttlMM_="timeToLiveMonths";
	private static final int ttlMMNum_=0;  // for the switch statement
	
	private static final String ttlDD_="timeToLiveDays";
	private static final int ttlDDNum_=1;  // for the switch statement
	
	private static final String ttlHH_="timeToLiveHours";
	private static final int ttlHHNum_=2;  // for the switch statement
	
	private static final String ttlMin_="timeToLiveMinutes";
	private static final int ttlMinNum_=3;  // for the switch statement
	
	private static final String http_="http";
	private static final String https_="https";
	
	private static final String vt_="validateTarget";
	
}
