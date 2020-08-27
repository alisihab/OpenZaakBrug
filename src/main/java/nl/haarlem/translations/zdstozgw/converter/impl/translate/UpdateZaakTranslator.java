package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class UpdateZaakTranslator extends Converter {

    public UpdateZaakTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsZakLk01) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsZakLk01.class);
	}	

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsZakLk01 = (ZdsZakLk01) this.getZdsDocument();
		this.getZaakService().updateZaak(zdsZakLk01.objects.get(0), zdsZakLk01.objects.get(1));
		var bv03 = new ZdsBv03(zdsZakLk01.stuurgegevens);
		var response = XmlUtils.getSOAPMessageFromObject(bv03);   
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}	
}
