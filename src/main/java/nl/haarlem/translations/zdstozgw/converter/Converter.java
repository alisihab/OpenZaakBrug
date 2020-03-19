package nl.haarlem.translations.zdstozgw.converter;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public interface Converter {

	String Convert(ZaakService zaakService, Object object);

}
