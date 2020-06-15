package nl.haarlem.translations.zdstozgw.requesthandler;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestHandlerFactory {

    private final ConfigService configService;

    @Autowired
    public RequestHandlerFactory(ConfigService configService) {
        this.configService = configService;
    }

    public RequestHandler getRequestHandler(Converter converter) {
        try {
            Class<?> c = Class.forName( this.configService.getConfiguratie().getRequestHandlerImplementation());
            java.lang.reflect.Constructor<?> ctor = c.getConstructor(Converter.class, ConfigService.class);
            Object object = ctor.newInstance(new Object[]{converter, this.configService});
            return (RequestHandler) object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
