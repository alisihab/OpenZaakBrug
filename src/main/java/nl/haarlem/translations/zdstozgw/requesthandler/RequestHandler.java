package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.converter.Converter;

@Data
public abstract class RequestHandler {

    protected Converter converter;
    protected ConfigService configService;

    public RequestHandler(Converter converter, ConfigService configService){
        this.converter = converter;
        this.configService = configService;
    }

    public abstract String execute(String request, String requestUrl, String requestSoapAction);
}
