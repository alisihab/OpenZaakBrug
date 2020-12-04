package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLa01GeefZaakdocumentLezen;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGeefZaakdocumentbewerkenDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocumentAntwoord;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakDocumentInhoud;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GeefZaakdocumentBewerkenTranslator extends Converter {

	public GeefZaakdocumentBewerkenTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
		super(context, translation, zaakService);
	}

	@Override
	public void load() throws ResponseStatusException {
		this.zdsDocument = (ZdsGeefZaakdocumentbewerkenDi02) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsGeefZaakdocumentbewerkenDi02.class);
	}

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsGeefZaakdocumentbewerkenDi02 = (ZdsGeefZaakdocumentbewerkenDi02) this.getZdsDocument();
		var documentIdentificatie = zdsGeefZaakdocumentbewerkenDi02.edcLv01.gelijk.identificatie;
		this.context.setKenmerk("documentidentificatie:" + documentIdentificatie);
		var result = this.getZaakService().checkOutZaakDocument(documentIdentificatie);

		
		/*		var edcLa01 = new ZdsEdcLa01GeefZaakdocumentLezen(zdsEdcLv01.stuurgegevens, this.context.getReferentienummer());
		edcLa01.antwoord = new ZdsZaakDocumentAntwoord();
		edcLa01.antwoord.document = new ArrayList<ZdsZaakDocumentInhoud>();
		edcLa01.antwoord.document.add(document);
		edcLa01.parameters = new ZdsParameters(zdsEdcLv01.parameters);
		var response = XmlUtils.getSOAPMessageFromObject(edcLa01);
		return new ResponseEntity<>(response, HttpStatus.OK);
		*/
		
		throw new RuntimeException("not imlemented yet");
	}
}