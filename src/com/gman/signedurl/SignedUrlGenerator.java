/**
 * 
 */
package com.gman.signedurl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		
		String bucketName=null;
		String fileName=null;
		
		Properties inputParams = new Properties();
		try {
			inputParams.load(SignedUrlGenerator.class.getResourceAsStream("/SignedURLInputFile.properties"));
			bucketName=inputParams.getProperty(bucketName_);
			fileName=inputParams.getProperty(fileName_);

			// Create a generator spec
			GeneratorSpec spec=new GeneratorSpec();
			spec.setBucketName(bucketName);  
			spec.setObjectName(fileName);  
			spec.setTtl(driver.calcTimeToLive(inputParams));
			String httpStr=inputParams.getProperty(http_);
			if(httpStr.compareTo("1")==0)
				spec.setHttp(true);
			String httpsStr=inputParams.getProperty(https_);
			if(httpsStr.compareTo("1")==0)
				spec.setHttps(true);

			// Generate a Signed URL
			String signedURL=null;
			SigningGenerator gen=new SigningGenerator();
			List<String> urls=null;
			if( gen != null ) {
				urls=gen.generate(spec);
				for(int i=0; i<urls.size(); /* number of URL protocols generated */ i++) {
					signedURL=urls.get(i);	
					if(signedURL !=null)
						//System.out.println("Signed URL is -->"+signedURL+"<--");
						System.out.println(signedURL);
				}
			}
			else
				System.out.println("No URL generated. See output for details");
			// Write output file
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	

	private Date calcTimeToLive(Properties inputParams) {
		Calendar expiration=new GregorianCalendar();
		System.out.println("Original Expiration value is "+expiration.getTime().toString());

		
		for(int i=ttlMMNum_; i<(ttlMinNum_+1); i++){
			addTimeToLiveValue(i, inputParams, expiration);
		}
		System.out.println("New Expiration value is "+expiration.getTime().toString());
		
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
	private static final int httpNum_=4;  // for the switch statement
	
	private static final String https_="https";
	private static final int httpsNum_=5;  // for the switch statement	
	
}
