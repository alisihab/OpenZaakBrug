package nl.haarlem.translations.zdstozgw.converter;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterFactory {

    private final ConfigService configService;
    private final ZaakService zaakService;

    @Autowired
    public ConverterFactory(ConfigService configService, ZaakService zaakService) {
        this.configService = configService;
        this.zaakService = zaakService;
    }

    public Converter getConverter(String soapAction, String request) {
        String application = XmlUtils.getApplicicatieFromZender(request);
        Translation translation = this.configService.getDefaultOrApplicationSpecificTranslationBySoapActionAndApplication(soapAction, application);

        String classname = translation.implementation;

        try {
            Class<?> c = Class.forName(classname);
            java.lang.reflect.Constructor<?> ctor = c.getConstructor(Translation.class, ZaakService.class);
            Object object = ctor.newInstance(new Object[]{translation, zaakService});
            return (Converter) object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
