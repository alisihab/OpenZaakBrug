package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;

@Entity
@Data
public class ZgwRequestResponseCycle {
	@Id
	@GeneratedValue
	private long id;
	private String referentienummer;

	private LocalDateTime timestamp;
	private Long durationInMilliseconds;	
	
	private String zgwMethod;
	@Column(columnDefinition="TEXT")
	private String zgwUrl;
	@Column(columnDefinition="TEXT")
	private String zgwRequestBody;
	private Long clientRequestSize;

	
	private int zgwResponseCode;	
//	@Lob
	@Column(columnDefinition="TEXT")
	private String zgwResponseBody;	
	private Long clientResponseSize;
	
}