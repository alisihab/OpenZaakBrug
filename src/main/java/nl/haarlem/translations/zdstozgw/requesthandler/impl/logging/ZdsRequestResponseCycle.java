package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.http.ResponseEntity;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestResponseCycle;

@Data
@Entity
@Table(indexes = @Index(columnList = "referentienummer"))
public class ZdsRequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;
	private String referentienummer;

	private LocalDateTime startdatetime;
	private LocalDateTime stopdatetime;
	private Long durationInMilliseconds;	
	
	private String zdsUrl;
	private String zdsSoapAction;
	
	@Column(columnDefinition="TEXT", name = "zds_request_body")
	private String zdsShortenedRequestBody;
	private Integer zdsRequestSize;
	
	private int zdsResponseCode;

	@Column(columnDefinition="TEXT", name = "zds_response_body")
	private String zdsShortenedResponseBody;
	private Integer zdsResponseSize;
	
	public ZdsRequestResponseCycle() {
		startdatetime = LocalDateTime.now();
	};

	public ZdsRequestResponseCycle(String zdsUrl, String zdsSoapAction, String zdsRequestBody, String referentienummer) {		
		this.zdsUrl = zdsUrl;
		this.zdsSoapAction = zdsSoapAction;

		this.zdsRequestSize = zdsRequestBody.length();
		this.zdsShortenedRequestBody = RequestResponseCycle.shortenLongMessages(zdsRequestBody);		
		
		this.referentienummer = referentienummer;		
		startdatetime = LocalDateTime.now();
	}

	public long getDurationInMilliseconds() {
		var milliseconds = Duration.between(startdatetime, LocalDateTime.now()).toMillis();
		return milliseconds;
	}

	public void setResponse(ResponseEntity<?> response) {
		this.zdsResponseCode = response.getStatusCodeValue();

		var message = response.getBody().toString();
		this.zdsResponseSize = message.length();
		this.zdsShortenedResponseBody = RequestResponseCycle.shortenLongMessages(message);	
				
		this.stopdatetime = LocalDateTime.now();
		this.durationInMilliseconds = Duration.between(startdatetime, stopdatetime).toMillis();
	}
}