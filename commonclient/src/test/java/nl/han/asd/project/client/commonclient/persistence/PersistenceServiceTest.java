package nl.han.asd.project.client.commonclient.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hsqldb.jdbc.JDBCResultSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import nl.han.asd.project.client.commonclient.database.IDatabase;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.persistence.IPersistence.DateFormat;
import nl.han.asd.project.client.commonclient.store.Contact;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Message.class, DateFormat.class, ArrayList.class})
public class PersistenceServiceTest {

    static String USERNAME = "username";
    static String PASSWORD = "password";

    @Mock
    IDatabase databaseMock;

    @InjectMocks
    PersistenceService persistenceService;

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullDatabase() throws Exception {
        new PersistenceService(null);
    }

    @Test
    public void testInitNotInitialized() throws Exception {
        persistenceService.init(USERNAME, PASSWORD);

        verify(databaseMock).init(eq(USERNAME), eq(PASSWORD));
    }

    @Test
    public void testInitAlreadyInitialized() throws Exception {
        persistenceService.init(USERNAME, PASSWORD);
        verify(databaseMock).init(eq(USERNAME), eq(PASSWORD));

        persistenceService.init(USERNAME, PASSWORD);
        verifyNoMoreInteractions(databaseMock);
    }

    @Test
    public void testDeleteMessageSQLException() throws Exception {
        int id = 12;

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        String query = "DELETE FROM Message WHERE id = ?";
        when(databaseMock.prepareStatement(eq(query))).thenReturn(preparedStatementMock);

        doThrow(new SQLException()).when(preparedStatementMock).close();

        assertFalse(persistenceService.deleteMessage(id));

        verify(preparedStatementMock).setInt(1, id);
        verify(preparedStatementMock).execute();
    }

    @Test
    public void testDeleteMessageValid() throws Exception {
        int id = 12;

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        String query = "DELETE FROM Message WHERE id = ?";
        when(databaseMock.prepareStatement(eq(query))).thenReturn(preparedStatementMock);

        assertTrue(persistenceService.deleteMessage(id));

        verify(preparedStatementMock).setInt(1, id);
        verify(preparedStatementMock).execute();
        verify(preparedStatementMock).close();
    }

    @Test
    public void testSaveMessageSQLException() throws Exception {
        String senderUsername = "sender username";
        Contact senderMock = mock(Contact.class);
        when(senderMock.getUsername()).thenReturn(senderUsername);

        String receiverUsername = "sender username";
        Contact receiverMock = mock(Contact.class);
        when(receiverMock.getUsername()).thenReturn(receiverUsername);

        String messageId = "message id";
        String messageText = "message text";
        String messageFormattedTimestamp = "message timestamp";
        boolean messageConfirmed = false;
        Message messageMock = mock(Message.class);
        when(messageMock.getMessageId()).thenReturn(messageId);
        when(messageMock.getSender()).thenReturn(senderMock);
        when(messageMock.getReceiver()).thenReturn(receiverMock);
        when(messageMock.getFormattedTimestamp()).thenReturn(messageFormattedTimestamp);
        when(messageMock.getText()).thenReturn(messageText);
        when(messageMock.isConfirmed()).thenReturn(messageConfirmed);

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        String query = "INSERT INTO Message (messageId, sender, receiver, timestamp, message, confirmed) VALUES (?, ?, ?, ?, ?, ?)";
        when(databaseMock.prepareStatement(eq(query))).thenReturn(preparedStatementMock);

        doThrow(new SQLException()).when(preparedStatementMock).close();
        assertFalse(persistenceService.saveMessage(messageMock));

        verify(preparedStatementMock).setString(1, messageId);
        verify(preparedStatementMock).setString(2, senderUsername);
        verify(preparedStatementMock).setString(3, receiverUsername);
        verify(preparedStatementMock).setString(4, messageFormattedTimestamp);
        verify(preparedStatementMock).setString(5, messageText);
        verify(preparedStatementMock).setBoolean(6, messageConfirmed);

        verify(preparedStatementMock).execute();
        verify(preparedStatementMock).close();
    }

    @Test
    public void testSaveMessageValid() throws Exception {
        String senderUsername = "sender username";
        Contact senderMock = mock(Contact.class);
        when(senderMock.getUsername()).thenReturn(senderUsername);

        String receiverUsername = "sender username";
        Contact receiverMock = mock(Contact.class);
        when(receiverMock.getUsername()).thenReturn(receiverUsername);

        String messageId = "message id";
        String messageText = "message text";
        String messageFormattedTimestamp = "message timestamp";
        boolean messageConfirmed = false;
        Message messageMock = mock(Message.class);
        when(messageMock.getMessageId()).thenReturn(messageId);
        when(messageMock.getSender()).thenReturn(senderMock);
        when(messageMock.getReceiver()).thenReturn(receiverMock);
        when(messageMock.getFormattedTimestamp()).thenReturn(messageFormattedTimestamp);
        when(messageMock.getText()).thenReturn(messageText);
        when(messageMock.isConfirmed()).thenReturn(messageConfirmed);

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        String query = "INSERT INTO Message (messageId, sender, receiver, timestamp, message, confirmed) VALUES (?, ?, ?, ?, ?, ?)";
        when(databaseMock.prepareStatement(eq(query))).thenReturn(preparedStatementMock);

        assertTrue(persistenceService.saveMessage(messageMock));

        verify(preparedStatementMock).setString(1, messageId);
        verify(preparedStatementMock).setString(2, senderUsername);
        verify(preparedStatementMock).setString(3, receiverUsername);
        verify(preparedStatementMock).setString(4, messageFormattedTimestamp);
        verify(preparedStatementMock).setString(5, messageText);
        verify(preparedStatementMock).setBoolean(6, messageConfirmed);

        verify(preparedStatementMock).execute();
        verify(preparedStatementMock).close();
    }

