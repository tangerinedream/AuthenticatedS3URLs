/**
 * 
 */
package com.gman.signedurl;

import java.util.Date;

/**
 * @author Dipper
 *
 */
public class GeneratorSpec {
	private String bucketName=null;
	private String objectName=null;
	private Date ttl=null;
	
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

}
