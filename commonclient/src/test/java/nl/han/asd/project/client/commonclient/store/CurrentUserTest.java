package nl.han.asd.project.client.commonclient.store;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author DDulos
 * @version 1.0
 * @since 27-May-16
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Contact.class, CurrentUser.class})
public class CurrentUserTest {

    private static final String TEST_CURRENTUSER_USERNAME = "testuser";
    private static final byte[] TEST_CURRENTUSER_PUBLICKEY = "thisIsTestPublicKey".getBytes();
    private static final String TEST_CURRENTUSER_SECRETHASH = "thisIsATestSecretHash";
    private CurrentUser currentUser;
    private Contact contactMock;

    @Before
    public void setUp() throws Exception {
        currentUser = null;
        contactMock = PowerMockito.mock(Contact.class);
        PowerMockito.whenNew(Contact.class).withArguments(TEST_CURRENTUSER_USERNAME, TEST_CURRENTUSER_PUBLICKEY).
                thenReturn(contactMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCurrentUserWithoutSecretHash(){
        currentUser = new CurrentUser(TEST_CURRENTUSER_USERNAME, TEST_CURRENTUSER_PUBLICKEY, null);
    }

    @Test
    public void testgetSecretHash() throws Exception {
        currentUser = new CurrentUser(TEST_CURRENTUSER_USERNAME, TEST_CURRENTUSER_PUBLICKEY, TEST_CURRENTUSER_SECRETHASH);
        Assert.assertEquals(TEST_CURRENTUSER_SECRETHASH, currentUser.getSecretHash());
    }

    @Test
    public void testGetCurrentUserAsContact() throws Exception {
        currentUser = new CurrentUser(TEST_CURRENTUSER_USERNAME, TEST_CURRENTUSER_PUBLICKEY, TEST_CURRENTUSER_SECRETHASH);
        Assert.assertEquals(contactMock, currentUser.asContact());
    }
}