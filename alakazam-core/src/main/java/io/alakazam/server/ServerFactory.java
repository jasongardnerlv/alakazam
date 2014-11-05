package io.alakazam.server;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.alakazam.jackson.Discoverable;
import io.alakazam.setup.Environment;
import org.eclipse.jetty.server.Server;

/**
 * A factory for building {@link Server} instances for Alakazam applications.
 *
 * @see DefaultServerFactory
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultServerFactory.class)
public interface ServerFactory extends Discoverable {
    /**
     * Build a server for the given Alakazam application.
     *
     * @param environment the application's environment
     * @return a {@link Server} running the Alakazam application
     */
    Server build(Environment environment);
}
