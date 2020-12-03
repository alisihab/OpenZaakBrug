package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;

@Data
public class RequestHandlerContext {

	protected String url;
	protected String soapAction;
	protected String requestBody;
	protected String referentienummer;
	protected String kenmerk;
	
	public RequestHandlerContext(String url, String soapAction, String requestBody) {
		this.url = url;
		this.soapAction = soapAction;
		this.requestBody = requestBody;
		this.referentienummer = "ozb-" + java.util.UUID.randomUUID().toString();
	}
}
