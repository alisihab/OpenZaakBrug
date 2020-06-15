package nl.haarlem.translations.zdstozgw;

import nl.haarlem.translations.zdstozgw.config.ConfigService;
import nl.haarlem.translations.zdstozgw.config.model.Configuratie;
import nl.haarlem.translations.zdstozgw.config.model.Replication;
import nl.haarlem.translations.zdstozgw.config.model.ResponseType;
import nl.haarlem.translations.zdstozgw.requesthandler.impl.ReplicationRequestHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.doReturn;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReplicationRequestHandlerTests {

    ReplicationRequestHandler replicationRequestHandler;

    @Mock
    ConfigService configService;

    @Before
    public void setup(){
        this.replicationRequestHandler = new ReplicationRequestHandler(null, configService);
    }


    @Test(expected = RuntimeException.class)
    public void execute_whenModusDisabled_shouldThrowError(){
        //assign
        Configuratie configuratie = new Configuratie()
                .setReplication(new Replication()
                        .setEnableZDS(false)
                        .setEnableZGW(false));

        doReturn(configuratie).when(configService).getConfiguratie();

        //act
        this.replicationRequestHandler.execute("test");
    }

    @Test(expected = RuntimeException.class)
    public void execute_whenResponseTypeZGWDoesNotMatchModus_shouldThrowError(){
        //assign
        Configuratie configuratie = new Configuratie()
                .setReplication(new Replication()
                        .setEnableZDS(true)
                        .setEnableZGW(false)
                .setResponseType(ResponseType.ZGW));

        doReturn(configuratie).when(configService).getConfiguratie();

        //act
        this.replicationRequestHandler.execute("test");
    }

    @Test(expected = RuntimeException.class)
    public void execute_whenResponseTypeZDSDoesNotMatchModus_shouldThrowError(){
        //assign
        Configuratie configuratie = new Configuratie()
                .setReplication(new Replication()
                        .setEnableZDS(false)
                        .setEnableZGW(true)
                        .setResponseType(ResponseType.ZDS));

        doReturn(configuratie).when(configService).getConfiguratie();

        //act
        this.replicationRequestHandler.execute("test");
    }

    @Test(expected = RuntimeException.class)
    public void execute_whenResponseTypeIsNull_shouldThrowError(){
        //assign
        Configuratie configuratie = new Configuratie()
                .setReplication(new Replication()
                        .setEnableZDS(true)
                        .setEnableZGW(true)
                        .setResponseType(null));

        doReturn(configuratie).when(configService).getConfiguratie();

        //act
        this.replicationRequestHandler.execute("test");
    }

}
