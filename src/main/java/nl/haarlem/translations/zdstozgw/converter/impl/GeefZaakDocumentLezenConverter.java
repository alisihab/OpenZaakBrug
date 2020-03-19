package nl.haarlem.translations.zdstozgw.converter.impl;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLa01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.EdcLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLk01_v2;
import nl.haarlem.translations.zdstozgw.translation.zds.model.ZakLv01;
import nl.haarlem.translations.zdstozgw.translation.zds.services.ZaakService;
import nl.haarlem.translations.zdstozgw.utils.XmlUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;

public class GeefZaakDocumentLezenConverter implements Converter {
    protected String templatePath;

    public GeefZaakDocumentLezenConverter(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public String Convert(ZaakService zaakService, Object object) {
        String result = "";
        try {
            EdcLa01 edcLa01 = zaakService.getZaakDoumentLezen((EdcLv01) object);
            //var zaak = zaakService.creeerZaak((ZakLk01_v2) object);


            return XmlUtils.getSOAPMessageFromObject(edcLa01);

        } catch (Exception ex) {
            ex.printStackTrace();
            var f03 = new nl.haarlem.translations.zdstozgw.translation.zds.model.F03();
            f03.setFaultString("Object was not saved");
            f03.setCode("StUF046");
            f03.setOmschrijving("Object niet opgeslagen");
            f03.setDetails(ex.getMessage());
            return f03.getSoapMessageAsString();
        }
    }

}

