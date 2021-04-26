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
	@Column(columnDefinition="TEXT")
	private String zdsRequestBody;
	private Integer zdsRequestSize;
	
	private int zdsResponseCode;
	@Column(columnDefinition="TEXT")
	private String zdsResponseBody;
	private Integer zdsResponseSize;

	public ZdsRequestResponseCycle(String zdsUrl, String zdsSoapAction, String zdsRequestBody, String referentienummer) {		
		this.zdsUrl = zdsUrl;
		this.zdsSoapAction = zdsSoapAction;
		this.zdsRequestBody = zdsRequestBody;

		this.referentienummer = referentienummer;
		
		startdatetime = LocalDateTime.now();
		this.zdsRequestSize = this.zdsRequestBody.length();
	}

	public long getDurationInMilliseconds() {
		var milliseconds = Duration.between(startdatetime, LocalDateTime.now()).toMillis();
		return milliseconds;
	}

	public void setResonse(ResponseEntity<?> response) {
		this.zdsResponseBody = response.getBody().toString();
		this.zdsResponseSize = this.zdsResponseBody.length();
		this.zdsResponseCode = response.getStatusCodeValue();

		this.stopdatetime = LocalDateTime.now();
		this.durationInMilliseconds = Duration.between(startdatetime, stopdatetime).toMillis();
	}
}