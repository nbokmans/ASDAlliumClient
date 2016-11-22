package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.IGetVertices;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Contact.class, ContactStore.class})
public class ContactStoreTest {

    @Mock
    IPersistence persistence;

    @Mock
    IGetVertices getVertices;

    private ContactStore contactStore;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String TEST_CONTACT1 = "testContact1";
    private static final String TEST_CONTACT2 = "testContact2";
    private static final String TEST_CONTACT3 = "testContact3";
    private static final String TEST_CONTACT4 = "testContact4";
    private Map<String, Contact> mockedSingleContactList;
    private Map<String, Contact> mockedMultipleContactList;
    private Map<String, Contact> mockDeletedContactList;
    private Contact contact1;
    private Contact contact2;
    private Contact contact3;
    private Contact contact4;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockedSingleContactList = new HashMap<>();
        mockedMultipleContactList = new HashMap<>();
        mockDeletedContactList = new HashMap<>();

        when(persistence.getContacts()).thenReturn(new HashMap<String, Contact>());
        contactStore = new ContactStore(persistence, getVertices);

        initializeMockContacts();
    }

    private void initializeMockContacts() {
        contact1 = PowerMockito.mock(Contact.class);
        contact2 = PowerMockito.mock(Contact.class);
        contact3 = PowerMockito.mock(Contact.class);
        contact4 = PowerMockito.mock(Contact.class);

        mockedSingleContactList.put(TEST_CONTACT1, contact1);

        mockedMultipleContactList.put(TEST_CONTACT1, contact1);
        mockedMultipleContactList.put(TEST_CONTACT2, contact2);
        mockedMultipleContactList.put(TEST_CONTACT3, contact3);
        mockedMultipleContactList.put(TEST_CONTACT4, contact4);

        mockDeletedContactList.put(TEST_CONTACT1, contact1);
        mockDeletedContactList.put(TEST_CONTACT3, contact2);
        mockDeletedContactList.put(TEST_CONTACT4, contact4);
    }

    @Test
    public void testInitializeContactStore() throws SQLException {
        contactStore.init(USERNAME, PASSWORD);
        verify(persistence).init(USERNAME, PASSWORD);
    }

    @Test
    public void testAddSingleContactToList() throws Exception {
        when(persistence.getContacts()).thenReturn(mockedSingleContactList);
        when(persistence.addContact(TEST_CONTACT1)).thenReturn(true);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT1).thenReturn(contact1);
        when(contact1.getUsername()).thenReturn(TEST_CONTACT1);

        contactStore.addContact(TEST_CONTACT1);

        verify(persistence, Mockito.times(1)).addContact(TEST_CONTACT1);
        assertEquals(1, contactStore.getAllContacts().size());
    }

    @Test
    public void testAddExistingSingleContactToListDoNothing() throws Exception {
        when(persistence.addContact(TEST_CONTACT1)).thenReturn(true);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT1).thenReturn(contact1);

        contactStore.addContact(TEST_CONTACT1);
        contactStore.addContact(TEST_CONTACT1);

        verify(persistence, Mockito.times(1)).addContact(TEST_CONTACT1);
    }

    @Test
    public void testAddMultipleContactsToList() throws Exception {
        addTestContacts();
        when(persistence.getContacts()).thenReturn(mockedMultipleContactList);

        verify(persistence, Mockito.times(4)).addContact(Mockito.any(String.class));

        assertEquals(4, contactStore.getAllContacts().size());
    }

    @Test
    public void testFindContactInListWithUsername() throws Exception {
        addTestContacts();
        when(persistence.getContacts()).thenReturn(mockedMultipleContactList);

        Contact selectedContact = contactStore.findContact(TEST_CONTACT3);

        assertEquals(contact3, selectedContact);
    }

    @Test
    public void testFindContactGivesNullWhenNotExistsInListReturnNull() throws Exception {
        addTestContacts();
        when(persistence.getContacts()).thenReturn(mockedMultipleContactList);

        Contact selectedContact = contactStore.findContact("testContact5");

        assertEquals(null, selectedContact);
    }

    @Test
    public void testFindContactByNullUsernameReturnsNull() throws Exception {
        when(persistence.getContacts()).thenReturn(mockedMultipleContactList);

        Contact selectedContact = contactStore.findContact(null);

        assertEquals(null, selectedContact);
    }

    @Test
    public void testDeleteSingleContactFromList() throws Exception {
        when(persistence.deleteContact(TEST_CONTACT2)).thenReturn(true);
        addTestContacts();

        when(persistence.getContacts()).thenReturn(mockedMultipleContactList);

        assertNotNull(contactStore.findContact(TEST_CONTACT2));
        assertEquals(4, contactStore.getAllContacts().size());

        contactStore.removeContact(TEST_CONTACT2);
        when(persistence.getContacts()).thenReturn(mockDeletedContactList);

        assertNull(contactStore.findContact(TEST_CONTACT2));
        assertEquals(3, contactStore.getAllContacts().size());
    }

    @Test
    public void testDeleteNonExistingContactFromList() throws Exception {
        when(persistence.deleteContact("TestContact5")).thenReturn(true);
        addTestContacts();

        when(persistence.getContacts()).thenReturn(mockedMultipleContactList);
        contactStore.removeContact(TEST_CONTACT2);

        assertNotNull(contactStore.findContact(TEST_CONTACT2));
        assertEquals(4, contactStore.getAllContacts().size());
    }

    @Test
    public void testDeleteContactByNullUsername() throws Exception {
        contactStore.removeContact(null);
        verify(persistence, Mockito.never()).deleteContact(Mockito.any(String.class));
    }

    @Test
    public void setAndGetCurrentUser() {
        CurrentUser user = Mockito.mock(CurrentUser.class);
        contactStore.setCurrentUser(user);
        Assert.assertEquals(contactStore.getCurrentUser(), user);
    }

    @Test
    public void getCurrentUserWhenNoCurrentUserReturnNull() {
        Assert.assertNull(contactStore.getCurrentUser());
    }

    @Test
    @PrepareForTest({Contact.class, ContactStore.class, Node.class})
    public void updatePublicKeyUser() throws Exception {
        addTestContacts();
        Node node1 = PowerMockito.mock(Node.class);
        Node node2 = PowerMockito.mock(Node.class);
        Node[] nodesMock = {node1, node2};
        byte[] publicKey = "newKey".getBytes();
        boolean isOnline = true;
        Map<String, Node> verticesMock = new HashMap<>();
        verticesMock.put("node1", node1);
        verticesMock.put("node2", node2);
        List<String> nodeIdsMock = new ArrayList<>();
        nodeIdsMock.add("node1");
        nodeIdsMock.add("node2");

        when(getVertices.getVertices()).thenReturn(verticesMock);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT1, publicKey, isOnline).thenReturn(contact1);

        contactStore.updateUserInformation(TEST_CONTACT1, publicKey, isOnline, nodeIdsMock);
        verify(contact1).setConnectedNodes(nodesMock);
    }

    @Test
    @PrepareForTest({Contact.class, ContactStore.class, Node.class})
    public void updateNonExistingUserDoNothing() throws Exception {
        List<String> nodeIdsMock = new ArrayList<>();
        byte[] publicKey = "newKey".getBytes();
        boolean isOnline = true;
        contactStore.updateUserInformation("", publicKey, isOnline, nodeIdsMock);
    }

    @Test
    public void closeContactStore() throws Exception {
        CurrentUser user = Mockito.mock(CurrentUser.class);
        contactStore.setCurrentUser(user);

        contactStore.close();

        Assert.assertNull(contactStore.getCurrentUser());
        verify(persistence).close();
    }

    private void addTestContacts() throws Exception {
        when(persistence.addContact(Mockito.any(String.class))).thenReturn(true);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT1).thenReturn(contact1);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT2).thenReturn(contact2);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT3).thenReturn(contact3);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CONTACT4).thenReturn(contact4);
        when(contact1.getUsername()).thenReturn(TEST_CONTACT1);
        when(contact2.getUsername()).thenReturn(TEST_CONTACT2);
        when(contact3.getUsername()).thenReturn(TEST_CONTACT3);
        when(contact4.getUsername()).thenReturn(TEST_CONTACT4);

        contactStore.addContact(TEST_CONTACT1);
        contactStore.addContact(TEST_CONTACT2);
        contactStore.addContact(TEST_CONTACT3);
        contactStore.addContact(TEST_CONTACT4);
    }
}
