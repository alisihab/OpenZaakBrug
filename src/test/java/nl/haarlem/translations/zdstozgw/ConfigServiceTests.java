package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Replication;
import nl.haarlem.translations.zdstozgw.config.model.ResponseType;
import nl.haarlem.translations.zdstozgw.config.model.Translation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigServiceTests {

    @Autowired
    ConfigService configService;

    @Test
    public void getTranslationBySoapActionAndApplicatie(){
        //assign
        String soapAction = "http://www.egem.nl/StUF/sector/zkn/0310/genereerDocumentIdentificatie_Di02";
        String applicatie = "GWS4all";

        //act
        Translation result = configService.getTranslationBySoapActionAndApplicatie(soapAction,applicatie);

        //assert
        Assert.assertTrue(result != null);

    }

    @Test
    public void getReplication_shouldReturnCorrectObject(){
        //assign
        Replication expectedResult = new Replication()
                .setEnableZDS(true)
                .setEnableZGW(true)
                .setResponseType(ResponseType.ZDS);

        //act
        Replication result = configService.getConfiguratie().getReplication();

        //assert
        Assert.assertEquals(expectedResult, result);

    }

}
