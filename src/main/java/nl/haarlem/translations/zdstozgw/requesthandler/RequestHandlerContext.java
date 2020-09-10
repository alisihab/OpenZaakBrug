package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;

@Data
public class RequestHandlerContext {

	protected String url;
	protected String soapAction;
	protected String requestBody;
	protected String referentienummer;

	public RequestHandlerContext(String url, String soapAction, String requestBody) {
		this.url = url;
		this.soapAction = soapAction;
		this.requestBody  = requestBody;
		this.referentienummer= java.util.UUID.randomUUID().toString();
	}	
}
