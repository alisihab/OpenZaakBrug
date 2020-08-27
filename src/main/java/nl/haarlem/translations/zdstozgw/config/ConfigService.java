package nl.haarlem.translations.zdstozgw.config;

import com.google.gson.Gson;
import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.config.model.Organisatie;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;


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
    }

    private void validateConfiguration() throws Exception {
    	this.configuratie.getRequestHandlerImplementation();
    	
    	this.configuratie.getOrganisaties().size();
        for (Organisatie organisatie : this.configuratie.getOrganisaties()) {
        	organisatie.getGemeenteNaam();
            organisatie.getGemeenteCode();
            organisatie.getRSIN();
        }
        
        var rolomschrijving = this.configuratie.getZgwRolOmschrijving();            
        rolomschrijving.getHeeftAlsBelanghebbende();
        rolomschrijving.getHeeftAlsInitiator();
        rolomschrijving.getHeeftAlsUitvoerende();
        rolomschrijving.getHeeftAlsVerantwoordelijke();
        rolomschrijving.getHeeftAlsGemachtigde();
        rolomschrijving.getHeeftAlsOverigBetrokkene();
        
        this.configuratie.getTranslations().size();
        for (Translation translation: this.configuratie.getTranslations()) {
        	translation.getTranslation();
        	translation.getPath();
        	translation.getSoapAction();
        	translation.getTemplate();
        	translation.getImplementation();
        	translation.getLegacyservice();
        }
    }

    public Translation getTranslationByPathAndSoapAction(String path, String soapAction) {
		log.debug("searching first translaton for : /" + path + "/ with soapaction: " + soapAction);
		for (Translation translation : configuratie.getTranslations()) {
			log.debug("\t checking path '" + translation.getPath() + "' with action: '" + translation.getSoapAction() + "'");
			if (path.equals(translation.getPath()) && soapAction.equals(translation.getSoapAction())) {
				return translation;
			}
		}
    	return null;
    }
}
