package nl.haarlem.translations.zdstozgw.translation.zds.model;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.utils.xpath.XpathDocument;
import org.w3c.dom.Document;

@Data
public class ZakLk01 {

    private final XpathDocument xpathDocument;
    private Document document;

    public ZakLk01(Document document) {
        this.document = document;
        this.xpathDocument  = new XpathDocument(document);
    }

}
