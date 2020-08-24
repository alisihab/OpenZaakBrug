package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerDocumentIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

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
