package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author DDulos
 * @version 1.0
 * @since 27-May-16
 */
public class ContactTest {

    private static final String TEST_CONTACT_PRIMARY_USERNAME = "testUsername";
    private static final String TEST_CONTACT_SECONDARY_USERNAME = "testUsernameSecondary";
    private static final byte[] TEST_CONTACT_PUBLICKEY = "test1234567890".getBytes();
    private static final byte[] TEST_CONTACT_NEW_PUBLICKEY = "test1234567NEW".getBytes();
    private Contact testContact;
    private Node[] testConnectedNodesArray;

    @Before
    public void initialize() throws Exception {
        testContact = new Contact(TEST_CONTACT_PRIMARY_USERNAME, TEST_CONTACT_PUBLICKEY);
        testContact.setOnline(true);

        testConnectedNodesArray = new Node[3];
        for (int i = 0; i < testConnectedNodesArray.length; i++) {
            testConnectedNodesArray[i] = new Node("Node_ID" + (i + 1), "127.0.0.1", 1234, ("12345678" + i).getBytes());
        }
    }

    @Test
    public void testCreatedContactHasCorrectUsername() throws Exception {
        assertEquals(TEST_CONTACT_PRIMARY_USERNAME, testContact.getUsername());
    }

    @Test
    public void testCreatedContactHasCorrectPublicKey() throws Exception {
        assertEquals(TEST_CONTACT_PUBLICKEY, testContact.getPublicKey());
    }

    @Test
    public void testCreatedContactHasCorrectOnlineStatus() throws Exception {
        assertEquals(true, testContact.isOnline());
    }

    @Test
    public void testSetConnectedNodes() throws Exception {
        testContact.setConnectedNodes(testConnectedNodesArray);

        assertEquals(testConnectedNodesArray[0].getId(), testContact.getConnectedNodes()[0].getId());
    }

    @Test
    public void testSetOnlineStatus() throws Exception {
        testContact.setOnline(false);

        assertEquals(false, testContact.isOnline());
    }

    @Test
    public void testSetPublicKey() throws Exception {
        testContact.setPublicKey(TEST_CONTACT_NEW_PUBLICKEY);

        assertEquals(TEST_CONTACT_NEW_PUBLICKEY, testContact.getPublicKey());
    }

    @Test(expected = NoConnectedNodesException.class)
    public void testThrowNoConnectedNodesException() throws NoConnectedNodesException {
        Node[] connectedNodes = testContact.getConnectedNodes();
    }

    @Test
    public void testFromDatabaseSuccess() {
        assertEquals(testContact, Contact.fromDatabase(TEST_CONTACT_PRIMARY_USERNAME));
    }

    @Test
    public void testEqualsIsEqualWithSameObjectTypeSameUsername() {
        final Contact otherContact = new Contact(TEST_CONTACT_PRIMARY_USERNAME);
        assertTrue(testContact.equals(otherContact));
    }

    @Test
    public void testEqualsIsNotEqualWithSameObjectTypeDifferentUsername() {
        final Contact otherContact = new Contact(TEST_CONTACT_SECONDARY_USERNAME);
        Assert.assertFalse(testContact.equals(otherContact));
    }

    @Test
    public void testEqualsIsNotEqualWithDifferentObjectType() {
        String s = "Test";
        Assert.assertFalse(testContact.equals(s));
    }

    @Test
    public void testToStringIsEqualWithSameUsername() {
        Assert.assertEquals(TEST_CONTACT_PRIMARY_USERNAME, testContact.toString());
    }

    @Test
    public void testToStringIsNotEqualWithDifferentUsername() {
        Assert.assertNotEquals(TEST_CONTACT_SECONDARY_USERNAME, testContact.toString());
    }

    @Test
    public void testHashCodeIsEqualWithSameUsername() {
        final Contact otherContact = new Contact(TEST_CONTACT_PRIMARY_USERNAME);
        Assert.assertEquals(otherContact.hashCode(), testContact.hashCode());
    }

    @Test
    public void testHashCodeIsNotEqualWithDifferentUsername() {
        final Contact otherContact = new Contact(TEST_CONTACT_SECONDARY_USERNAME);
        Assert.assertNotEquals(otherContact.hashCode(), testContact.hashCode());
    }
}