/**
 * 
 */
package com.gman.authS3URL;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gman.signedurl.SigningGenerator;

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
		List<WorkOrder> woList=this.makeWorkOrders();
		
		// Process work orders
		Date ttl=null;
		WorkOrderResult result=null;
		
		// Establish S3 Connection
		AmazonS3 s3Conn=this.makeS3Connection();
		
		for( WorkOrder item : woList ) {
			// process this item or not?
			if(item.getAuthLinkSpec().getProcessing().isGenerateAuthURL()) {
				
				// calc expiration
				ttl=this.makeExpirationDate(item);
				
				// process
				result=this.generate(s3Conn, item, ttl);
				
				// output Note: it will automatically indicate if the link is valid, so long as validation was requested in the json work order file
				System.out.println(result.toString());
			}
		}
	}
	
	
	protected WorkOrderResult generate(AmazonS3 s3Conn, WorkOrder wo, Date ttl) {		
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(wo.getAuthLinkSpec().getTarget().getBucketName(), wo.getAuthLinkSpec().getTarget().getObjectName());
		generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
		generatePresignedUrlRequest.setExpiration(ttl);
		
		
		URL url =null;
		WorkOrderResult woResult=new WorkOrderResult(wo);
		
		// If WorkOrder contains an https request, generate that
		if(wo.getAuthLinkSpec().getProtocol().isHttps()) {
			// Set HTTPS protocol URL by accessing the s3 https endpoint
			s3Conn.setEndpoint(HTTPS_ENDPOINT_);
			url = s3Conn.generatePresignedUrl(generatePresignedUrlRequest);
			woResult.getSignedHttpsUrl().setUrl(url.toString());
			//woResult.getSignedHttpsUrl().setProtocol(HTTPS_);
			if(wo.getAuthLinkSpec().getProcessing().isValidateAuthURL() == true) {
				if(validTarget(url)) {
					woResult.getSignedHttpsUrl().setValid(true);
				}
			}
		}

		// If WorkOrder contains an http request, generate that
		if(wo.getAuthLinkSpec().getProtocol().isHttp()) {
			// Set HTTP protocol URL by accessing the s3 http endpoint
			s3Conn.setEndpoint(HTTP_ENDPOINT_);
			url = s3Conn.generatePresignedUrl(generatePresignedUrlRequest);
			woResult.getSignedHttpUrl().setUrl(url.toString());
			//woResult.getSignedHttpUrl().setProtocol(HTTP_);
			if(wo.getAuthLinkSpec().getProcessing().isValidateAuthURL() == true) {
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
	    int rc=500;
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
	
	protected List<WorkOrder> makeWorkOrders() {
		/**
		 * Alternative Jackson approach
		 * JsonParser jp=new JsonParser();
		 * jp.readValueAs(WorkOrder.class);
		 * ObjectMapper om=new ObjectMapper();
		 * MappingIterator mi=om.readValues(jp, WorkOrder.class);
		 * 
		 * or...
		 * 
		 * public static <T> MappingIterator<T> readValues(InputStream src, Class<T> c) throws IOException {       
  		 * JsonFactory factory = INSTANCE.mapper.getJsonFactory();       
  		 * JsonParser parser = factory.createJsonParser(src);       
  		 * return INSTANCE.mapper.readValues(parser, c); 
  		 * called by:
  		 * try {                   
  		 *   rawMos = JacksonManager.readValues(input, RawMo.class);               
  		 * } catch (IOException e) {                   
  		 *   logger.error(loggingPrefix + " ERROR cannot read json source", e);               
  		 * }
  		 * 
		 */
		List<WorkOrder> woList=new Vector<WorkOrder>();
		
		// Get Config.json file
		String jsonFile=new String("SignedUrlConfig.json");
		
		// The slash is prepended to locate the json file in the root package
		InputStream is = this.getClass().getResourceAsStream("/"+jsonFile);
		
		// Parse file
		ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
		boolean done=false;

		// Jackson doesn't process multiples in data-bind mode.  So, we'll cover that in a future version
//		while( !done /*is.available()>0*/ ) {
			WorkOrder wo=null;
			
			// Use Jackson in Databind mode
			try {
				int availTest=is.available();
				wo = mapper.readValue(is, WorkOrder.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				System.err.println("JSON parse exception processing "+jsonFile);
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				System.err.println("JSON mapping exception processing "+jsonFile);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("io exception processing "+jsonFile);
				e.printStackTrace();
			}
			if( wo != null ) {	
				woList.add(wo);
//				done=true; // THIS IS A TEMP FORCED STOP until Jackson "ObjectMapper.readValues()" is working
			} else {
				done=true;
			}
//		}
		
		// return a List of WorkOrder objects
		return( woList);
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
					SigningGenerator.class.getResourceAsStream("/AwsCredentials.properties")));  // Starts with slash because SigningGenerator is not in the "default" package.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s3client;
	}
	
	private Date makeExpirationDate(WorkOrder wo) {
		Calendar expiration=new GregorianCalendar();
		//System.out.println("Current time is "+expiration.getTime().toString());

		// Note: we utilize an inner class "expiration" of WorkOrder as that is where the expiration settings are mapped from the json file
		// Add month increment
		expiration.add(Calendar.MONTH, wo.getAuthLinkSpec().getExpiration().getMonths());
		// Add Day increment
		expiration.add(Calendar.DATE, wo.getAuthLinkSpec().getExpiration().getDays());
		// Add Hour increment
		expiration.add(Calendar.HOUR, wo.getAuthLinkSpec().getExpiration().getHours());
		// Add Minute increment
		expiration.add(Calendar.MINUTE, wo.getAuthLinkSpec().getExpiration().getMinutes());
		
		System.out.println("Expiration date set to "+expiration.getTime().toString());
		
		return(expiration.getTime());
	}
	
	protected static final String HTTPS_="https";
	protected static final String HTTPS_ENDPOINT_="https://s3.amazonaws.com";
	
	protected static final String HTTP_="http";
	protected static final String HTTP_ENDPOINT_="http://s3.amazonaws.com";
	
}
