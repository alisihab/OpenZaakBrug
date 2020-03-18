package nl.haarlem.translations.zdstozgw.convertor;

import nl.haarlem.translations.zdstozgw.convertor.impl.CreeerZaak;
import nl.haarlem.translations.zdstozgw.convertor.impl.VoegZaakdocumentToe;

public class ConvertorFactory {

  	public static Convertor getConvertor(String soapAction, String application) {

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
        		classname = CreeerZaak.class.getName();
        		templatepath = "--template-zit-in-de-code--";
        		break;
        	case "http://www.egem.nl/StUF/sector/zkn/0310/voegZaakdocumentToe_Lk01":
        		classname = VoegZaakdocumentToe.class.getName();
        		templatepath = "--template-zit-in-de-code--";
        		break;
        	default: 
            	return null;
        }
		try {
			Class<?> c = Class.forName(classname);
			java.lang.reflect.Constructor<?> ctor = c.getConstructor(String.class);
			Object object = ctor.newInstance(new Object[] { templatepath });
			return  (Convertor) object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}		
}
