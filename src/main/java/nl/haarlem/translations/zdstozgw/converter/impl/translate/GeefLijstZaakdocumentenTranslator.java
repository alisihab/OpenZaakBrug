package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLa01LijstZaakdocumenten;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefLijstZaakdocumentenTranslator extends Converter {

    public GeefLijstZaakdocumentenTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsZakLv01) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsZakLv01.class);
	}	

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
      	var document = this.zdsDocument;
		ZdsZakLv01 zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();
		ZdsZakLa01LijstZaakdocumenten zdsResponse = this.getZaakService().geefLijstZaakdocumenten(zdsZakLv01);
		zdsResponse.parameters  = new ZdsParameters(zdsZakLv01.parameters);        
      	var response = XmlUtils.getSOAPMessageFromObject(zdsResponse);   
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}	
}
