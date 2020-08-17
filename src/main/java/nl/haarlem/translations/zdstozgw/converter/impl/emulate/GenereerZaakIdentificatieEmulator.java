package nl.haarlem.translations.zdstozgw.converter.impl.emulate;

import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.NotImplementedConverter;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class GenereerZaakIdentificatieEmulator extends NotImplementedConverter {

	public GenereerZaakIdentificatieEmulator(Translation translation, ZaakService zaakService) {
		super(translation, zaakService);
	}

}
