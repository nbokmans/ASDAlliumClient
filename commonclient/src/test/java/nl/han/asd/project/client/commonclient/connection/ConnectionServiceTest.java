package nl.han.asd.project.client.commonclient.connection;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConnectionService.class, Parser.class, Wrapper.class, Semaphore.class })
public class ConnectionServiceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Mock
    private IEncryptionService encryptionServiceMock;
    @Mock
    private File fileMock;

    @Test(expected = IllegalArgumentException.class)
    public void hostPortConstructorNullHost() throws Exception {
        new ConnectionService(null, 1024);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encrytionServiceHostPortPublicKeyConstructorNullEncryptionService() throws Exception {
        new ConnectionService(null, "localhost", 1024, new byte[] { 1, 2, 3 });
    }

    @Test(expected = IllegalArgumentException.class)
    public void encrytionServiceHostPortPublicKeyConstructorNullHost() throws Exception {
        new ConnectionService(encryptionServiceMock, null, 1024, new byte[] { 1, 2, 3 });

    }

    @Test(expected = IllegalArgumentException.class)
    public void encrytionServiceHostPortPublicKeyConstructorNullPublicKey() throws Exception {
        new ConnectionService(encryptionServiceMock, "localhost", 1024, (byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encrytionServiceHostPortKeyFileConstructorNullEncryptionService() throws Exception {
        new ConnectionService(null, "localhost", 1024, fileMock);

    }

    @Test(expected = IllegalArgumentException.class)
    public void encrytionServiceHostPortKeyFileConstructorNullHost() throws Exception {
        new ConnectionService(encryptionServiceMock, null, 1024, fileMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encrytionServiceHostPortKeyFileConstructorNullKeyfile() throws Exception {
        new ConnectionService(encryptionServiceMock, "localhost", 1024, (File) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeNullWrapper() throws Exception {
        new ConnectionService("localhost", 1024).write(null);
    }

    @Test
    public void writeMutexTimeout() throws Exception {
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        whenNew(Semaphore.class).withArguments(eq(1)).thenReturn(mutexMock);

        SocketHandler socketHandlerMock = mock(SocketHandler.class);
        SocketHandler newSocketHandlerMock = mock(SocketHandler.class);

        whenNew(SocketHandler.class).withAnyArguments().thenReturn(socketHandlerMock);
        ConnectionService connectionService = new ConnectionService("localhost", 1024);
        whenNew(SocketHandler.class).withAnyArguments().thenReturn(newSocketHandlerMock);

        PowerMockito.when(mutexMock.tryAcquire(-1, TimeUnit.MILLISECONDS)).thenReturn(false);

        Wrapper wrapperMock = mock(Wrapper.class);
        connectionService.write(wrapperMock);

        verifyNoMoreInteractions(socketHandlerMock);
        verify(newSocketHandlerMock).write(wrapperMock);
        verify(newSocketHandlerMock).close();
    }

    @Test
    public void writeMutexAquiredNoWaitingThreads() throws Exception {
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        whenNew(Semaphore.class).withArguments(eq(1)).thenReturn(mutexMock);

        SocketHandler socketHandlerMock = mock(SocketHandler.class);
        SocketHandler newSocketHandlerMock = mock(SocketHandler.class);

        whenNew(SocketHandler.class).withAnyArguments().thenReturn(socketHandlerMock);
        ConnectionService connectionService = new ConnectionService("localhost", 1024);
        whenNew(SocketHandler.class).withAnyArguments().thenReturn(newSocketHandlerMock);

        PowerMockito.when(mutexMock.tryAcquire(-1, TimeUnit.MILLISECONDS)).thenReturn(true);
        PowerMockito.when(mutexMock.hasQueuedThreads()).thenReturn(false);

        Wrapper wrapperMock = mock(Wrapper.class);
        connectionService.write(wrapperMock);

        verify(mutexMock).release();
        verifyNoMoreInteractions(newSocketHandlerMock);
        verify(socketHandlerMock).write(wrapperMock);
        verify(socketHandlerMock).close();
    }

    @Test
    public void writeAndReadMutexAquiredNoWaitingThreads() throws Exception {
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        whenNew(Semaphore.class).withArguments(eq(1)).thenReturn(mutexMock);

        SocketHandler socketHandlerMock = mock(SocketHandler.class);
        SocketHandler newSocketHandlerMock = mock(SocketHandler.class);

        whenNew(SocketHandler.class).withAnyArguments().thenReturn(socketHandlerMock);
        ConnectionService connectionService = new ConnectionService("localhost", 1024);
        whenNew(SocketHandler.class).withAnyArguments().thenReturn(newSocketHandlerMock);

        PowerMockito.when(mutexMock.tryAcquire(-1, TimeUnit.MILLISECONDS)).thenReturn(true);
        PowerMockito.when(mutexMock.hasQueuedThreads()).thenReturn(false);

        Wrapper requestWrapperMock = mock(Wrapper.class);
        GeneratedMessage responseMock = mock(Wrapper.class);
        when(socketHandlerMock.writeAndRead(eq(requestWrapperMock))).thenReturn(responseMock);

        GeneratedMessage response = connectionService.writeAndRead(requestWrapperMock);

        if (!response.equals(responseMock)) {
            fail();
        }

        verifyNoMoreInteractions(newSocketHandlerMock);
        verify(socketHandlerMock).writeAndRead(requestWrapperMock);
        verify(socketHandlerMock).close();
    }

    @Test
    public void writeAndReadMutexAquiredWaitingThreads() throws Exception {
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        whenNew(Semaphore.class).withArguments(eq(1)).thenReturn(mutexMock);

        SocketHandler socketHandlerMock = mock(SocketHandler.class);
        SocketHandler newSocketHandlerMock = mock(SocketHandler.class);

        whenNew(SocketHandler.class).withAnyArguments().thenReturn(socketHandlerMock);
        ConnectionService connectionService = new ConnectionService("localhost", 1024);
        whenNew(SocketHandler.class).withAnyArguments().thenReturn(newSocketHandlerMock);

        PowerMockito.when(mutexMock.tryAcquire(-1, TimeUnit.MILLISECONDS)).thenReturn(true);
        PowerMockito.when(mutexMock.hasQueuedThreads()).thenReturn(true);

        Wrapper requestWrapperMock = mock(Wrapper.class);
        GeneratedMessage responseMock = mock(Wrapper.class);
        when(socketHandlerMock.writeAndRead(eq(requestWrapperMock))).thenReturn(responseMock);

        GeneratedMessage response = connectionService.writeAndRead(requestWrapperMock);

        if (!response.equals(responseMock)) {
            fail();
        }

        verifyNoMoreInteractions(newSocketHandlerMock);
        verify(socketHandlerMock).writeAndRead(requestWrapperMock);
        verify(socketHandlerMock, times(0)).close();
    }

    @Test(expected = MessageNotSentException.class)
    public void writeAndReadInterruptException() throws Exception {
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        whenNew(Semaphore.class).withArguments(eq(1)).thenReturn(mutexMock);

        SocketHandler socketHandlerMock = mock(SocketHandler.class);
        SocketHandler newSocketHandlerMock = mock(SocketHandler.class);

        whenNew(SocketHandler.class).withAnyArguments().thenReturn(socketHandlerMock);
        ConnectionService connectionService = new ConnectionService("localhost", 1024);
        whenNew(SocketHandler.class).withAnyArguments().thenReturn(newSocketHandlerMock);

        PowerMockito.when(mutexMock.tryAcquire(-1, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());

        Wrapper wrapperMock = mock(Wrapper.class);
        connectionService.writeAndRead(wrapperMock);

        verify(socketHandlerMock, times(0)).write(wrapperMock);
    }

    @Test
    public void writeAndReadTimeoutNewSocket() throws Exception {
        Semaphore mutexMock = PowerMockito.mock(Semaphore.class);
        whenNew(Semaphore.class).withArguments(eq(1)).thenReturn(mutexMock);

        SocketHandler socketHandlerMock = mock(SocketHandler.class);
        SocketHandler newSocketHandlerMock = mock(SocketHandler.class);

        whenNew(SocketHandler.class).withAnyArguments().thenReturn(socketHandlerMock);
        ConnectionService connectionService = new ConnectionService("localhost", 1024);
        whenNew(SocketHandler.class).withAnyArguments().thenReturn(newSocketHandlerMock);

        PowerMockito.when(mutexMock.tryAcquire(-1, TimeUnit.MILLISECONDS)).thenReturn(false);

        Wrapper requestWrapperMock = mock(Wrapper.class);
        GeneratedMessage responseMock = mock(Wrapper.class);
        when(newSocketHandlerMock.writeAndRead(eq(requestWrapperMock))).thenReturn(responseMock);

        GeneratedMessage response = connectionService.writeAndRead(requestWrapperMock);
        assertSame(responseMock, response);

        verifyNoMoreInteractions(socketHandlerMock);
        verify(newSocketHandlerMock).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrapperNullMessage() throws Exception {
        new ConnectionService("localhost", 1024).wrap(null, Wrapper.Type.ADMINDELETEREQUEST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wraperNullType() throws Exception {
        GeneratedMessage messageMock = mock(GeneratedMessage.class);
        new ConnectionService("localhost", 1024).wrap(messageMock, null);
    }

    @Test
    public void wrapperNullEncyptionService() throws Exception {
        ByteString messageByteString = ByteString.copyFrom("message byte string".getBytes());

        Wrapper.Type type = Wrapper.Type.ADMINDELETEREQUEST;
        GeneratedMessage messageMock = mock(GeneratedMessage.class);
        when(messageMock.toByteString()).thenReturn(messageByteString);

        Wrapper.Builder builderMock = PowerMockito.mock(Wrapper.Builder.class);
        PowerMockito.mockStatic(Wrapper.class);
        PowerMockito.when(Wrapper.newBuilder()).thenReturn(builderMock);

        Wrapper wrapperMock = mock(Wrapper.class);
        when(builderMock.build()).thenReturn(wrapperMock);

        Wrapper ret = new ConnectionService("localhost", 1024).wrap(messageMock, type);
        assertSame(wrapperMock, ret);

        verify(builderMock).setType(type);
        verify(builderMock).setData(messageByteString);
    }

    @Test
    public void wrapperEncryptionService() throws Exception {
        Wrapper.Type type = Wrapper.Type.ADMINDELETEREQUEST;
        byte[] messageBytes = "message byte string".getBytes();

        GeneratedMessage messageMock = mock(GeneratedMessage.class);
        when(messageMock.toByteArray()).thenReturn(messageBytes);

        byte[] publicKeyBytes = "public key bytes".getBytes();
        byte[] encryptedMessageBytes = "encrypted message bytes".getBytes();
        IEncryptionService encryptionServiceMock = mock(IEncryptionService.class);
        when(encryptionServiceMock.encryptData(publicKeyBytes, messageBytes)).thenReturn(encryptedMessageBytes);

        Wrapper.Builder builderMock = PowerMockito.mock(Wrapper.Builder.class);
        PowerMockito.mockStatic(Wrapper.class);
        PowerMockito.when(Wrapper.newBuilder()).thenReturn(builderMock);

        Wrapper wrapperMock = mock(Wrapper.class);
        when(builderMock.build()).thenReturn(wrapperMock);

        Wrapper ret = new ConnectionService(encryptionServiceMock, "localhost", 1024, publicKeyBytes).wrap(messageMock,
                type);
        assertSame(wrapperMock, ret);

        verify(builderMock).setType(type);
        verify(builderMock).setData(ByteString.copyFrom(encryptedMessageBytes));
    }

}
