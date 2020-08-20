package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.StufUtils;

import javax.xml.bind.annotation.*;
import java.util.List;

import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.STUF;
import static nl.haarlem.translations.zdstozgw.translation.zds.model.namespace.Namespace.ZKN;

@Data
@XmlRootElement(namespace = ZKN, name = "genereerZaakIdentificatie_Di02")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZdsGenereerZaakIdentificatieDi02 extends ZdsDocument{

    public ZdsGenereerZaakIdentificatieDi02() {
    }
}
