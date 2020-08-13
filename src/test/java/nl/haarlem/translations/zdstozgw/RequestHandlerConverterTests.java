package nl.haarlem.translations.zdstozgw;


import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandler;
import nl.haarlem.translations.zdstozgw.requesthandler.RequestHandlerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
public class RequestHandlerConverterTests {
    RequestHandlerFactory requestHandlerFactory;

    @Mock
    ConfigService configService;

    @Before
    public void setup() {
        this.requestHandlerFactory = new RequestHandlerFactory(configService);
    }

    @Test
    public void getRequestHandler_shouldReturnCorrectRequestHandler() {
        //assign
        String expectedClass = "nl.haarlem.translations.zdstozgw.requesthandler.impl.BasicRequestHandler";
        Configuratie configuratie = new Configuratie()
                .setRequestHandlerImplementation(expectedClass);
        doReturn(configuratie).when(configService).getConfiguratie();

        //act
        RequestHandler requestHandler = requestHandlerFactory.getRequestHandler(null);

        //assert
        Assert.assertEquals(expectedClass, requestHandler.getClass().getCanonicalName());
    }
}
