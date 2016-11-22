package nl.han.asd.project.client.commonclient.connection;

import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test
    public void parseFromWrapper() throws Exception {
        ClientLoginResponse expected = ClientLoginResponse.newBuilder().setStatus(ClientLoginResponse.Status.SUCCES)
                .build();

        Wrapper wrapper = Wrapper.newBuilder().setType(Type.CLIENTLOGINRESPONSE).setData(expected.toByteString())
                .build();

        ClientLoginResponse actual = (ClientLoginResponse) Parser.parseFrom(wrapper);

        assertEquals(expected, actual);
    }

    @Test
    public void parseFromBytes() throws Exception {
        ClientLoginResponse expected = ClientLoginResponse.newBuilder().setStatus(ClientLoginResponse.Status.SUCCES)
                .build();

        ClientLoginResponse actual = Parser.parseFrom(expected.toByteArray(), ClientLoginResponse.class);

        assertEquals(expected, actual);
    }
}
