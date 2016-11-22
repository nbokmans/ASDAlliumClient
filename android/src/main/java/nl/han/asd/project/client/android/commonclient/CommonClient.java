package nl.han.asd.project.client.android.commonclient;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.androidannotations.annotations.EBean;

import java.util.Properties;

import nl.han.asd.project.client.commonclient.CommonClientGateway;
import nl.han.asd.project.client.commonclient.CommonClientModule;

@EBean(scope = EBean.Scope.Singleton)
public class CommonClient {
    private CommonClientGateway gateway;

    public CommonClient() {
        Injector injector = Guice.createInjector(new CommonClientModule(), new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = new Properties();
                properties.setProperty("master-server-host", "tumma.nl");
                properties.setProperty("master-server-port", "36475");
                properties.setProperty("heartbeat-delay", "5");
                bind(Properties.class).toInstance(properties);
            }
        });
        gateway = injector.getInstance(CommonClientGateway.class);
    }

    public CommonClientGateway getGateway() {
        return gateway;
    }
}