package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZknDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsFo03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01ActualiseerZaakstatus;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class CreeerZaakTranslator extends Converter {

    public CreeerZaakTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsZakLk01) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsZakLk01.class);
	}	
	
	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		ZdsZakLk01 zdsZakLk01 = (ZdsZakLk01) this.zdsDocument;
		String rsin = this.getZaakService().getRSIN(zdsZakLk01.stuurgegevens.zender.organisatie);
		ZdsZaak zdsZaak = zdsZakLk01.objects.get(0);   
      	var zgwZaak = this.getZaakService().creeerZaak(rsin, zdsZaak);
      	var bv03 = new ZdsBv03(zdsZakLk01.stuurgegevens);
		var response = XmlUtils.getSOAPMessageFromObject(bv03);   
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}
}