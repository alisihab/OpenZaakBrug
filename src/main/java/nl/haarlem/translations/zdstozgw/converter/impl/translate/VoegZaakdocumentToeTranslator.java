package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class VoegZaakdocumentToeTranslator extends Converter {

    public VoegZaakdocumentToeTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }

	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsEdcLk01) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsEdcLk01.class);
	}

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsEdcLk01 = (ZdsEdcLk01) this.getZdsDocument();		
		var zdsInformatieObject = zdsEdcLk01.objects.get(0);		
		this.getZaakService().voegZaakDocumentToe(this.getZaakService().getRSIN(zdsEdcLk01.stuurgegevens.zender.organisatie), zdsInformatieObject);		
		var bv03 = new ZdsBv03(zdsEdcLk01.stuurgegevens, context.getReferentienummer());
		var response = XmlUtils.getSOAPMessageFromObject(bv03);   
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}	
}
