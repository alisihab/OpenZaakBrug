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
import java.io.File;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


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

//    	
//        Scanner scanner = null;
//        try {
//            File resource = new ClassPathResource("config.json").getFile();
//            scanner = new Scanner(resource);
//        } catch (Exception ex) {
//        	log.error("error loading content from file", ex);
//        	log.error("######################################################################################");
//            log.error("#####                                                                            #####");
//            log.error("##### Unable to load configuration. Make sure 'config.json' is on the classpath  #####");
//            log.error("#####                                                                            #####");
//            log.error("######################################################################################");
//            throw ex;
//        }
//        try {
//            String result = "";
//            while (scanner.hasNextLine()) {
//                result += scanner.nextLine();
//            }
//            Gson gson = new Gson();
//            this.configuratie = gson.fromJson(result, Configuratie.class);
//            
//            if(configuratie == null) {
//            	log.error("configuratie == null");
//            	log.error("json:" + result);
//            	throwException();
//            }
//
//        } catch (Exception ex) {
//        	log.error("error loading json from string", ex);
//        	throwException();
//        }
        validateConfiguration();
    }

//    private void throwException() throws Exception {
//        log.error("##########################################################################################");
//        log.error("#####                                                                                #####");
//        log.error("##### Unable to load configuration. Make sure 'config.json' contains a valid config  #####");
//        log.error("#####                                                                                #####");
//        log.error("##########################################################################################");
//        throw new Exception();
//    }
//
    private void validateConfiguration() throws Exception {
//        try {
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
//        } catch (Exception ex) {
//        	log.error("error validating config", ex);
//            throwException();
//        }

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
