package integration.nl.han.asd.project.client.commonclient.database;

import nl.han.asd.project.client.commonclient.database.HyperSQLDatabase;
import nl.han.asd.project.client.commonclient.database.IDatabase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

public class HyperSQLDatabaseIT {

    private IDatabase database;

    @Before
    public void setupTestSuite() throws SQLException, NoSuchAlgorithmException {
        database = new HyperSQLDatabase();
        database.init("test", "test123");
        database.resetDatabase();
    }

    @Test
    public void testResetDatabaseAcceptsSuccessfulAndAcceptsNewInserts() throws SQLException {
        ResultSet contactsBefore = database.select("SELECT * FROM Contact");
        database.query("INSERT INTO Contact (username) VALUES ('Test')");
        ResultSet contactsAfter = database.select("SELECT * FROM Contact");
        Assert.assertFalse(contactsBefore.next());
        Assert.assertTrue(contactsAfter.next());
    }

    @Test
    public void testResetDatabaseSuccessful() throws SQLException {
        ResultSet messages = database.select("SELECT * FROM Message");
        ResultSet contacts = database.select("SELECT * FROM Contact");
        ResultSet scripts = database.select("SELECT * FROM Script");
        Assert.assertFalse(contacts.next());
        Assert.assertFalse(messages.next());
        Assert.assertFalse(scripts.next());
    }

    @Test
    public void testQuerySuccessful() throws SQLException {
        Assert.assertTrue(database.query("INSERT INTO Contact(username) VALUES ('Test')"));
    }

    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void testQueryPrimaryKeyViolation() throws SQLException {
        database.query("INSERT INTO Contact(id, username) VALUES (1, 'Test')");
        database.query("INSERT INTO Contact(id, username) VALUES (1, 'Test')");
    }

    @Test
    public void testSelectSuccessful() throws SQLException {
        String randomUsername = UUID.randomUUID().toString().substring(0, 12);
        database.query("INSERT INTO Contact(username) VALUES ('" + randomUsername + "')");
        ResultSet result = database.select("SELECT * FROM Contact");
        result.next();
        Assert.assertEquals(result.getObject(2), randomUsername);
    }

    @Test
    public void testIsDatabaseConnectionOpen() throws SQLException {
        Assert.assertTrue(database.isOpen());
    }

    @Test
    public void testCanDatabaseConnectionBeClosed() throws Exception {
        database.close();
        Assert.assertFalse(database.isOpen());
    }

}
