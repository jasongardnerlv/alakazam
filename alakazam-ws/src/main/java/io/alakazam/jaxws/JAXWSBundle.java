package io.alakazam.jaxws;

import io.alakazam.Bundle;
import io.alakazam.lifecycle.ServerLifecycleListener;
import io.alakazam.setup.Bootstrap;
import io.alakazam.setup.Environment;
import org.eclipse.jetty.server.Server;

import javax.xml.ws.handler.Handler;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A alakazam bundle that enables alakazam applications to publish SOAP web services using JAX-WS and create
 * web service clients.
 */
public class JAXWSBundle implements Bundle {

    protected static final String DEFAULT_PATH = "/soap";
    protected JAXWSEnvironment jaxwsEnvironment;
    protected String servletPath;

    /**
     * Initialize JAXWSEnvironment. Service endpoints are published relative to '/soap'.
     */
    public JAXWSBundle() {
        this(DEFAULT_PATH);
    }

    /**
     * Initialize JAXWSEnvironment. Service endpoints are published relative to the provided servletPath.
     *
     * @param servletPath Root path for service endpoints. Leading slash is required.
     */
    public JAXWSBundle(String servletPath) {
        this(servletPath, new JAXWSEnvironment(servletPath));
    }

    /**
     * Use provided JAXWSEnvironment. Service endpoints are published relative to the provided servletPath.
     *
     * @param servletPath      Root path for service endpoints. Leading slash is required.
     * @param jaxwsEnvironment Valid JAXWSEnvironment.
     */
    public JAXWSBundle(String servletPath, JAXWSEnvironment jaxwsEnvironment) {
        checkArgument(servletPath != null, "Servlet path is null");
        checkArgument(servletPath.startsWith("/"), "%s is not an absolute path", servletPath);
        checkArgument(jaxwsEnvironment != null, "jaxwsEnvironment is null");
        this.servletPath = servletPath.endsWith("/") ? servletPath + "*" : servletPath + "/*";
        this.jaxwsEnvironment = jaxwsEnvironment;
    }

    /**
     * Implements com.yammer.alakazam.Bundle#initialize()
     */
    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    /**
     * Implements com.yammer.alakazam.Bundle#run()
     */
    @Override
    public void run(Environment environment) {
        checkArgument(environment != null, "Environment is null");
        environment.servlets().addServlet("CXF Servlet", jaxwsEnvironment.buildServlet()).addMapping(servletPath);
        environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
            @Override
            public void serverStarted(Server server) {
                jaxwsEnvironment.logEndpoints();
            }
        });
    }

    /**
     * Publish JAX-WS endpoint. Endpoint will be published relative to the CXF servlet path.
     *
     * @param endpointBuilder EndpointBuilder.
     */
    public void publishEndpoint(EndpointBuilder endpointBuilder) {
        checkArgument(endpointBuilder != null, "EndpointBuilder is null");
        this.jaxwsEnvironment.publishEndpoint(endpointBuilder);
    }

    /**
     * Publish JAX-WS protected endpoint using alakazam BasicAuthentication with alakazam Hibernate Bundle
     * integration. Service is scanned for @UnitOfWork annotations. EndpointBuilder is published relative to the CXF
     * servlet path.
     *
     * @param path           Relative endpoint path.
     * @param service        Service implementation.
     * @deprecated Use the {@link #publishEndpoint(EndpointBuilder)} publishEndpoint} method instead.
     */
    public void publishEndpoint(String path, Object service) {
        checkArgument(service != null, "Service is null");
        checkArgument(path != null, "Path is null");
        checkArgument((path).trim().length() > 0, "Path is empty");
        this.publishEndpoint(new EndpointBuilder(path, service));
    }

    /**
     * Factory method for creating JAX-WS clients.
     *
     * @param serviceClass Service interface class.
     * @param address      Endpoint URL address.
     * @param handlers     Client side JAX-WS handlers. Optional.
     * @param <T>          Service interface type.
     * @return JAX-WS client proxy.
     * @deprecated Use the {@link #getClient(ClientBuilder)} getClient} method instead.
     */
    @Deprecated
    public <T> T getClient(Class<T> serviceClass, String address, Handler... handlers) {
        checkArgument(serviceClass != null, "ServiceClass is null");
        checkArgument(address != null, "Address is null");
        checkArgument((address).trim().length() > 0, "Address is empty");
        return jaxwsEnvironment.getClient(
                new ClientBuilder<T>(serviceClass, address).handlers(handlers));
    }

    /**
     * Factory method for creating JAX-WS clients.
     *
     * @param clientBuilder ClientBuilder.
     * @param <T>           Service interface type.
     * @return Client proxy.
     */
    public <T> T getClient(ClientBuilder<T> clientBuilder) {
        checkArgument(clientBuilder != null, "ClientBuilder is null");
        return jaxwsEnvironment.getClient(clientBuilder);
    }
}
