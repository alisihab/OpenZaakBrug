package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;

@Data
public class RequestHandlerContext {

	private String url;
	private String soapAction;
	private String requestBody;

	public RequestHandlerContext(String url, String soapAction, String requestBody) {
		this.url = url;
		this.soapAction = soapAction;
		this.requestBody  = requestBody;
	}
	
}
