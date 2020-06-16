package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.SpringContext;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.config.model.ResponseType;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycle;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.logging.RequestResponseCycleService;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class ReplicationRequestHandler extends RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RequestResponseCycleService requestResponseCycleService;

    private String request;

    public ReplicationRequestHandler(Converter converter, ConfigService configService) {
        super(converter, configService);
        requestResponseCycleService = SpringContext.getBean(RequestResponseCycleService.class);
    }

    @Override
    public String execute(String request, String requestUrl, String requestSoapAction) {
        Configuratie configuratie = configService.getConfiguratie();
        validateReplicationConfiguration(configuratie);
        RequestResponseCycle requestResponseCycle = new RequestResponseCycle()
                .setClientRequestBody(request)
                .setClientSoapAction(requestSoapAction)
                .setClientUrl(requestUrl)
                .setReplicationModus(this.getConfigService().getConfiguratie().getReplication().toString())
                .setConverterImplementation(this.getConverter().getTranslation().getImplementation())
                .setConverterTemplate(this.getConverter().getTranslation().getImplementation());
        requestResponseCycleService.add(requestResponseCycle);
        this.requestResponseCycleService.setSessionUUID(requestResponseCycle.getSessionUuid());

        this.request = request;
        String responseZDS = null, responseZGW = null;

        if (configuratie.getReplication().enableZDS) {
            responseZDS = this.postZdsRequest();
        }

        if (configuratie.getReplication().enableZGW) {
            responseZGW = this.converter.convert(request);
        }

        String response = null;
        switch (configuratie.getReplication().getResponseType()) {
            case ZDS: response = responseZDS; break;
            case ZGW: response = responseZGW; break;
        }

//        requestResponseCycle.setClientResponseCode(HttpStatus.OK.toString())
//                .setClientResponeBody(response);
        return response;
    }


    private void validateReplicationConfiguration(Configuratie configuratie) {
        boolean enableZDS = configuratie.getReplication().enableZDS;
        boolean enableZGW = configuratie.getReplication().enableZGW;
        ResponseType responseType = configuratie.getReplication().getResponseType();

        if ((!enableZDS && !enableZGW)
                || ((enableZDS || enableZGW) && (responseType != ResponseType.ZDS && responseType != ResponseType.ZGW))
                || (responseType == ResponseType.ZDS && !enableZDS)
                || (responseType == ResponseType.ZGW && !enableZGW)) {
            throw new RuntimeException("Replication configuration is not setup correctly.");
        }
    }

    protected String postZdsRequest() {
        String url = this.converter.getTranslation().getLegacyservice();
        String soapAction = this.converter.getTranslation().getSoapAction();
        log.info("Performing ZDS request to: '" + url + "' for soapaction:" + soapAction);
        var post = new PostMethod(this.converter.getTranslation().getLegacyservice());
        try {
            post.setRequestHeader("SOAPAction", soapAction);
            post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
            StringRequestEntity requestEntity = new org.apache.commons.httpclient.methods.StringRequestEntity(this.request, "text/xml", "utf-8");
            post.setRequestEntity(requestEntity);
            var httpclient = new org.apache.commons.httpclient.HttpClient();
            int responsecode = httpclient.executeMethod(post);
            String zdsResponseCode = "" + responsecode;
            String zdsResponseBody = post.getResponseBodyAsString();

            if (responsecode != 200) {
                log.warn("Receive the responsecode status " + responsecode + "  from: " + url + " (dus geen status=200  van het ouwe zaaksysteem)");
            }
            return zdsResponseBody;
        } catch (IOException ce) {
            throw new RuntimeException("OpenZaakBrug kon geen geen verbinding maken met:" + url);
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
    }
}
