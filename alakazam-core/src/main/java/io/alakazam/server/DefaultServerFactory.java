package io.alakazam.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.alakazam.jetty.ConnectorFactory;
import io.alakazam.jetty.HttpConnectorFactory;
import io.alakazam.jetty.RoutingHandler;
import io.alakazam.setup.Environment;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

// TODO: 5/15/13 <coda> -- add tests for DefaultServerFactory

/**
 * The default implementation of {@link ServerFactory}, which allows for multiple sets of
 * application connectors, all running on separate ports.
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
 *     <tr>
 *         <td>Name</td>
 *         <td>Default</td>
 *         <td>Description</td>
 *     </tr>
 *     <tr>
 *         <td>{@code applicationConnectors}</td>
 *         <td>An {@link HttpConnectorFactory HTTP connector} listening on port 8080.</td>
 *         <td>A set of {@link ConnectorFactory connectors} which will handle application requests.</td>
 *     </tr>
 * </table>
 * <p/>
 * For more configuration parameters, see {@link AbstractServerFactory}.
 *
 * @see ServerFactory
 * @see AbstractServerFactory
 */
@JsonTypeName("default")
public class DefaultServerFactory extends AbstractServerFactory {
    @Valid
    @NotNull
    private List<ConnectorFactory> applicationConnectors =
            Lists.newArrayList(HttpConnectorFactory.application());

    @JsonProperty
    public List<ConnectorFactory> getApplicationConnectors() {
        return applicationConnectors;
    }

    @JsonProperty
    public void setApplicationConnectors(List<ConnectorFactory> connectors) {
        this.applicationConnectors = connectors;
    }

    @Override
    public Server build(Environment environment) {
        printBanner(environment.getName());
        final ThreadPool threadPool = createThreadPool();
        final Server server = buildServer(environment.lifecycle(), threadPool);
        final Handler applicationHandler = createAppServlet(server,
                                                            environment.resteasy(),
                                                            environment.getObjectMapper(),
                                                            environment.getValidator(),
                                                            environment.getApplicationContext(),
                                                            environment.getRestEasyServletContainer());
        final RoutingHandler routingHandler = buildRoutingHandler(server, applicationHandler);
        server.setHandler(addStatsHandler(routingHandler));
        return server;
    }

    private RoutingHandler buildRoutingHandler(Server server, Handler applicationHandler) {
        final List<Connector> appConnectors = buildAppConnectors(server);

        final Map<Connector, Handler> handlers = Maps.newLinkedHashMap();

        for (Connector connector : appConnectors) {
            server.addConnector(connector);
            handlers.put(connector, applicationHandler);
        }

        return new RoutingHandler(handlers);
    }

    private List<Connector> buildAppConnectors(Server server) {
        final List<Connector> connectors = Lists.newArrayList();
        for (ConnectorFactory factory : applicationConnectors) {
            connectors.add(factory.build(server, "application", null));
        }
        return connectors;
    }
}
