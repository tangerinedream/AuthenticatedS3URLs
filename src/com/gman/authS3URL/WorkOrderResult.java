/**
 * 
 */
package com.gman.authS3URL;

/**
 * @author Dipper
 *
 */
public class WorkOrderResult {
	protected WorkOrder workOrder=null;
	protected SignedUrlResult signedHttpUrl=null;
	protected SignedUrlResult signedHttpsUrl=null;
	
	public WorkOrderResult(WorkOrder wo) {
		workOrder=wo;
	}
	
	public static class SignedUrlResult {
		private String protocol=null;
		private String url=null;
		private boolean valid=false;
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public boolean isValid() {
			return valid;
		}
		public void setValid(boolean valid) {
			this.valid = valid;
		}
		
		public String toString() {
			if(valid) 
				return("URL Validated");
			
			if(url!=null)
				return(url.toString());
			
			return(null);
		}
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		if(this.getSignedHttpsUrl()!=null) {
			sb.append("\n" + this.getSignedHttpsUrl().toString());
		}
		if(this.getSignedHttpUrl() != null) {
			sb.append("\n" + this.getSignedHttpUrl().toString());
		}
		return(sb.toString());
	}

	public WorkOrder getWorkOrder() {
		return workOrder;
	}

	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}

	public SignedUrlResult getSignedHttpUrl() {
		return signedHttpUrl;
	}

	public void setSignedHttpUrl(SignedUrlResult signedHttpUrl) {
		this.signedHttpUrl = signedHttpUrl;
	}

	public SignedUrlResult getSignedHttpsUrl() {
		return signedHttpsUrl;
	}

	public void setSignedHttpsUrl(SignedUrlResult signedHttpsUrl) {
		this.signedHttpsUrl = signedHttpsUrl;
	}

}
