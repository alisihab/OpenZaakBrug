package nl.haarlem.translations.zdstozgw.requesthandler.impl.logging;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ZgwRequestResponseCycle {
    @Id
    @GeneratedValue
    private long id;
    private String referentienummer;    

    private String zgwMethod;    
    @Lob
    private String zgwUrl;
    @Lob
    private String zgwRequestBody;
    private int zgwResponseCode;
    @Lob
    private String zgwResponseBody;
}