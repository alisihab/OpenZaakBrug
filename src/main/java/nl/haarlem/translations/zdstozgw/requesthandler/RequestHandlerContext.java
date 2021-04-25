package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;

// obsolete

@Data
public class RequestHandlerContext {
}
/*
	private String modus;
	private String version;
	private String protocol;
	private String endpoint;
	protected String url;
	protected String soapAction;
	protected String requestBody;
	protected String referentienummer;
	protected String kenmerk;
	
	public RequestHandlerContext(String modus, String version, String protocol, String endpoint, String url,
			String soapAction, String requestBody, String referentienummer) {
		this.modus = modus;
		this.version = version;
		this.protocol = protocol;
		this.endpoint = endpoint;
		this.url = url;
		this.soapAction = soapAction;
		this.requestBody = requestBody;
		this.referentienummer = referentienummer;
	}
}
*/
/*
		var session = this.getConverter().getSession();
		this.sessionService.save(session);


			long startTime = System.currentTimeMillis();
			String finalUrl = url;
			String zgwResponse = (String) debug.endpoint(debugName,
					() -> this.restTemplateService.getRestTemplate().postForObject(finalUrl, entity, String.class));
			long endTime = System.currentTimeMillis();			
			var duration = endTime - startTime;
			var message = "POST to: " + url + " took " + duration + " milliseconds";
			log.debug(message);
			debug.infopoint("Duration", message);
			log.debug("POST response: " + zgwResponse);
			return zgwResponse;
*/