package io.alakazam.jaxws;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPBinding;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Performs CXF Bus setup and provides methods for publishing JAX-WS endpoints and create JAX-WS clients.
 */
public class JAXWSEnvironment {

    private static Logger log = LoggerFactory.getLogger(JAXWSEnvironment.class);

    protected final Bus bus;
    protected final String defaultPath;

    public String getDefaultPath() {
        return this.defaultPath;
    }

    public JAXWSEnvironment(String defaultPath) {

        System.setProperty("org.apache.cxf.Logger", "org.apache.cxf.common.logging.Slf4jLogger");
        /*
        Instruct CXF to use CXFBusFactory instead of SpringBusFactory. CXFBusFactory provides ExtensionManagerBus
        which loads extension based on contents of META-INF/cxf/bus-extensions.txt file. Many CXF modules contain
        such file. When building shaded jar for alakazam service, these files have to be merged into single
        bus-extension.txt file by using AppendingTransformer with Maven shade plugin.
        */
        System.setProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME, "org.apache.cxf.bus.CXFBusFactory");
        this.bus = BusFactory.newInstance().createBus();
        this.defaultPath = defaultPath.replace("/*", "");
    }

    public HttpServlet buildServlet() {
        CXFNonSpringServlet cxf = new CXFNonSpringServlet();
        cxf.setBus(bus);
        return cxf;
    }

    public void logEndpoints() {
        ServerRegistry sr = bus.getExtension(org.apache.cxf.endpoint.ServerRegistry.class);
        if (sr.getServers().size() > 0) {
            String endpoints = "";
            for (Server s : sr.getServers()) {
                endpoints += "    " + this.defaultPath + s.getEndpoint().getEndpointInfo().getAddress() +
                        " (" + s.getEndpoint().getEndpointInfo().getInterface().getName() + ")\n";
            }
            log.info("The following JAX-WS service endpoints were registered:\n\n" + endpoints);
        } else {
            log.info("No JAX-WS service endpoints were registered.");
        }
    }

    /**
     * Publish JAX-WS server side endpoint.
     */
    public void publishEndpoint(EndpointBuilder endpointBuilder) {
        checkArgument(endpointBuilder != null, "EndpointBuilder is null");

        Endpoint endpoint = Endpoint.publish(endpointBuilder.getPath(), endpointBuilder.getService());

        // MTOM support
        if (endpointBuilder.isMtomEnabled()) {
            ((SOAPBinding) endpoint.getBinding()).setMTOMEnabled(true);
        }


        org.apache.cxf.endpoint.Endpoint cxfendpoint = null;

        ServerRegistry sr = bus.getExtension(org.apache.cxf.endpoint.ServerRegistry.class);

        for (Server s : sr.getServers()) {
            Class<?> endpointClass = ((Class) s.getEndpoint().getService().get("endpoint.class"));
            if (endpointBuilder.getService().getClass().getName().equals(endpointClass.getName()) ||
                    (endpointClass.isInterface() && endpointClass.isAssignableFrom(endpointBuilder.getService().getClass()))) {
                cxfendpoint = s.getEndpoint();
                break;
            }
        }

        if (cxfendpoint == null) {
            throw new RuntimeException("Error publishing endpoint for service: "
                    + endpointBuilder.getService().getClass().getSimpleName() +
                    ": " + "Matching 'endpoint.class' not found.");
        }

        Invoker invoker = cxfendpoint.getService().getInvoker();

        cxfendpoint.getService().setInvoker(invoker);

        // CXF interceptors

        if (endpointBuilder.getCxfInInterceptors() != null) {
            cxfendpoint.getInInterceptors().addAll(endpointBuilder.getCxfInInterceptors());
        }

        if (endpointBuilder.getCxfInFaultInterceptors() != null) {
            cxfendpoint.getInFaultInterceptors().addAll(endpointBuilder.getCxfInFaultInterceptors());
        }

        if (endpointBuilder.getCxfOutInterceptors() != null) {
            cxfendpoint.getOutInterceptors().addAll(endpointBuilder.getCxfOutInterceptors());
        }

        if (endpointBuilder.getCxfOutFaultInterceptors() != null) {
            cxfendpoint.getOutFaultInterceptors().addAll(endpointBuilder.getCxfOutFaultInterceptors());
        }
    }

    /**
     * JAX-WS client factory
     *
     * @param clientBuilder ClientBuilder.
     * @param <T>           Service interface type.
     * @return JAX-WS client proxy.
     */
    public <T> T getClient(ClientBuilder<T> clientBuilder) {

        JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
        proxyFactory.setServiceClass(clientBuilder.getServiceClass());
        proxyFactory.setAddress(clientBuilder.getAddress());

        // JAX-WS handlers
        if (clientBuilder.getHandlers() != null) {
            for (Handler h : clientBuilder.getHandlers()) {
                proxyFactory.getHandlers().add(h);
            }
        }

        // CXF interceptors
        if (clientBuilder.getCxfInInterceptors() != null) {
            proxyFactory.getInInterceptors().addAll(clientBuilder.getCxfInInterceptors());
        }
        if (clientBuilder.getCxfInFaultInterceptors() != null) {
            proxyFactory.getInFaultInterceptors().addAll(clientBuilder.getCxfInFaultInterceptors());
        }
        if (clientBuilder.getCxfOutInterceptors() != null) {
            proxyFactory.getOutInterceptors().addAll(clientBuilder.getCxfOutInterceptors());
        }
        if (clientBuilder.getCxfOutFaultInterceptors() != null) {
            proxyFactory.getOutFaultInterceptors().addAll(clientBuilder.getCxfOutFaultInterceptors());
        }

        T proxy = clientBuilder.getServiceClass().cast(proxyFactory.create());

        // MTOM support
        if (clientBuilder.isMtomEnabled()) {
            BindingProvider bp = (BindingProvider) proxy;
            SOAPBinding binding = (SOAPBinding) bp.getBinding();
            binding.setMTOMEnabled(true);
        }

        HTTPConduit http = (HTTPConduit) ClientProxy.getClient(proxy).getConduit();
        HTTPClientPolicy client = http.getClient();
        client.setConnectionTimeout(clientBuilder.getConnectTimeout());
        client.setReceiveTimeout(clientBuilder.getReceiveTimeout());

        return proxy;
    }
}
