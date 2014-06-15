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
		signedHttpUrl = new SignedUrlResult();
		signedHttpUrl.setProtocol("http");
		signedHttpsUrl = new SignedUrlResult();
		signedHttpsUrl.setProtocol("https");
		
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
			StringBuffer sb=new StringBuffer();
			if(valid) 
				sb.append("URL Target Validated");
			
			if(url!=null)
				sb.append("\n" + url.toString());
			
			return(sb.toString());
		}
	}
	
	public String toString() {
		StringBuffer sb=new StringBuffer();
		if(this.getSignedHttpsUrl().getUrl()!=null) {
			sb.append(this.getSignedHttpsUrl().toString());
			sb.append("\n");
		}
		if(this.getSignedHttpUrl().getUrl() != null) {
			sb.append(this.getSignedHttpUrl().toString());
			sb.append("\n");
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
