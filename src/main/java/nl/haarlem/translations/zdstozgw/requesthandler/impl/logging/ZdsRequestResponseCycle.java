package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ZdsRequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;
    private String referentienummer;    

    private String zdsMethod;    
    private String zdsUrl;
    private String zdsSoapAction;
    @Lob
    private String zdsRequestBody;
    private int zdsResponseCode;
    @Lob
    private String zdsResponseBody;    
}