package nl.haarlem.translations.zdstozgw.config;

import com.google.gson.Gson;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Scanner;


@Service
@Data
public class ConfigService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Configuratie configuratie;

    public ConfigService() throws Exception {

        InputStream inputStream = null;
        Scanner s = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("config.json");
            s = new Scanner(inputStream).useDelimiter("\\A");
        } catch (Exception ex){
            log.error("######################################################################################");
            log.error("#####                                                                            #####");
            log.error("##### Unable to load configuration. Make sure 'config.json' is on the classpath  #####");
            log.error("#####                                                                            #####");
            log.error("######################################################################################");
            throw ex;
        }

        try {
            String result = s.hasNext() ? s.next() : "";
            Gson gson = new Gson();
            configuratie = gson.fromJson(result,Configuratie.class);

        } catch (Exception ex){
            throwException();
        }
        validateConfiguration();
    }

    private  void throwException() throws Exception {
        log.error("##########################################################################################");
        log.error("#####                                                                                #####");
        log.error("##### Unable to load configuration. Make sure 'config.json' contains a valid config  #####");
        log.error("#####                                                                                #####");
        log.error("##########################################################################################");
        throw new Exception();
    }

    private void validateConfiguration() throws Exception {
        try {
            configuratie.getOrganisaties().size();
            for(Organisatie organisatie:configuratie.getOrganisaties()){
                organisatie.getGemeenteCode();
                organisatie.getRSIN();
            }
            configuratie.getZaakTypes().size();
            for(ZaakType zaakType:configuratie.getZaakTypes()){
                zaakType.getZaakType();
                zaakType.getCode();
            }
        } catch (Exception ex){
            throwException();
        }

    }
}
