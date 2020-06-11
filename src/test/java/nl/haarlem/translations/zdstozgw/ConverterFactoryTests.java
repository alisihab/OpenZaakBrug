package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConverterFactoryTests {

    @Autowired
    ConverterFactory converterFactory;

    @Test
    public void getConverter_shouldInitiateCorrectConverter() throws IOException {
        //assign
        String soapAction = "http://www.egem.nl/StUF/sector/zkn/0310/voegZaakdocumentToe_Lk01";
        String implementation = "nl.haarlem.translations.zdstozgw.converter.impl.VoegZaakdocumentToeConverter";
        String content = IOUtils.toString(getClass()
                .getClassLoader()
                .getResourceAsStream("zds1.1/VoegZaakDocumentToe"), "UTF-8");

        //act
        Converter converter = converterFactory.getConverter(soapAction, content);
        System.out.println(converter.getClass().toString());

        //assert
        Assert.assertTrue(converter != null);
        Assert.assertEquals(implementation, converter.getClass().getCanonicalName());
    }

}
