package nl.haarlem.translations.zdstozgw.converter;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterFactory {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConfigService configService;
    private final ZaakService zaakService;

    @Autowired
    public ConverterFactory(ConfigService configService, ZaakService zaakService) {
        this.configService = configService;
        this.zaakService = zaakService;
    }

    public Converter getConverter(String path, String soapAction) throws ConverterException {
        Translation translation = this.configService.getTranslationByPathAndSoapAction(path, soapAction);
        
        if(translation == null) {
        	String combinations = "";
        	for(Translation t : this.configService.getConfiguratie().getTranslations()) {
        		combinations += "\n\tpath: '" + t.getPath()+ "' soapaction: '" + t.getSoapAction() + "'";
        	}
        	log.error("Could not load a convertor for path: '" + path + "' with soapaction: '" + soapAction + "'");        	
        	throw new ConverterException("Could not load a convertor for path: '" + path + "' with soapaction: '" + soapAction + "'\navailable services:" + combinations);
        }
        String classname = translation.implementation;
        try {
            Class<?> c = Class.forName(classname);
            java.lang.reflect.Constructor<?> ctor = c.getConstructor(Translation.class, ZaakService.class);
            Object object = ctor.newInstance(new Object[]{translation, zaakService});
            return (Converter) object;
        } 
        catch (Exception e) {        	
        	log.error("error loading class:" + classname, e);
        	throw new ConverterException("Error loading class:" + classname, e);
        }
    }
}