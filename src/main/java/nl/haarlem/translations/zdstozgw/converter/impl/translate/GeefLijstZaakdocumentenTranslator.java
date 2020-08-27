package nl.haarlem.translations.zdstozgw.converter.impl.translate;

import java.util.ArrayList;

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
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsParameters;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsStuurgegevens;
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
		ZdsZakLv01 zdsZakLv01 = (ZdsZakLv01) this.getZdsDocument();
		var zaakidentificatie = zdsZakLv01.gelijk.identificatie;
		var relevanteDocumenten= this.getZaakService().geefLijstZaakdocumenten(zaakidentificatie);
		
		ZdsZakLa01LijstZaakdocumenten zdsZakLa01LijstZaakdocumenten = new ZdsZakLa01LijstZaakdocumenten(zdsZakLv01.stuurgegevens);
        zdsZakLa01LijstZaakdocumenten.antwoord = new ZdsZakLa01LijstZaakdocumenten.Antwoord();
        zdsZakLa01LijstZaakdocumenten.stuurgegevens = new ZdsStuurgegevens(zdsZakLv01.stuurgegevens);
        zdsZakLa01LijstZaakdocumenten.stuurgegevens.berichtcode = "La01";
        zdsZakLa01LijstZaakdocumenten.stuurgegevens.entiteittype = "ZAK";
        zdsZakLa01LijstZaakdocumenten.parameters  = new ZdsParameters(zdsZakLv01.parameters);
        zdsZakLa01LijstZaakdocumenten.antwoord.object = new ZdsZakLa01LijstZaakdocumenten.Antwoord.Object();
        zdsZakLa01LijstZaakdocumenten.antwoord.object.identificatie = zaakidentificatie;
        zdsZakLa01LijstZaakdocumenten.antwoord.object.heeftRelevant = relevanteDocumenten;
        
      	var response = XmlUtils.getSOAPMessageFromObject(zdsZakLa01LijstZaakdocumenten);   
        return new ResponseEntity<>(response, HttpStatus.OK);        
	}	
}
