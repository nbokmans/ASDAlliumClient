package nl.han.asd.project.client.commonclient.store;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import nl.han.asd.project.client.commonclient.master.IGetClientGroup;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Contact.class, System.class, ContactManager.class,
HanRoutingProtocol.ClientRequest.Builder.class, HanRoutingProtocol.ClientResponse.class,
HanRoutingProtocol.Client.class})
public class ContactManagerTest {

    @Mock
    IGetClientGroup getClientGroup;

    @Mock
    IContactStore contactStore;

    private ContactManager contactManager;

    @Before
    public void setUp(){
        contactManager = new ContactManager(getClientGroup, contactStore);
    }

    @Test
    public void testUpdateAllContactInformation() throws Exception {
        PowerMockito.mockStatic(System.class);
        List<HanRoutingProtocol.Client> clients = createAndMockUsers(2);

        HanRoutingProtocol.ClientRequest.Builder builder =
                PowerMockito.mock(HanRoutingProtocol.ClientRequest.Builder.class);
        PowerMockito.whenNew(HanRoutingProtocol.ClientRequest.Builder.class).withAnyArguments().thenReturn(builder);
        HanRoutingProtocol.ClientResponse response = PowerMockito.mock(HanRoutingProtocol.ClientResponse.class);

        PowerMockito.when(System.currentTimeMillis()).thenReturn(1000000L);
        Mockito.when(getClientGroup.getClientGroup(Mockito.any(HanRoutingProtocol.ClientRequest.class))).
                thenReturn(response);
        Mockito.when(response.getClientsList()).thenReturn(clients);

        contactManager.updateAllContactInformation();

        Mockito.verify(contactStore, Mockito.times(2)).updateUserInformation(Mockito.any(String.class), Mockito.any(byte[].class),
            Mockito.any(Boolean.class), Mockito.any(List.class));
    }

    @Test
    public void testUpdateUserInformationTooSoonItIsNotUpdated(){
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.currentTimeMillis()).thenReturn(100L);

        contactManager.updateAllContactInformation();

        Mockito.verify(contactStore, Mockito.times(0)).updateUserInformation(Mockito.any(String.class), Mockito.any(byte[].class),
                Mockito.any(Boolean.class), Mockito.any(List.class));
    }

    @Test
    public void testUpdateAllContactInformationReturnsException() throws Exception {
        PowerMockito.mockStatic(System.class);

        HanRoutingProtocol.ClientRequest.Builder builder =
                PowerMockito.mock(HanRoutingProtocol.ClientRequest.Builder.class);
        PowerMockito.whenNew(HanRoutingProtocol.ClientRequest.Builder.class).withAnyArguments().thenReturn(builder);

        PowerMockito.when(System.currentTimeMillis()).thenReturn(1000000L);
        Mockito.when(getClientGroup.getClientGroup(Mockito.any(HanRoutingProtocol.ClientRequest.class))).
                thenThrow(new IOException());

        contactManager.updateAllContactInformation();

        Mockito.verify(contactStore, Mockito.times(0)).updateUserInformation(Mockito.any(String.class), Mockito.any(byte[].class),
                Mockito.any(Boolean.class), Mockito.any(List.class));
    }

    private List<HanRoutingProtocol.Client> createAndMockUsers(int nrUsers){
        List<HanRoutingProtocol.Client> clients = new ArrayList<>();

        for(int i = 0; i < nrUsers; i ++){
            HanRoutingProtocol.Client client = PowerMockito.mock(HanRoutingProtocol.Client.class);

            clients.add(client);

            Mockito.when(client.getConnectedNodesList()).thenReturn(Mockito.mock(ProtocolStringList.class));
            Mockito.when(client.getUsername()).thenReturn("Username");
            Mockito.when(client.getPublicKey()).thenReturn(Mockito.mock(ByteString.class));
        }
        return clients;
    }
}