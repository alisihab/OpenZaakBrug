package nl.haarlem.translations.zdstozgw.converter.impl.emulate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import nl.haarlem.translations.zdstozgw.jpa.EmulateParameterRepository;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerContext;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDi02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsGenereerZaakIdentificatieDu02;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZaakIdentificatie;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

public class GenereerZaakIdentificatieEmulator extends Converter {

	public GenereerZaakIdentificatieEmulator(RequestHandlerContext context, Translation translation,
			ZaakService zaakService) {
		super(context, translation, zaakService);
	}

	@Override
	public void load() throws ResponseStatusException {
		this.zdsDocument = (ZdsGenereerZaakIdentificatieDi02) XmlUtils.getStUFObject(this.getContext().getRequestBody(),
				ZdsGenereerZaakIdentificatieDi02.class);
	}

	@Override
	public ResponseEntity<?> execute() throws ConverterException {
		EmulateParameterRepository repository = SpringContext.getBean(EmulateParameterRepository.class);
		var prefixparam = repository.getOne("ZaakIdentificatiePrefix");
		var idparam = repository.getOne("ZaakIdentificatieHuidige");
		var identificatie = Long.parseLong(idparam.getParameterValue()) + 1;
		idparam.setParameterValue(Long.toString(identificatie));
		repository.save(idparam);

		var di02 = (ZdsGenereerZaakIdentificatieDi02) this.zdsDocument;
		var du02 = new ZdsGenereerZaakIdentificatieDu02(di02.stuurgegevens, this.context.getReferentienummer());
		du02.zaak = new ZdsZaakIdentificatie();
		du02.zaak.functie = "entiteit";
		du02.zaak.identificatie = prefixparam.getParameterValue() + identificatie;

		var response = XmlUtils.getSOAPMessageFromObject(du02);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