    @Test
    public void testConfirmMessageSQLException() throws Exception {
        String messageId = "message id";

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        String query = "UPDATE Message SET confirmed = ? WHERE messageId = ?";
        when(databaseMock.prepareStatement(eq(query))).thenReturn(preparedStatementMock);

        doThrow(new SQLException()).when(preparedStatementMock).close();
        persistenceService.confirmMessage(messageId);

        verify(preparedStatementMock).setBoolean(1, true);
        verify(preparedStatementMock).setString(2, messageId);
        verify(preparedStatementMock).execute();
        verify(preparedStatementMock).close();
    }

    @Test
    public void testConfirmMessageValid() throws Exception {
        String messageId = "message id";

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        String query = "UPDATE Message SET confirmed = ? WHERE messageId = ?";
        when(databaseMock.prepareStatement(eq(query))).thenReturn(preparedStatementMock);

        persistenceService.confirmMessage(messageId);

        verify(preparedStatementMock).setBoolean(1, true);
        verify(preparedStatementMock).setString(2, messageId);
        verify(preparedStatementMock).execute();
        verify(preparedStatementMock).close();
    }

    @Test
    public void testGetAllMessagesNullResultSet() throws Exception {
        when(databaseMock.select(eq("SELECT * FROM Message"))).thenReturn(null);
        when(databaseMock.select(eq("SELECT * FROM Message"))).thenReturn(null);

        assertEquals(Collections.emptyList(), persistenceService.getAllMessages());
    }

    @Test
    public void testGetAllMessagesSQLException() throws Exception {
        ResultSet resultSetMock = mock(ResultSet.class);

        when(databaseMock.select(eq("SELECT * FROM Message"))).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenThrow(new SQLException());

        assertEquals(Collections.emptyList(), persistenceService.getAllMessages());
    }

    @Test
    public void testGetAllMessagesValid() throws Exception {
        List<Message> messageList = new ArrayList<>();

        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.select(eq("SELECT * FROM Message"))).thenReturn(resultSetMock);

        when(resultSetMock.next()).thenReturn(true, false);

        Message messgeMock = mock(Message.class);
        PowerMockito.mockStatic(Message.class);
        PowerMockito.when(Message.fromDatabase(eq(resultSetMock))).thenReturn(messgeMock);

        messageList.add(messgeMock);
        assertEquals(messageList, persistenceService.getAllMessages());
    }

    @Test
    public void testGetAllMessagesPerContactSQLException() throws Exception {
        when(databaseMock.select(eq("SELECT * FROM Message ORDER BY timestamp ASC"))).thenThrow(new SQLException());
        assertEquals(Collections.emptyMap(), persistenceService.getAllMessagesPerContact());
    }

    @Test
    public void testGetAllMessagesPerContactValid() throws Exception {
        String senderUsername = "sender username";
        Contact senderMock = mock(Contact.class);
        when(senderMock.getUsername()).thenReturn(senderUsername);

        String receiverUsername = "sender username";
        Contact receiverMock = mock(Contact.class);
        when(receiverMock.getUsername()).thenReturn(receiverUsername);

        String messageId = "message id";
        String messageText = "message text";
        String messageFormattedTimestamp = "message timestamp";
        boolean messageConfirmed = false;
        Message messageMock = mock(Message.class);
        when(messageMock.getMessageId()).thenReturn(messageId);
        when(messageMock.getSender()).thenReturn(senderMock);
        when(messageMock.getReceiver()).thenReturn(receiverMock);
        when(messageMock.getFormattedTimestamp()).thenReturn(messageFormattedTimestamp);
        when(messageMock.getText()).thenReturn(messageText);
        when(messageMock.isConfirmed()).thenReturn(messageConfirmed);

        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.select(eq("SELECT * FROM Message ORDER BY timestamp ASC"))).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true, false);

        PowerMockito.mockStatic(Message.class);
        PowerMockito.when(Message.fromDatabase(resultSetMock)).thenReturn(messageMock);

        Map<Contact, List<Message>> contactMessageMap = new HashMap<>();
        contactMessageMap.put(senderMock, new ArrayList<Message>());
        contactMessageMap.get(senderMock).add(messageMock);

        contactMessageMap.put(receiverMock, new ArrayList<Message>());
        contactMessageMap.get(receiverMock).add(messageMock);

        assertEquals(contactMessageMap, persistenceService.getAllMessagesPerContact());
    }

    @Test
    public void testAddContactSQLException() throws Exception {
        when(databaseMock.prepareStatement(eq("INSERT INTO Contact (username) VALUES (?)")))
                .thenThrow(new SQLException());
        assertFalse(persistenceService.addContact("username"));
    }

    @Test
    public void testAddContactValid() throws Exception {
        String username = "username";

        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.prepareStatement(eq("INSERT INTO Contact (username) VALUES (?)")))
                .thenReturn(preparedStatementMock);

        assertTrue(persistenceService.addContact(username));

        verify(preparedStatementMock).setString(1, username);
        verify(preparedStatementMock).execute();
        verify(preparedStatementMock).close();
    }

    @Test
    public void testDeleteContactSQLException() throws Exception {
        when(databaseMock.prepareStatement(eq("DELETE FROM Contact WHERE username = ?"))).thenThrow(new SQLException());
        assertFalse(persistenceService.deleteContact("username"));
    }

}
