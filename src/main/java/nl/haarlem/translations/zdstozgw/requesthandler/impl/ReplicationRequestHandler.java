package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;

public class ReplicationRequestHandler extends RequestHandler {

    private Configuratie configuratie;
    private String request;

    public ReplicationRequestHandler(Converter converter, ConfigService configService) {
        super(converter);
        this.configuratie = configService.getConfiguratie();
    }

    @Override
    public String execute(String request) {
        this.request = request;
        String response = null;

        if(configuratie.getReplication().enableZDS){

        }

        if(configuratie.getReplication().enableZGW){

        }

        switch (configuratie.getReplication().getResponseType()){
            case ZDS: break;
            case ZGW: break;
        }

        return this.converter.convert(request);
    }

//    protected String postZdsRequest(RequestResponseCycle session) {
//        // what are we going to do?
//        session.addZdsRequest(this.zdsUrl, this.converter.getTranslation().getSoapaction(), this.converter.getTranslation().getSoapaction());
//        log.info("Performing ZDS request to: '" + this.zdsUrl + "' for soapaction:" + zdsSoapAction);
//        var post = new PostMethod(this.zdsUrl);
//        try {
//            post.setRequestHeader("SOAPAction", zdsSoapAction);
//            post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
//            StringRequestEntity requestEntity = new org.apache.commons.httpclient.methods.StringRequestEntity(zdsRequest, "text/xml", "utf-8");
//            post.setRequestEntity(requestEntity);
//            var httpclient = new org.apache.commons.httpclient.HttpClient();
//            int responsecode = httpclient.executeMethod(post);
//            String zdsResponseCode = "" + responsecode;
//            String zdsResponseBody = post.getResponseBodyAsString();
//
//            if (responsecode != 200) {
//                log.warn("Receive the responsecode status " + responsecode + "  from: " + this.zdsUrl + " (dus geen status=200  van het ouwe zaaksysteem)");
//            }
//            session.addZdsRespone(zdsResponseCode, zdsResponseBody);
//            return zdsResponseBody;
//        } catch (IOException ce) {
//            throw new ConverterException(this, "OpenZaakBrug kon geen geen verbinding maken met:" + this.zdsUrl, zdsRequest,ce);
//        } finally {
//            // Release current connection to the connection pool once you are done
//            post.releaseConnection();
//        }
//    }
}
