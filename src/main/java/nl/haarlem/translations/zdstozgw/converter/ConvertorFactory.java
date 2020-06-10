package nl.haarlem.translations.zdstozgw.converter;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ConvertorFactory {

	private final ConfigService configService;

	@Autowired
	public ConvertorFactory(ConfigService configService){
		this.configService = configService;
	}

  	public Converter getConvertor(String soapAction, String request) {
		String application = XmlUtils.getApplicicatieFromZender(request);
		Translation translation = configService.getTranslationBySoapActionAndApplicatie(soapAction, application);

		String classname = translation.implementation;
		String templatepath = translation.template;

		try {
			Class<?> c = Class.forName(classname);
			java.lang.reflect.Constructor<?> ctor = c.getConstructor(String.class);
			Object object = ctor.newInstance(new Object[] { templatepath });
			return  (Converter) object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}		
}
