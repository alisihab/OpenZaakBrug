package nl.haarlem.translations.zdstozgw.converter;

import nl.haarlem.translations.zdstozgw.converter.impl.ActualiseerZaakStatusConverter;
import nl.haarlem.translations.zdstozgw.converter.impl.CreeerZaakConverter;
import nl.haarlem.translations.zdstozgw.converter.impl.GeefZaakDocumentLezenConverter;
import nl.haarlem.translations.zdstozgw.converter.impl.VoegZaakdocumentToeConverter;

public class ConvertorFactory {

  	public static Converter getConvertor(String soapAction, String application) {

		String classname = null;
		String templatepath = null;
	
		switch(soapAction) 
        { 
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
				classname = GeefZaakDocumentLezenConverter.class.getName();
				break;
			case "http://www.egem.nl/StUF/sector/zkn/0310/actualiseerZaakstatus_Lk01":
				classname = ActualiseerZaakStatusConverter.class.getName();
				break;

        	default: 
            	return null;
        }
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
