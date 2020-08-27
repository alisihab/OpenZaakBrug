package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01GeefZaakDetails;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class GeefZaakDetailsTranslator extends Converter {

    public GeefZaakDetailsTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsZakLv01) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsZakLv01.class);
	}
	
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();
		ZdsZakLa01GeefZaakDetails zdsResponse = this.getZaakService().getZaakDetails(zdsZakLv01);
		zdsResponse.parameters = new ZdsParameters(zdsZakLv01.parameters);
		var response = XmlUtils.getSOAPMessageFromObject(zdsResponse);   
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}		
}
