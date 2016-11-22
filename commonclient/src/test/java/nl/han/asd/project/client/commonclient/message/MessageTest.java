package nl.han.asd.project.client.commonclient.message;

import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Contact.class, IPersistence.class, Message.class, HanRoutingProtocol.Message.class})
public class MessageTest {

    private Date date = new Date();
    private String testData = "testData";
    private Message message;
    private Message message2;
    private Message message3;
    private Message message4;
    private String messageId = "messageId12345";

    private String username;
    private String username2;
    private Contact sender;
    private Contact contactReceiver;
    private int databaseId;

    @Before
    public void setUp() {
        username = "Username";
        username2 = "Username2";
        sender = PowerMockito.mock(Contact.class);
        contactReceiver = PowerMockito.mock(Contact.class);
        databaseId = 1;
        message = new Message(sender, contactReceiver, date, testData);
        message2 = new Message(sender, contactReceiver, date, testData, messageId);
        message4 = new Message(sender, contactReceiver, date, testData, messageId);
        message3 = new Message(databaseId, sender, contactReceiver, date, testData, messageId);
    }

    @Test
    public void testGetSender() throws Exception {
        assertEquals(sender, message.getSender());
    }

    @Test
    public void testGetText() throws Exception {
        assertEquals(testData, message.getText());
    }

    @Test
    public void toStringCreatesRightString() throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("Message[messageId = " + messageId + ", sender=").append(message.getSender().getUsername()).append(", receiver=").append(message.getReceiver().getUsername()).append(", timestamp=").append(message.getMessageTimestamp()).append(", text=").append(message.getText()).append(", confirmed=").append(message.isConfirmed()).append("]");
        assertEquals(sb.toString(), message2.toString());
    }

    @Test
    public void equalsWithDifferentObjectButSameMessageContentReturnsTrue(){
        assertTrue(message4.equals(message2));
    }

    @Test
    public void equalsThrowsFalseWhenComparedWithNull() throws Exception {
        assertFalse(message.equals(null));
    }

    @Test
    public void getMessageTimeStampReturnsRightTimeStamp() throws Exception {
        assertEquals(message.getMessageTimestamp(), date);
    }

    @Test
    public void getMessageId() {
        Assert.assertEquals(message2.getMessageId(), messageId);
    }

    @Test
    public void getDatabaseId() {
        Assert.assertEquals(message3.getDatabaseId(), databaseId);
    }

    @Test
    public void getMessageFromDatabase() throws SQLException, ParseException {
        PowerMockito.mockStatic(Contact.class);
        PowerMockito.mock(IPersistence.class);
        ResultSet resultset = Mockito.mock(ResultSet.class);
        Timestamp timestamp = new Timestamp(date.getTime());
        PowerMockito.when(Contact.fromDatabase(username)).thenReturn(sender);
        PowerMockito.when(Contact.fromDatabase(username2)).thenReturn(contactReceiver);

        Mockito.when(contactReceiver.getUsername()).thenReturn(username);
        Mockito.when(resultset.getObject(1)).thenReturn(databaseId);
        Mockito.when(resultset.getObject(2)).thenReturn(messageId);
        Mockito.when(resultset.getObject(3)).thenReturn(username);
        Mockito.when(resultset.getObject(4)).thenReturn(username2);
        Mockito.when(resultset.getTimestamp(5)).thenReturn(timestamp);
        Mockito.when(resultset.getObject(6)).thenReturn(message3.getText());
        Mockito.when(resultset.getObject(7)).thenReturn(false);

        Assert.assertEquals(message3.toString(), Message.fromDatabase(resultset).toString());
    }

    @Test
    public void getMessageFromDatabaseResultsInSQLException() throws SQLException, ParseException {
        ResultSet resultset = Mockito.mock(ResultSet.class);
        Mockito.when(resultset.getObject(1)).thenThrow(new SQLException());

        Assert.assertNull( Message.fromDatabase(resultset));
        Mockito.verify(resultset, Mockito.never()).getObject(2);
    }

    @Test
    public void getMessageFromProtocolMessage() throws SQLException, ParseException {
        HanRoutingProtocol.Message protocolMessage = PowerMockito.mock(HanRoutingProtocol.Message.class);
        PowerMockito.mockStatic(Contact.class);
        PowerMockito.when(Contact.fromDatabase(username)).thenReturn(sender);
        PowerMockito.when(protocolMessage.getText()).thenReturn(testData);
        PowerMockito.when(protocolMessage.getSender()).thenReturn(username);
        PowerMockito.when(protocolMessage.getId()).thenReturn(messageId);
        PowerMockito.when(protocolMessage.getTimeSent()).thenReturn(date.getTime());

        Assert.assertEquals(message2.toString(), Message.fromProtocolMessage(protocolMessage, contactReceiver).toString());
    }
}
