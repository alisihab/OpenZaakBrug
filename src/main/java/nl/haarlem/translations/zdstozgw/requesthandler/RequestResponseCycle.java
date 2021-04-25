package nl.haarlem.translations.zdstozgw.requesthandler;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Entity
@Data
public class RequestResponseCycle {
	
	@Id
	@GeneratedValue
	private long id;

	private LocalDateTime startdatetime;
	private LocalDateTime stopdatetime;
	private long durationInMilliseconds;
	
	private String modus;
	private String version;
	private String protocol;
	private String endpoint;	
	
	private String clientUrl;
	private String clientSoapAction;
	@Column(columnDefinition="TEXT")
	private String clientRequestBody;
	private Integer clientRequestSize;	
	private String referentienummer;
	
	private String functie;
	private String kenmerk;		
	private String converterImplementation;
	private String converterTemplate;

	
	
	@Column(columnDefinition="TEXT")
	private String clientResponseBody;
	private int clientResponseCode;
	private Integer clientResponseSize;
	
	private Boolean zaakGerepliceerd = false;
	private Boolean documentenGerepliceerd = false;
		
	@Lob
	private String stackTrace;

	public RequestResponseCycle() {
		startdatetime = LocalDateTime.now();
	};
	
	public RequestResponseCycle(String modus, String version, String protocol, String endpoint, String url, String soapAction, String requestBody, String referentienummer) {		
		this.modus = modus;
		this.version = version;
		this.protocol = protocol;
		this.endpoint = endpoint;
		this.clientUrl = url;
		this.clientSoapAction = soapAction;
		this.clientRequestBody = requestBody;
		this.referentienummer = referentienummer;
		
		startdatetime = LocalDateTime.now();
		this.clientRequestSize = this.clientRequestBody.length();
	}

	public long getDurationInMilliseconds() {
		var milliseconds = Duration.between(startdatetime, LocalDateTime.now()).toMillis();
		return milliseconds;
	}

	public void setResonse(ResponseEntity<?> response) {
		this.clientResponseBody = response.getBody().toString();
		this.clientResponseSize = this.clientResponseBody.length();
		this.clientResponseCode = response.getStatusCodeValue();

		this.stopdatetime = LocalDateTime.now();
		this.durationInMilliseconds = Duration.between(startdatetime, stopdatetime).toMillis();
	}
}