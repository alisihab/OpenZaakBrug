package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsBv03;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsEdcLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGeefZaakdocumentbewerkenDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsUpdateZaakdocumentDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaak;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZakLk01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class UpdateZaakdocumentTranslator extends Converter {

	public UpdateZaakdocumentTranslator(RequestHandlerContext context, Translation translation, ZaakService zaakService) {
		super(context, translation, zaakService);
	}

	@Override
	public void load() throws ResponseStatusException {
		this.zdsDocument = (ZdsUpdateZaakdocumentDi02) XmlUtils.getStUFObject(this.getContext().getRequestBody(), ZdsUpdateZaakdocumentDi02.class);
	}

	@Override
	public ResponseEntity<?> execute() throws ResponseStatusException {
		var zdsUpdateZaakdocumentDi02 = (ZdsUpdateZaakdocumentDi02) this.getZdsDocument();
		var lock = zdsUpdateZaakdocumentDi02.parameters.checkedOutId;
		var zdsWasInformatieObject = zdsUpdateZaakdocumentDi02.edcLk02.documenten.get(0);
		var zdsWordtInformatieObject = zdsUpdateZaakdocumentDi02.edcLk02.documenten.get(1);
		this.context.setKenmerk("documentidentificatie:" + zdsWasInformatieObject.identificatie);
		this.getZaakService().updateZaakDocument(lock, zdsWasInformatieObject, zdsWordtInformatieObject);

		var bv02 = new ZdsBv02();
		var response = XmlUtils.getSOAPMessageFromObject(bv02);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}