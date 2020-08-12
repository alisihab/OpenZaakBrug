package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import nl.haarlem.translations.zdstozgw.converter.Converter;
import nl.haarlem.translations.zdstozgw.converter.ConverterFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
public class ConverterFactoryTests {

    ConverterFactory converterFactory;

    @Mock
    ConfigService configService;

    @Before
    public void setup() {
        this.converterFactory = new ConverterFactory(configService, null);
    }

    @Test
    public void getConverter_shouldInitiateCorrectConverter() throws IOException {
        //assign
        String soapAction = "http://www.egem.nl/StUF/sector/zkn/0310/voegZaakdocumentToe_Lk01";
        String implementation = "nl.haarlem.translations.zdstozgw.converter.impl.VoegZaakdocumentToeConverter";
        String content = IOUtils.toString(getClass()
                .getClassLoader()
                .getResourceAsStream("zds1.1/VoegZaakDocumentToe"), "UTF-8");

        Translation translation = new Translation()
                .setSoapAction(soapAction)
                .setImplementation(implementation);
        doReturn(translation).when(configService).getDefaultOrApplicationSpecificTranslationBySoapActionAndApplication(any(), any());

        //act
        Converter converter = converterFactory.getConverter(soapAction, content);

        //assert
        Assert.assertTrue(converter != null);
        Assert.assertEquals(implementation, converter.getClass().getCanonicalName());
    }

}
