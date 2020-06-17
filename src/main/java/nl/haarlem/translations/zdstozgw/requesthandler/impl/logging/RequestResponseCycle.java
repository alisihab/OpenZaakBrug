package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;
import org.apache.commons.httpclient.HttpStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class RequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;

    // Wat heeft de client gevraagd en gekregen
    private LocalDateTime timestamp;
    private long durationInMilliseconds;
    private String clientUrl;
    private String clientSoapAction;
    @Lob
    private String clientRequestBody;
    @Lob
    private String clientResponseBodyZGW;
    @Lob
    private String clientResponseBodyZDS;
    private String clientResponseCode;

    // Wanneer we ergens in het proces een fout hebben, dan willen we die bewaren
    @Lob
    private String stackTrace;

    // Welke modus draaiden we en hadden we een fout?
    private String replicationModus;
    private String converterImplementation;
    private String converterTemplate;

}