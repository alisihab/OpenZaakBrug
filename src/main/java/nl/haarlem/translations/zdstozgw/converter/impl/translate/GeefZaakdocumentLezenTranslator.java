package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsAntwoord;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLa01GeefZaakdocumentLezen;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public class GeefZaakdocumentLezenTranslator extends Converter {

    public GeefZaakdocumentLezenTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
        super(context, translation, zaakService);
    }


	@Override
	public void load() throws ResponseStatusException {
        this.zdsDocument = (ZdsEdcLv01) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsEdcLv01.class);
	}		

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsEdcLv01 = (ZdsEdcLv01) this.getZdsDocument();
		var documentIdentificatie = zdsEdcLv01.gelijk.identificatie;
		ZdsZaakDocument document  = this.getZaakService().getZaakDocumentLezen(documentIdentificatie);
        var edcLa01 = new ZdsEdcLa01GeefZaakdocumentLezen(zdsEdcLv01.stuurgegevens);
        edcLa01.antwoord = new ZdsAntwoord();
        edcLa01.antwoord.object = document;
        edcLa01.parameters = new ZdsParameters(zdsEdcLv01.parameters);
		var response = XmlUtils.getSOAPMessageFromObject(edcLa01);
        return new ResponseEntity<>(response, HttpStatus.OK);	
	}		
}