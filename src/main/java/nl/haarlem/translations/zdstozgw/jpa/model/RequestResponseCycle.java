package nl.haarlem.translations.zdstozgw.jpa.model;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RequestResponseCycle {    
	@Id @GeneratedValue long id; 
    
	// Wat heeft de client gevraagd en gekregen
	Date timestamp;
    Duration duration;
    String	clientUrl;
    String	clientSoapAction;
    @Lob 
    String	clientRequestBody;
    @Lob 
    String	clientResponeBody;
    String	clientResponeCode;
   
    // Welke modus draaiden we
    String	replicationModus;        
    String	converterImplementation;
    String	converterTemplate;

    // Wat is heeft het zds zaaksysteem gekregen en ontvangen
    String	zdsUrl;
    String	zdsSoapAction;
    @Lob 
    String	zdsRequest;
    @Lob 
    String	zdsResponeBody;
    String	zdsResponeCode;

    // Wat is er door zgw is bedacht
    @Lob 
    String	zgwResponeBody;
    
    // Wanneer we ergens in het proces een fout hebben, dan willen we die bewaren
    @Lob
    String	stackTrace;
    
    
    
	public RequestResponseCycle(String clientUrl, String clientSoapAction, String clientRequestBody) {
		this.clientUrl = clientUrl;
		this.clientSoapAction = clientSoapAction;
		this.clientRequestBody = clientRequestBody;
	}



	public Date getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}



	public Duration getDuration() {
		return duration;
	}



	public void setDuration(Duration duration) {
		this.duration = duration;
	}



	public String getClientUrl() {
		return clientUrl;
	}



	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}



	public String getClientSoapAction() {
		return clientSoapAction;
	}



	public void setClientSoapAction(String clientSoapAction) {
		this.clientSoapAction = clientSoapAction;
	}



	public String getClientRequestBody() {
		return clientRequestBody;
	}



	public void setClientRequestBody(String clientRequestBody) {
		this.clientRequestBody = clientRequestBody;
	}



	public String getClientResponeBody() {
		return clientResponeBody;
	}



	public void setClientResponeBody(String clientResponeBody) {
		this.clientResponeBody = clientResponeBody;
	}



	public String getClientResponeCode() {
		return clientResponeCode;
	}



	public void setClientResponseCode(String clientResponeCode) {
		this.clientResponeCode = clientResponeCode;
	}



	public String getReplicationModus() {
		return replicationModus;
	}



	public void setReplicationModus(String replicationModus) {
		this.replicationModus = replicationModus;
	}



	public String getConverterImplementation() {
		return converterImplementation;
	}



	public void setConverterImplementation(String converterImplementation) {
		this.converterImplementation = converterImplementation;
	}



	public String getConverterTemplate() {
		return converterTemplate;
	}



	public void setConverterTemplate(String converterTemplate) {
		this.converterTemplate = converterTemplate;
	}



	public String getZdsUrl() {
		return zdsUrl;
	}



	public void setZdsUrl(String zdsUrl) {
		this.zdsUrl = zdsUrl;
	}



	public String getZdsSoapAction() {
		return zdsSoapAction;
	}



	public void setZdsSoapAction(String zdsSoapAction) {
		this.zdsSoapAction = zdsSoapAction;
	}



	public String getZdsRequest() {
		return zdsRequest;
	}



	public void setZdsRequest(String zdsRequest) {
		this.zdsRequest = zdsRequest;
	}



	public String getZdsResponeBody() {
		return zdsResponeBody;
	}



	public void setZdsResponeBody(String zdsResponeBody) {
		this.zdsResponeBody = zdsResponeBody;
	}



	public String getZdsResponeCode() {
		return zdsResponeCode;
	}



	public void setZdsResponeCode(String zdsResponeCode) {
		this.zdsResponeCode = zdsResponeCode;
	}



	public String getZgwResponeBody() {
		return zgwResponeBody;
	}



	public void setZgwResponeBody(String zgwResponeBody) {
		this.zgwResponeBody = zgwResponeBody;
	}



	public String getStackTrace() {
		return stackTrace;
	}



	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}



	public long getId() {
		return id;
	}


}
