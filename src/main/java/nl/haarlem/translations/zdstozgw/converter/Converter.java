package nl.haarlem.translations.zdstozgw.converter;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZdsZknDocument;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

@Data
public abstract class Converter {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	protected Translation translation;
	protected ZaakService zaakService;
	private RequestResponseCycle session;
	protected ZdsZknDocument zdsDocument;

	public Converter(RequestResponseCycle session, Translation translation, ZaakService zaakService) {
		this.session = session;
		this.translation = translation;
		this.zaakService = zaakService;
	}

	public abstract void load() throws ResponseStatusException;

	public abstract ResponseEntity<?> execute() throws ConverterException;
}