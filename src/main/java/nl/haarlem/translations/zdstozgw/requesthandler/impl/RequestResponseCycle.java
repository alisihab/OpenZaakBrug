package nl.haarlem.translations.zdstozgw.requesthandler.impl;

import lombok.Data;
import org.apache.commons.httpclient.HttpStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
public class RequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;

    // Wat heeft de client gevraagd en gekregen
    private LocalDateTime timestamp;
    private Duration duration;
    private String clientUrl;
    private String clientSoapAction;
    @Lob
    private String clientRequestBody;
    @Lob
    private String clientResponeBody;
    private String clientResponseCode;

    // Wanneer we ergens in het proces een fout hebben, dan willen we die bewaren
    @Lob
    private String stackTrace;

    // Welke modus draaiden we en hadden we een fout?
    private String replicationModus;
    private String converterImplementation;
    private String converterTemplate;

    // Wat is heeft het zds zaaksysteem gekregen en ontvangen
    @Lob
    private String zdsUrl;
    @Lob
    private String zdsSoapAction;
    @Lob
    private String zdsRequestBody;
    @Lob
    private String zdsResponseCode;
    @Lob
    private String zdsResponseBody;

    @Lob
    private String zgwUrl;
    @Lob
    private String zgwRequestBody;
    @Lob
    private String zgwResponseBody;

    public RequestResponseCycle(String clientUrl, String clientSoapAction, String clientRequestBody) {
        this.timestamp = LocalDateTime.now();
        this.clientUrl = clientUrl;
        this.clientSoapAction = clientSoapAction;
        this.clientRequestBody = clientRequestBody;
    }


    public void setClientResponse(HttpStatus responseCode, String responseBody) {
        this.clientResponseCode = responseCode.toString();
        this.clientResponeBody = responseBody;
    }

    public void setConverter(String replicationmodus, String implementation, String template) {
        this.replicationModus = replicationmodus;
        this.converterImplementation = implementation;
        this.converterTemplate = template;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public long getId() {
        return this.id;
    }

    public void addZdsRequest(String zdsUrl, String zdsSoapAction, String zdsRequestBody) {
        this.zdsUrl = (this.zdsUrl == null ?  zdsUrl : this.zdsUrl + "\n---------------------\n" + zdsUrl);
        this.zdsSoapAction = (this.zdsSoapAction == null ?  zdsSoapAction : this.zdsSoapAction + "\n---------------------\n" + zdsSoapAction);
        this.zdsRequestBody = (this.zdsRequestBody == null ?  zdsRequestBody : this.zdsRequestBody + "\n---------------------\n" + zdsRequestBody);
    }

    public void addZdsRespone(String zdsResponseCode, String zdsResponseBody) {
        this.zdsResponseCode = (this.zdsResponseCode == null ?  zdsResponseCode : this.zdsResponseCode + "\n---------------------\n" + zdsResponseCode);
        this.zdsResponseBody = (this.zdsResponseBody == null ?  zdsResponseBody : this.zdsResponseBody + "\n---------------------\n" + zdsResponseBody);
    }

    public void addZgwRequest(String request, String method, String json) {
        this.zgwUrl = (this.zgwUrl == null ?  method + " " + request : this.zgwUrl + "\n---------------------\n" + method + " " + request);
        this.zgwRequestBody = (this.zgwRequestBody == null ?  request : this.zgwRequestBody+ "\n---------------------\n" + request);
    }

    public void addZgwResponse(String response) {
        this.zgwResponseBody = (this.zgwResponseBody == null ?  response : this.zgwResponseBody + "\n---------------------\n" + response);
    }
}