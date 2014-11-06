package io.alakazam.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import io.alakazam.resteasy.AlakazamResourceConfig;
import io.alakazam.resteasy.setup.RestEasyContainerHolder;
import io.alakazam.resteasy.setup.RestEasyEnvironment;
import io.alakazam.jetty.MutableServletContextHandler;
import io.alakazam.jetty.setup.ServletEnvironment;
import io.alakazam.lifecycle.setup.LifecycleEnvironment;

import javax.validation.Validator;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO: 5/15/13 <coda> -- add tests for Environment

/**
 * A Alakazam application's environment.
 */
public class Environment {
    private final String name;

    private final ObjectMapper objectMapper;
    private Validator validator;

    private final RestEasyContainerHolder restEasyServletContainer;
    private final RestEasyEnvironment restEasyEnvironment;

    private final MutableServletContextHandler servletContext;
    private final ServletEnvironment servletEnvironment;

    private final LifecycleEnvironment lifecycleEnvironment;

    /**
     * Creates a new environment.
     *
     * @param name                the name of the application
     * @param objectMapper the {@link ObjectMapper} for the application
     */
    public Environment(String name,
                       ObjectMapper objectMapper,
                       Validator validator,
                       ClassLoader classLoader) {
        this.name = name;
        this.objectMapper = objectMapper;
        this.validator = validator;

        this.servletContext = new MutableServletContextHandler();
        servletContext.setClassLoader(classLoader);
        this.servletEnvironment = new ServletEnvironment(servletContext);

        this.lifecycleEnvironment = new LifecycleEnvironment();

        final AlakazamResourceConfig restEasyConfig = new AlakazamResourceConfig();
        this.restEasyServletContainer = new RestEasyContainerHolder(new HttpServletDispatcher());
        this.restEasyEnvironment = new RestEasyEnvironment(restEasyServletContainer, restEasyConfig);
    }

    /**
     * Returns the application's {@link RestEasyEnvironment}.
     */
    public RestEasyEnvironment resteasy() {
        return restEasyEnvironment;
    }


    /**
     * Returns the application's {@link LifecycleEnvironment}.
     */
    public LifecycleEnvironment lifecycle() {
        return lifecycleEnvironment;
    }

    /**
     * Returns the application's {@link ServletEnvironment}.
     */
    public ServletEnvironment servlets() {
        return servletEnvironment;
    }

    /**
     * Returns the application's {@link ObjectMapper}.
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Returns the application's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the application's {@link Validator}.
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * Sets the application's {@link Validator}.
     */
    public void setValidator(Validator validator) {
        this.validator = checkNotNull(validator);
    }

    /*
    * Internal Accessors
    */

    // TODO: 5/4/13 <coda> -- figure out how to make these accessors not a public API

    public MutableServletContextHandler getApplicationContext() {
        return servletContext;
    }

    public HttpServletDispatcher getRestEasyServletContainer() {
        return restEasyServletContainer.getContainer();
    }

}
