package io.alakazam.jetty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.alakazam.jackson.Discoverable;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * A factory for creating Jetty {@link Connector}s.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ConnectorFactory extends Discoverable {
    /**
     * Create a new connector.
     *
     * @param server     the application's {@link Server} instance
     * @param name       the application's name
     * @param threadPool the application's thread pool
     * @return a {@link Connector}
     */
    Connector build(Server server,
                    String name,
                    ThreadPool threadPool);
}
