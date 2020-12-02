package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;

@Data
public class RequestHandlerContext {

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
