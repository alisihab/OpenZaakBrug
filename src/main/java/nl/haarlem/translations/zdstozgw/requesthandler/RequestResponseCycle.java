package nl.haarlem.translations.zdstozgw.requesthandler;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Indexed;

import lombok.Data;

@Data
@Entity
@Table(indexes= {
		@Index(columnList = "referentienummer"), 
		@Index(columnList = "kenmerk")}
)
public class RequestResponseCycle {
	static int MAX_MESSAGE_SIZE = 2 * 1024;
	
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
	@Column(columnDefinition="TEXT", name = "client_request_body")
	private String clientRequestBody;
	private Integer clientRequestSize;	
	private String referentienummer;
	
	private String functie;
	private String kenmerk;		
	private String converterImplementation;
	private String converterTemplate;

	@Column(columnDefinition="TEXT", name = "client_response_body")
	private String clientResponseBody;
	private int clientResponseCode;
	private Integer clientResponseSize;
	
	private Integer AantalZakenGerepliceerd = 0;
	private Integer AantalDocumentenGerepliceerd = 0;
		
	@Column(columnDefinition="TEXT")
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
		this.referentienummer = referentienummer;		
		
		// always store the orginal request
		this.clientRequestSize = requestBody.length();
		this.clientRequestBody = requestBody;

		startdatetime = LocalDateTime.now();
	}

	public long getDurationInMilliseconds() {
		var milliseconds = Duration.between(startdatetime, LocalDateTime.now()).toMillis();
		return milliseconds;
	}

	public static String shortenLongMessages(String message) {
		if(message.length() > RequestResponseCycle.MAX_MESSAGE_SIZE) {
			var niceEnding = "...(" + (message.length() - RequestResponseCycle.MAX_MESSAGE_SIZE) + " characters have been trimmed)..";
			return message.substring(0, RequestResponseCycle.MAX_MESSAGE_SIZE) + niceEnding;
		}
		else {
			// do nothing
			return message;
		}
	}
	
	public void setResponse(ResponseEntity<?> response) {
		this.clientResponseCode = response.getStatusCodeValue();

		this.clientResponseBody  = response.getBody().toString();
		this.clientResponseSize = this.clientResponseBody.length();
		
		if(this.clientResponseCode == 200) {
			// status = OK, message can be shortened
			this.clientRequestBody = RequestResponseCycle.shortenLongMessages(this.clientRequestBody);
			this.clientResponseBody = RequestResponseCycle.shortenLongMessages(this.clientResponseBody);
		}

		this.stopdatetime = LocalDateTime.now();
		this.durationInMilliseconds = Duration.between(startdatetime, stopdatetime).toMillis();
	}

	public String getReportName() {
		String reportName = this.getModus();
		if (reportName == null || reportName.length() < 1) {
			reportName = "Execute";
		} else {
			reportName = reportName.substring(0, 1).toUpperCase() + reportName.substring(1);
			if (this.clientSoapAction != null) {
				int i = this.clientSoapAction.lastIndexOf('/');
				if (i != -1 ) {
					reportName = reportName + " " + this.clientSoapAction.substring(i + 1, this.clientSoapAction.length());
				}
			}
		}
		return reportName;
	}
}