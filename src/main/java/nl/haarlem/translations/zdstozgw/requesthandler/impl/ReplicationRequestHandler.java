package nl.haarlem.translations.zdstozgw.converthandler.impl;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.ReplicationModus;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;

public class ReplicationRequestHandler extends RequestHandler {

    private ZaakService zaakService;
    private ConfigService configService;
    private ReplicationModus replicationModus;

    public ReplicationRequestHandler(Converter converter, ZaakService zaakService, ConfigService configService) {
        super(converter);
        this.zaakService = zaakService;
        this.configService = configService;
        this.replicationModus = configService.getConfiguratie().replicationModus;
    }

    @Override
    public String execute() {

        switch (this.replicationModus){
            case USE_ZDS:

        }

        return this.converter.Convert(this.zaakService, null);
    }

    protected String postZdsRequest(RequestResponseCycle session, String zdsSoapAction, String zdsRequest) {
        // what are we going to do?
        session.addZdsRequest(this.zdsUrl, zdsSoapAction, zdsRequest);
        log.info("Performing ZDS request to: '" + this.zdsUrl + "' for soapaction:" + zdsSoapAction);
        var post = new PostMethod(this.zdsUrl);
        try {
            post.setRequestHeader("SOAPAction", zdsSoapAction);
            post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
            StringRequestEntity requestEntity = new org.apache.commons.httpclient.methods.StringRequestEntity(zdsRequest, "text/xml", "utf-8");
            post.setRequestEntity(requestEntity);
            var httpclient = new org.apache.commons.httpclient.HttpClient();
            int responsecode = httpclient.executeMethod(post);
            String zdsResponseCode = "" + responsecode;
            String zdsResponseBody = post.getResponseBodyAsString();

            if (responsecode != 200) {
                log.warn("Receive the responsecode status " + responsecode + "  from: " + this.zdsUrl + " (dus geen status=200  van het ouwe zaaksysteem)");
            }
            session.addZdsRespone(zdsResponseCode, zdsResponseBody);
            return zdsResponseBody;
        } catch (IOException ce) {
            throw new ConverterException(this, "OpenZaakBrug kon geen geen verbinding maken met:" + this.zdsUrl, zdsRequest,ce);
        } finally {
            // Release current connection to the connection pool once you are done
            post.releaseConnection();
        }
    }
}
