package nl.haarlem.translations.zdstozgw.converter;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.Translation;
import nl.haarlem.translations.zdstozgw.converter.impl.ActualiseerZaakStatusConverter;
import nl.haarlem.translations.zdstozgw.converter.impl.CreeerZaakConverter;
import nl.haarlem.translations.zdstozgw.converter.impl.GeefZaakdocumentLezenConverter;
import nl.haarlem.translations.zdstozgw.converter.impl.VoegZaakdocumentToeConverter;

public class ConverterFactory {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static ConfigService config = null;

	static {
		try {
			// cannot be done inline, ConfigService can throw an exception
			config = new ConfigService();
		} catch (Exception ex) {
			log.error("Could not load the configuration", ex);
		}
	}	

	public static Converter getConverter(String soapAction, String requestbody) {

		String classname = null;
		String templatepath = null;
		String legacyservice = null;

		for (Translation translation : config.getConfiguratie().getTranslations()) {
			if (translation.soapaction.equals(soapAction) && requestbody.contains(translation.requestcontains)) {
				classname = translation.implementation;
				templatepath = translation.template;
				legacyservice = translation.legacyservice;
				log.info("Using translation: '" + translation.translation + "' for soapaction:" + soapAction);
				break;
			}
		}
		// START-FALLBACK-OBSOLETE: the old code from getConvertor()
		if (classname == null) {
			log.warn("Fallback for soapaction:" + soapAction + " with application:" + requestbody);

			switch (soapAction) {
			case "genereerZaakIdentificatie_Di02":
			case "http://www.egem.nl/StUF/sector/zkn/0310/genereerZaakIdentificatie_Di02":
				classname = "nl.sudwestfryslan.translations.zdstozgw.implementation.GenereerZaakIdentificatie";
				templatepath = "src\\main\\java\\nl\\sudwestfryslan\\translations\\zdstozgw\\implementation\\genereerZaakIdentificatie_Du02.xml";
				break;
			case "http://www.egem.nl/StUF/sector/zkn/0310/creeerZaak_Lk01":
			case "creeerZaak_ZakLk01":
				classname = CreeerZaakConverter.class.getName();
				break;
			case "http://www.egem.nl/StUF/sector/zkn/0310/voegZaakdocumentToe_Lk01":
				classname = VoegZaakdocumentToeConverter.class.getName();
				break;
			case "http://www.egem.nl/StUF/sector/zkn/0310/geefZaakdocumentLezen_Lv01":
				classname = GeefZaakdocumentLezenConverter.class.getName();
				break;
			case "http://www.egem.nl/StUF/sector/zkn/0310/actualiseerZaakstatus_Lk01":
				classname = ActualiseerZaakStatusConverter.class.getName();
				break;

			default:
				return null;
			}
		}
		// END-FALLBACK-OBSOLETE: the old code from getConvertor()

		if (classname == null) {
			return null;
		}
		try {
			log.info("Loading class: '" + classname + "' with template path: '" + templatepath + "' and legacy url:'" + legacyservice + "'");
			Class<?> c = Class.forName(classname);
			java.lang.reflect.Constructor<?> ctor = c.getConstructor(String.class, String.class);
			Object object = ctor.newInstance(new Object[] { templatepath, legacyservice });
			return (Converter) object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
