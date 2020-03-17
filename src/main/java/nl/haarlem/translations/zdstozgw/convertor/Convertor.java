package nl.haarlem.translations.zdstozgw.convertor;
import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public interface Convertor {

	String Convert(ZaakService zaakService, Object object);

}
