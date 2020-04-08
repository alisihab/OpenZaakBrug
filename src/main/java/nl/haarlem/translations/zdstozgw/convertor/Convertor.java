package nl.haarlem.translations.zdstozgw.convertor;
import nl.haarlem.translations.zdstozgw.jpa.ApplicationParameterRepository;
import nl.haarlem.translations.zdstozgw.translation.zds.model.StufRequest;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public abstract class Convertor {

	
	public abstract String Convert(ZaakService zaakService, ApplicationParameterRepository repository, Object object);
	public abstract String getImplementation();
	public abstract String getTemplate();
}
