package io.alakazam.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableMap;
import io.alakazam.jetty.ConnectorFactory;
import io.alakazam.jetty.ContextRoutingHandler;
import io.alakazam.jetty.HttpConnectorFactory;
import io.alakazam.setup.Environment;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

// TODO: 5/15/13 <coda> -- add tests for SimpleServerFactory

/**
 * A single-connector implementation of {@link ServerFactory}, suitable for PaaS deployments
 * (e.g., Heroku) where applications are limited to a single, runtime-defined port. A startup script
 * can override the port via {@code -Ddw.server.connector.port=$PORT}.
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
 *     <tr>
 *         <td>Name</td>
 *         <td>Default</td>
 *         <td>Description</td>
 *     </tr>
 *     <tr>
 *         <td>{@code connector}</td>
 *         <td>An {@link HttpConnectorFactory HTTP connector} listening on port {@code 8080}.</td>
 *         <td>The {@link ConnectorFactory connector} which will handle the application requests.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code applicationContextPath}</td>
 *         <td>{@code /application}</td>
 *         <td>The context path of the application servlets, including RestEasy.</td>
 *     </tr>
 * </table>
 * <p/>
 * For more configuration parameters, see {@link AbstractServerFactory}.
 *
 * @see ServerFactory
 * @see AbstractServerFactory
 */
@JsonTypeName("simple")
public class SimpleServerFactory extends AbstractServerFactory {
    @Valid
    @NotNull
    private ConnectorFactory connector = HttpConnectorFactory.application();

    @NotEmpty
    private String applicationContextPath = "/application";

    @JsonProperty
    public ConnectorFactory getConnector() {
        return connector;
    }

    @JsonProperty
    public void setConnector(ConnectorFactory factory) {
        this.connector = factory;
    }

    @JsonProperty
    public String getApplicationContextPath() {
        return applicationContextPath;
    }

    @JsonProperty
    public void setApplicationContextPath(String contextPath) {
        this.applicationContextPath = contextPath;
    }

    @Override
    public Server build(Environment environment) {
        printBanner(environment.getName());
        final ThreadPool threadPool = createThreadPool();
        final Server server = buildServer(environment.lifecycle(), threadPool);

        environment.getApplicationContext().setContextPath(applicationContextPath);
        final Handler applicationHandler = createAppServlet(server,
                                                            environment.resteasy(),
                                                            environment.getObjectMapper(),
                                                            environment.getValidator(),
                                                            environment.getApplicationContext(),
                                                            environment.getRestEasyServletContainer());

        final Connector conn = connector.build(server,
                                               environment.getName(),
                                               null);

        server.addConnector(conn);

        final ContextRoutingHandler routingHandler = new ContextRoutingHandler(ImmutableMap.of(
                applicationContextPath, applicationHandler
        ));
        server.setHandler(addStatsHandler(addRequestLog(server, routingHandler, environment.getName())));

        return server;
    }
}
