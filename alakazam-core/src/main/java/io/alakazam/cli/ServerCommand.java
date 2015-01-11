package io.alakazam.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import io.alakazam.Application;
import io.alakazam.Configuration;
import io.alakazam.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a application as an HTTP server.
 *
 * @param <T> the {@link Configuration} subclass which is loaded from the configuration file
 */
public class ServerCommand<T extends Configuration> extends EnvironmentCommand<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerCommand.class);

    private final Class<T> configurationClass;

    public ServerCommand(Application<T> application) {
        super(application, "server", "Runs the Alakazam application as an HTTP server");
        this.configurationClass = application.getConfigurationClass();
    }

    /*
     * Since we don't subclass ServerCommand, we need a concrete reference to the configuration
     * class.
     */
    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(Environment environment, Namespace namespace, T configuration) throws Exception {
        final Server server = configuration.getServerFactory().build(environment);
        try {
            writePID();
            server.addLifeCycleListener(new LifeCycleListener());
            cleanupAsynchronously();
            server.start();
            application.serverStarted();
        } catch (Exception e) {
            LOGGER.error("Unable to start server, shutting down", e);
            server.stop();
            cleanup();
            throw e;
        }
    }

    private class LifeCycleListener extends AbstractLifeCycle.AbstractLifeCycleListener {
        @Override
        public void lifeCycleStopped(LifeCycle event) {
            cleanup();
            removePID();
        }
    }

    private final void writePID() throws Exception {
        String procId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        File pidfile = new File(System.getProperty("user.dir"), application.getName() + ".pid");
        BufferedWriter bw = null;
        try {
            bw = Files.newBufferedWriter(Paths.get(pidfile.toURI()), Charset.defaultCharset());
        } catch (Exception e) {
            throw e;
        }
        bw.write(procId, 0, procId.length());
        bw.close();
    }

    private final void removePID() {
        File pidfile = new File(System.getProperty("user.dir"), application.getName() + ".pid");
        try {
            pidfile.delete();
        } catch (Exception e) {
            /*ignored*/
        }
    }
}
