/**
 * 
 */
package com.gman.signedurl;

import java.util.Date;

/**
 * @author Gary Silverman
 * POJO of the specification for the Signing Generator to use
 *
 */
public class GeneratorSpec {
	private String bucketName=null;
	private String objectName=null;
	private Date ttl=null;
	private boolean http=false;  	// generate http URL
	private boolean https=false;		// generate https URL
	private boolean validateTarget=false;  //perform GET operation in attempt to determine if target exists
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public Date getTtl() {
		return ttl;
	}
	public void setTtl(Date ttl) {
		this.ttl = ttl;
	}
	public boolean isHttp() {
		return http;
	}
	public void setHttp(boolean http) {
		this.http = http;
	}
	public boolean isHttps() {
		return https;
	}
	public void setHttps(boolean https) {
		this.https = https;
	}
	/**
	 * @return the validateTarget
	 */
	public boolean isValidateTarget() {
		return validateTarget;
	}
	/**
	 * @param validateTarget the validateTarget to set
	 */
	public void setValidateTarget(boolean validateTarget) {
		this.validateTarget = validateTarget;
	}

}
