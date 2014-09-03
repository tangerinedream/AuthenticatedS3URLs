/**
 * 
 */
package com.gman.authS3URL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman.authS3URL.WorkOrder.AuthLinkSpec;

/**
 * @author GMan
 *
 */
public class SigningWorker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Make an instance of myself
		SigningWorker worker=new SigningWorker();
		worker.doWork();
		
	}
	
	public void doWork() {
		// get work orders
		List<AuthLinkSpec> linkSpecs=this.makeWorkOrders();
		
		// Process work orders
		Date ttl=null;
		WorkOrderResult result=null;
		
		// Establish S3 Connection
		AmazonS3 s3Conn=this.makeS3Connection();
		
		for( AuthLinkSpec item : linkSpecs ) {
			// process this item or not?
			if(item.getProcessing().isGenerateAuthURL()) {
				
				// calc expiration
				ttl=this.makeExpirationDate(item);
				
				// process
				result=this.generate(s3Conn, item, ttl);
				
				// output Note: it will automatically indicate if the link is valid, so long as validation was requested in the json work order file
				System.out.println(result.toString());
			}
		}
	}
	
	
	protected WorkOrderResult generate(AmazonS3 s3Conn, AuthLinkSpec spec, Date ttl) {		
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(spec.getTarget().getBucketName(), spec.getTarget().getObjectName());
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(ttl);
		
		
		URL url =null;
		WorkOrderResult woResult=new WorkOrderResult(spec);
		
		// If WorkOrder contains an https request, generate that
		if(spec.getProtocol().isHttps()) {
			// Set HTTPS protocol URL by accessing the s3 https endpoint
			s3Conn.setEndpoint(HTTPS_ENDPOINT_);
			url = s3Conn.generatePresignedUrl(generatePresignedUrlRequest);
			woResult.getSignedHttpsUrl().setUrl(url.toString());
			//woResult.getSignedHttpsUrl().setProtocol(HTTPS_);
			if(spec.getProcessing().isValidateAuthURL() == true) {
				if(validTarget(url)) {
					woResult.getSignedHttpsUrl().setValid(true);
				}
			}
		}

		// If WorkOrder contains an http request, generate that
		if(spec.getProtocol().isHttp()) {
			// Set HTTP protocol URL by accessing the s3 http endpoint
			s3Conn.setEndpoint(HTTP_ENDPOINT_);
			url = s3Conn.generatePresignedUrl(generatePresignedUrlRequest);
			woResult.getSignedHttpUrl().setUrl(url.toString());
			//woResult.getSignedHttpUrl().setProtocol(HTTP_);
			if(spec.getProcessing().isValidateAuthURL() == true) {
				if(validTarget(url)) {
					woResult.getSignedHttpUrl().setValid(true);
				}
			}
		}
		
		return(woResult);
	}
		
	/**
	 * Validate the URL by attempting to GET it.  A 200 result code means it has been validated.
	 * @param URL
	 * @return
	 */
	private boolean validTarget(URL generatedURL) {
		int rc=HttpURLConnection.HTTP_NOT_FOUND;  // Need to default to something!
		try {
		    HttpURLConnection connection = (HttpURLConnection)generatedURL.openConnection();
		    connection.setRequestMethod("GET");
		    connection.connect();
		    rc = connection.getResponseCode();
		    if(rc == HttpURLConnection.HTTP_OK) 
		    	return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Validation Error [HTTP_CODE=="+rc+"] "+ generatedURL.toString());
	    return false;
	}
	
	protected List<AuthLinkSpec> makeWorkOrders() {

		// The list of linkSpec items
		List<AuthLinkSpec> resList=new Vector<AuthLinkSpec>();
		
		// Get Config.json file
		String jsonFile=new String("SignedUrlConfig.json");
		
		// The slash is prepended to locate the json file in the root package
		InputStream is = this.getClass().getResourceAsStream("/"+jsonFile);
		
		// Create the Jackson Object Mapper
		ObjectMapper om = new ObjectMapper();
		
		try {
			//Parse the JSON file, constructing the WorkOrder containing zero or more instances of "linkSpec" items
			WorkOrder wo = om.readValue(is, WorkOrder.class);
			ArrayList <AuthLinkSpec>linkSpecs=wo.getLinkSpecs();
			for(int i=0;i<linkSpecs.size();i++) {
				resList.add(linkSpecs.get(i));
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		// return a List of AuthLinkSpec objects
		return( resList );
	}
	
	/**
	 * Create the S3 Client API handle, obtaining the AWS credential from a .properties file
	 * 
	 * @return
	 */
	protected AmazonS3 makeS3Connection() {
		AmazonS3 s3client=null;
		
		// Setup Amazon S3 client
		try {
			s3client = new AmazonS3Client(new PropertiesCredentials(
					SigningWorker.class.getResourceAsStream("/AwsCredentials.properties")));  // Starts with slash because SigningGenerator is not in the "default" package.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s3client;
	}
	
	private Date makeExpirationDate(AuthLinkSpec spec) {
		Calendar expiration=new GregorianCalendar();
		//System.out.println("Current time is "+expiration.getTime().toString());

		// Note: we utilize an inner class "expiration" of WorkOrder as that is where the expiration settings are mapped from the json file
		// Add month increment
		expiration.add(Calendar.MONTH, spec.getExpiration().getMonths());
		// Add Day increment
		expiration.add(Calendar.DATE, spec.getExpiration().getDays());
		// Add Hour increment
		expiration.add(Calendar.HOUR, spec.getExpiration().getHours());
		// Add Minute increment
		expiration.add(Calendar.MINUTE, spec.getExpiration().getMinutes());
		
		System.out.println("Expiration date set to "+expiration.getTime().toString());
		
		return(expiration.getTime());
	}
	
	protected static final String HTTPS_="https";
	protected static final String HTTPS_ENDPOINT_="https://s3.amazonaws.com";
	
	protected static final String HTTP_="http";
	protected static final String HTTP_ENDPOINT_="http://s3.amazonaws.com";
	
}
