package nl.haarlem.translations.zdstozgw.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.Translation;

@Service
@Data
public class ConfigService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Configuratie configuratie;

	public ConfigService() throws Exception {
		var cpr = new ClassPathResource("config.json");
		var filename = cpr.getFile().getAbsoluteFile();
		log.info("Loading config from:" + filename);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

		Gson gson = new Gson();
		this.configuratie = gson.fromJson(bufferedReader, Configuratie.class);

		validateConfiguration();
		log.debug("ConfigService succesfully loaded");		
	}

	private void validateConfiguration() throws Exception {
		log.debug("validateConfiguration");
		log.debug("requestHandlerImplementation:" + this.configuratie.getRequestHandlerImplementation());

		this.configuratie.getOrganisaties().size();
		log.debug("=== organisaties ===");
		for (Organisatie organisatie : this.configuratie.getOrganisaties()) {
			log.debug("gemeentenaam:" + organisatie.getGemeenteNaam());
			log.debug("gemeentecode:" + organisatie.getGemeenteCode());
			log.debug("rsin:" + organisatie.getRSIN());
		}

		var rolomschrijving = this.configuratie.getZgwRolOmschrijving();
		log.debug("=== rol omschrijvingen ===");
		log.debug("heeftBetrekkingOp:" + rolomschrijving.getHeeftBetrekkingOp());
		log.debug("heeftAlsBelanghebbende:" + rolomschrijving.getHeeftAlsBelanghebbende());
		log.debug("heeftAlsInitiator:" + rolomschrijving.getHeeftAlsInitiator());
		log.debug("heeftAlsUitvoerende:" + rolomschrijving.getHeeftAlsUitvoerende());
		log.debug("heeftAlsVerantwoordelijke:" + rolomschrijving.getHeeftAlsVerantwoordelijke());
		log.debug("heeftAlsGemachtigde:" + rolomschrijving.getHeeftAlsGemachtigde());
		log.debug("heeftAlsOverigeBetrokkene:" + rolomschrijving.getHeeftAlsOverigBetrokkene());

		var replicatie = this.configuratie.getReplication();
		log.debug("=== replicatie ===");
		log.debug("geefZaakDetailsAction:" + replicatie.getGeefZaakdetails().getSoapaction());
		log.debug("geefZaakDetailsUrl:" + replicatie.getGeefZaakdetails().getUrl());
		log.debug("geefLijstZaakdocumentenAction:" + replicatie.getGeefLijstZaakdocumenten().getSoapaction());
		log.debug("geefLijstZaakdocumentenUrl:" + replicatie.getGeefLijstZaakdocumenten().getUrl());
		log.debug("geefZaakDocumentLezenAction:" + replicatie.getGeefZaakdocumentLezen().getSoapaction());
		log.debug("geefZaakDocumentLezenUrl:" + replicatie.getGeefZaakdocumentLezen().getUrl());

		this.configuratie.getTranslations().size();
		log.debug("=== translaties ===");
		for (Translation translation : this.configuratie.getTranslations()) {
			log.debug("translation:" + translation.getTranslation());
			log.debug("path:" + translation.getPath());
			log.debug("soapAction:" + translation.getSoapAction());
			log.debug("template:" + translation.getTemplate());
			log.debug("implementation:" + translation.getImplementation());
			log.debug("legacyservice:" + translation.getLegacyservice());
		}
	}

	public Translation getTranslationByPathAndSoapAction(String path, String soapAction) {
		log.debug("searching first translaton for : /" + path + "/ with soapaction: " + soapAction);
		for (Translation translation : this.configuratie.getTranslations()) {
			log.debug("\t checking path '" + translation.getPath() + "' with action: '" + translation.getSoapAction()
					+ "'");
			if (path.equals(translation.getPath()) && soapAction.equals(translation.getSoapAction())) {
				return translation;
			}
		}
		return null;
	}
}