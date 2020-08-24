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
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class ActualiseerZaakStatusTranslator extends Converter {

    public ActualiseerZaakStatusTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsZakLk01ActualiseerZaakstatus) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsZakLk01ActualiseerZaakstatus.class);
	}	
	
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
      	var zdsZakLk01ActualiseerZaakstatus = (ZdsZakLk01ActualiseerZaakstatus) this.zdsDocument;
      	var zgwZaak = this.getZaakService().actualiseerZaakstatus(zdsZakLk01ActualiseerZaakstatus);
      	var bv03 =  new ZdsBv03(zdsZakLk01ActualiseerZaakstatus.stuurgegevens);
		var response = XmlUtils.getSOAPMessageFromObject(bv03);        
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}
}
