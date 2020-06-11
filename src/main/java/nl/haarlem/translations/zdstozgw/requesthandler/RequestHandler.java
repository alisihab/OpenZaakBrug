package nl.haarlem.translations.zdstozgw.requesthandler;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.converter.Converter;

@Data
public abstract class RequestHandler {

    protected Converter converter;

    public RequestHandler(Converter converter){
        this.converter = converter;
    }

    public abstract <T> T execute(String request);
}
