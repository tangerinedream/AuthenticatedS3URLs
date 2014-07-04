/**
 * 
 */
package com.gman.authS3URL;

import java.util.ArrayList;

/**
 * @author GMan
 * 
 */
public class WorkOrder {
	protected ArrayList<AuthLinkSpec> linkSpecs=null;
	
	public static class AuthLinkSpec { 
		protected String description = null;
		protected Processing processing=null;
		protected Protocol protocol=null;
		protected Target target=null;
		protected Expiration expiration=null;
		
		public static class Processing {
			private boolean generateAuthURL=true;
			private boolean validateAuthURL=true;
			public boolean isGenerateAuthURL() {
				return generateAuthURL;
			}
			public void setGenerateAuthURL(boolean generateAuthURL) {
				this.generateAuthURL = generateAuthURL;
			}
			public boolean isValidateAuthURL() {
				return validateAuthURL;
			}
			public void setValidateAuthURL(boolean validateAuthURL) {
				this.validateAuthURL = validateAuthURL;
			}
		}	
		
		public static class Protocol {
			private boolean http=false;
			private boolean https=false;
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
			
		}
		
		public static class Target {
			private String bucketName=null;
			private String objectName=null;
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
		}
		
		public static class Expiration {
			private Integer months=new Integer(0);
			private Integer days=new Integer(0);
			private Integer hours=new Integer(0);
			private Integer minutes=new Integer(0);
			public Integer getMonths() {
				return months;
			}
			public void setMonths(Integer months) {
				this.months = months;
			}
			public Integer getDays() {
				return days;
			}
			public void setDays(Integer days) {
				this.days = days;
			}
			public Integer getHours() {
				return hours;
			}
			public void setHours(Integer hours) {
				this.hours = hours;
			}
			public Integer getMinutes() {
				return minutes;
			}
			public void setMinutes(Integer minutes) {
				this.minutes = minutes;
			}
			
			
		}
	
		public String getDescription() {
			return description;
		}
	
		public void setDescription(String description) {
			this.description = description;
		}
	
		public Processing getProcessing() {
			return processing;
		}
	
		public void setProcessing(Processing processing) {
			this.processing = processing;
		}
	
		public Protocol getProtocol() {
			return protocol;
		}
	
		public void setProtocol(Protocol protocol) {
			this.protocol = protocol;
		}
	
		public Target getTarget() {
			return target;
		}
	
		public void setTarget(Target target) {
			this.target = target;
		}
	
		public Expiration getExpiration() {
			return expiration;
		}
	
		public void setExpiration(Expiration expiration) {
			this.expiration = expiration;
		}
	
	}

	public ArrayList<AuthLinkSpec> getLinkSpecs() {
		return linkSpecs;
	}

	public void setLinkSpecs(ArrayList<AuthLinkSpec> linkSpecs) {
		this.linkSpecs = linkSpecs;
	}
}
