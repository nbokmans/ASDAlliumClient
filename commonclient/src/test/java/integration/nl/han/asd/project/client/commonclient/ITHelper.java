package integration.nl.han.asd.project.client.commonclient;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.xebialabs.overcast.host.DockerHost;
import nl.han.asd.project.client.commonclient.CommonClientGateway;
import nl.han.asd.project.client.commonclient.CommonClientModule;
import nl.han.asd.project.client.commonclient.connection.IConnectionServiceFactory;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.database.IDatabase;
import nl.han.asd.project.client.commonclient.graph.IUpdateGraph;
import nl.han.asd.project.client.commonclient.heartbeat.IHeartbeatService;
import nl.han.asd.project.client.commonclient.login.ILoginService;
import nl.han.asd.project.client.commonclient.master.IGetClientGroup;
import nl.han.asd.project.client.commonclient.master.IRegistration;
import nl.han.asd.project.client.commonclient.message.IMessageConfirmation;
import nl.han.asd.project.client.commonclient.message.IReceiveMessage;
import nl.han.asd.project.client.commonclient.message.ISendMessage;
import nl.han.asd.project.client.commonclient.message.ISubscribeMessageReceiver;
import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import nl.han.asd.project.client.commonclient.store.IContactStore;
import nl.han.asd.project.client.commonclient.store.IMessageStore;
import nl.han.asd.project.client.commonclient.store.IScriptStore;
import nl.han.asd.project.protocol.HanRoutingProtocol;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ITHelper {
    private static final int TIME_OUT_MASTER = 5000;
    private static final int TIME_OUT_CLIENT = 5000;
    private static final int TIME_OUT_NODE = 8000;
    private static final int NR_TRIES_MASTER = 10;
    private static final int NR_TRIES_CLIENT = 10;

    protected String masterHost;
    protected int masterPort;
    protected int nextPort;

    protected CloudHost master;
    protected List<DockerHost> nodes = new ArrayList<>();
    protected DockerHost client;

    protected IContactStore contactStore;
    protected IMessageStore messageStore;
    protected IRegistration registration;
    protected ILoginService loginService;
    protected ISendMessage sendMessage;
    protected ISubscribeMessageReceiver subscribeMessageReceiver;
    protected IScriptStore scriptStore;
    protected IHeartbeatService heartbeatService;
    protected IUpdateGraph updateGraph;
    protected IMessageConfirmation messageConfirmation;
    protected IGetClientGroup getClientGroup;
    protected IConnectionServiceFactory connectionFactory;
    protected IReceiveMessage receiveMessage;
    protected IDatabase database;
    protected IPersistence persistence;
    protected CommonClientGateway commonClientGateway;

    protected void startMaster() {
        master = CloudHostFactory.getCloudHost("master");
        master.setup();
        masterHost = master.getHostName();
        masterPort = master.getPort(1337);
        nextPort = masterPort + 1;

        checkMasterStarted(master);
    }

    protected void startNodes(int numberNodes) {
        for (int i = 0; i < numberNodes; i++) {
            List<String> nodeCommand = new ArrayList<>();
            nodeCommand.add("masterIP=" + masterHost);
            nodeCommand.add("masterPort=" + masterPort);
            nodeCommand.add("externalPort=" + nextPort);
            DockerHost node = (DockerHost) CloudHostFactory.getCloudHost("node");
            nodes.add(node);
            node.setEnv(nodeCommand);
            node.setup();
            nextPort++;
        }

        checkNodesStarted(nodes);
    }

    protected void startClient(String integrationType) {
        int currentNrUsers = getNrClients();
        List<String> clientCommand = new ArrayList<>();
        clientCommand.add("master-server-host=" + masterHost);
        clientCommand.add("master-server-port=" + masterPort);
        clientCommand.add("integration-enabled=true");
        clientCommand.add("integration-type=" + integrationType);
        clientCommand.add("heartbeat-delay=3");
        client = (DockerHost) CloudHostFactory.getCloudHost("client");
        client.setEnv(clientCommand);
        client.setup();
        nextPort++;

        int attempts = 0;
        while (currentNrUsers == getNrClients() && attempts < NR_TRIES_CLIENT){
            try {
                Thread.sleep(TIME_OUT_CLIENT);
                attempts++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void injectInterfaces() {
        final Properties properties = new Properties();
        properties.setProperty("master-server-host", masterHost);
        properties.setProperty("master-server-port", Integer.toString(masterPort));
        properties.setProperty("heartbeat-delay", "3");

        Injector injector = Guice.createInjector(new CommonClientModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Properties.class).toInstance(properties);
                    }
                });

        contactStore = injector.getInstance(IContactStore.class);
        messageStore = injector.getInstance(IMessageStore.class);
        registration = injector.getInstance(IRegistration.class);
        loginService = injector.getInstance(ILoginService.class);
        sendMessage = injector.getInstance(ISendMessage.class);
        subscribeMessageReceiver = injector.getInstance(ISubscribeMessageReceiver.class);
        scriptStore = injector.getInstance(IScriptStore.class);
        heartbeatService = injector.getInstance(IHeartbeatService.class);
        messageConfirmation = injector.getInstance(IMessageConfirmation.class);
        updateGraph = injector.getInstance(IUpdateGraph.class);
        getClientGroup = injector.getInstance(IGetClientGroup.class);
        receiveMessage = injector.getInstance(IReceiveMessage.class);
        connectionFactory = injector.getInstance(IConnectionServiceFactory.class);
        database = injector.getInstance(IDatabase.class);
        persistence = injector.getInstance(IPersistence.class);

        commonClientGateway = new CommonClientGateway(contactStore, messageStore, registration, loginService,
                scriptStore, sendMessage, subscribeMessageReceiver, heartbeatService, messageConfirmation, updateGraph);
    }

    protected void checkMasterStarted(CloudHost cloudHost){
        int attempts = 0;
        while(true) {
            try {
                Thread.sleep(TIME_OUT_MASTER);
                new Socket(cloudHost.getHostName(), cloudHost.getPort(1337)).close();
                break;
            } catch (IOException | InterruptedException e) {
                attempts++;
            }
            if (attempts > NR_TRIES_MASTER){
                return;
            }
        }
    }

    protected void checkNodesStarted(List<DockerHost> dockerHosts){
        int attempts = 0;
        for (int i = 0; i < dockerHosts.size(); i++) {
            while(true) {
                try {
                    new Socket(dockerHosts.get(i).getHostName(), dockerHosts.get(i).getPort(1337)).close();
                    break;
                } catch (IOException e) {
                    attempts++;
                }
                try {
                    Thread.sleep(TIME_OUT_NODE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (attempts > dockerHosts.size()) {
                    return;
                }
            }
        }
    }

    protected int getNrClients(){
        HanRoutingProtocol.ClientRequest.Builder builder = HanRoutingProtocol.ClientRequest.newBuilder();
        builder.setClientGroup(1);
        try {
            HanRoutingProtocol.ClientResponse clientWrapper = getClientGroup.getClientGroup(builder.build());
            return clientWrapper.getClientsCount();
        } catch (IOException | MessageNotSentException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
