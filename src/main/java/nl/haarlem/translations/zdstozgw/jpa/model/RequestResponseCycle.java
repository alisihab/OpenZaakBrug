package nl.haarlem.translations.zdstozgw.jpa.model;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.springframework.http.HttpStatus;


@Entity
public class RequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;

	// Wat heeft de client gevraagd en gekregen
	private Date timestamp;
	private Duration duration;
	private String clientUrl;
	private String clientSoapAction;
	@Lob
	private String clientRequestBody;
	@Lob
	private String clientResponeBody;
	private String clientResponeCode;

	// Wanneer we ergens in het proces een fout hebben, dan willen we die bewaren
	@Lob
	private String stackTrace;		
	
	// Welke modus draaiden we en hadden we een fout?
	private String replicationModus;
	private String converterImplementation;
	private String converterTemplate;

	// Wat is heeft het zds zaaksysteem gekregen en ontvangen
	@Transient
	private int zdsCount = 0;
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
	
	@Transient
	private int zgwCount = 0;
	@Lob
	private String zgwUrl;
	@Lob
	private String zgwRequestBody;
	@Lob
	private String zgwResponseBody;

	public RequestResponseCycle(String clientUrl, String clientSoapAction, String clientRequestBody) {
		this.timestamp = new Date();
		this.clientUrl = clientUrl;
		this.clientSoapAction = clientSoapAction;
		this.clientRequestBody = clientRequestBody;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Duration getDuration() {
		return this.duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setClientResponse(HttpStatus responseCode, String responseBody) {
		this.clientResponeCode = responseCode.toString();
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
		this.zdsUrl = (this.zdsUrl == null ?  zdsUrl : this.zdsUrl + "\n[zds#" + zdsCount + "]---------------------\n" + zdsUrl);
		this.zdsSoapAction = (this.zdsSoapAction == null ?  zdsSoapAction : this.zdsSoapAction + "\n[zds#" + zdsCount + "]---------------------\n" + zdsSoapAction);
		this.zdsRequestBody = (this.zdsRequestBody == null ?  zdsRequestBody : this.zdsRequestBody + "\n[zds#" + zdsCount + "]---------------------\n" + zdsRequestBody);
	}

	public void addZdsRespone(String zdsResponseCode, String zdsResponseBody) {
		this.zdsResponseCode = (this.zdsResponseCode == null ?  zdsResponseCode : this.zdsResponseCode + "\n[zds#" + zdsCount + "]---------------------\n" + zdsResponseCode);
		this.zdsResponseBody = (this.zdsResponseBody == null ?  zdsResponseBody : this.zdsResponseBody + "\n[zds#" + zdsCount + "]---------------------\n" + zdsResponseBody);				
		zdsCount += 1;
	}
	
	public void addZgwRequest(String request, String method, String json) {
		this.zgwUrl = (this.zgwUrl == null ?  method + " " + request : this.zgwUrl + "\n[zgw#" + zgwCount + "]---------------------\n" + method + " " + request);
		this.zgwRequestBody = (this.zgwRequestBody == null ?  request : this.zgwRequestBody+ "\n[zgw" + zgwCount + "]---------------------\n" + request); 
	}

	public void addZgwResponse(String response) {
		this.zgwResponseBody = (this.zgwResponseBody == null ?  response : this.zgwResponseBody + "\n[zgw" + zgwCount + "]---------------------\n" + response);
		zgwCount += 1;
	}	
}
