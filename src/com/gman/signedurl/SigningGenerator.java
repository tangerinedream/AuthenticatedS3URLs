/**
 * 
 */
package com.gman.signedurl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.PropertiesCredentials;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

/**
 * @author Gary Silverman
 *
 */
public class SigningGenerator {
	
	private AmazonS3 s3client_=null;	
	
	public List<String> generate(GeneratorSpec spec) {
		
		
		try {
			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				    new GeneratePresignedUrlRequest(spec.getBucketName(), spec.getObjectName());
			generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
			generatePresignedUrlRequest.setExpiration(spec.getTtl());

			// create s3client if not yet established
			if(s3client_==null) {
				s3client_=this.fabricateAmazonS3Client();
			}
			
			List<String> listOfURLs=new ArrayList<String>(2);
			URL url =null;
			if(s3client_ != null ) {
				if(spec.isHttps()) {
					url = s3client_.generatePresignedUrl(generatePresignedUrlRequest);	
					listOfURLs.add(url.toString());
				}
				if(spec.isHttp()) {
					s3client_.setEndpoint("http://s3.amazonaws.com");
					url = s3client_.generatePresignedUrl(generatePresignedUrlRequest);
					listOfURLs.add(url.toString());
					s3client_.setEndpoint("https://s3.amazonaws.com");
				}
			}	
			return(listOfURLs);
		} catch (AmazonServiceException exception) {
			System.out.println("Caught an AmazonServiceException, " +
					"which means your request made it " +
					"to Amazon S3, but was rejected with an error response " +
			"for some reason.");
			System.out.println("Error Message: " + exception.getMessage());
			System.out.println("HTTP  Code: "    + exception.getStatusCode());
			System.out.println("AWS Error Code:" + exception.getErrorCode());
			System.out.println("Error Type:    " + exception.getErrorType());
			System.out.println("Request ID:    " + exception.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, " +
					"which means the client encountered " +
					"an internal error while trying to communicate" +
					" with S3, " +
			"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			
		}
		return(null);
	}
	
	private AmazonS3 fabricateAmazonS3Client() {
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
}
