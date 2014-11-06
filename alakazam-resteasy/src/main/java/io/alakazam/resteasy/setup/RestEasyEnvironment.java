package io.alakazam.resteasy.setup;

import com.google.common.base.Function;
import io.alakazam.jetty.MutableServletContextHandler;
import io.alakazam.resteasy.AlakazamResourceConfig;
import org.jboss.resteasy.spi.Registry;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class RestEasyEnvironment {
    private final RestEasyContainerHolder holder;
    private final MutableServletContextHandler servletContext;
    private String urlPattern;

    public RestEasyEnvironment(RestEasyContainerHolder holder,
                                String urlPattern,
                                MutableServletContextHandler servletContext) {
        this.holder = holder;
        this.urlPattern = urlPattern;
        this.servletContext = servletContext;
    }

    public void disable() {
        holder.setContainer(null);
    }

    // TODO
    // public void replace(Function<ResourceConfig, ServletContainer> replace) {
    //     holder.setContainer(replace.apply(config));
    // }

    /**
     * Adds the given object as a RestEasy singleton component.
     *
     * @param component a RestEasy singleton component
     */
    public void register(Object component) {
        Registry registry = ((Registry)servletContext.getAttribute(Registry.class.getName()));
        if (registry != null) {
            registry.addSingletonResource(checkNotNull(component));
        } else {
            AlakazamResourceConfig.addSingleton(checkNotNull(component));
        }
    }

    /**
     * Adds the given class as a RestEasy component.
     *
     * @param componentClass a RestEasy component class
     */
    public void register(Class<?> componentClass) {
        Registry registry = ((Registry)servletContext.getAttribute(Registry.class.getName()));
        if (registry != null) {
            registry.addPerRequestResource(checkNotNull(componentClass));
        } else {
            AlakazamResourceConfig.addClass(checkNotNull(componentClass));
        }
    }

    // TODO
    // /**
    //  * Adds array of package names which will be used to scan for components. Packages will be
    //  * scanned recursively, including all nested packages.
    //  *
    //  * @param packages array of package names
    //  */
    // public void packages(String... packages) {
    //     config.init(new PackageNamesScanner(checkNotNull(packages)));
    // }

    //TODO
    // /**
    //  * Enables the resteasy feature with the given name.
    //  *
    //  * @param featureName the name of the feature to be enabled
    //  * @see com.sun.resteasy.api.core.ResourceConfig
    //  */
    // public void enable(String featureName) {
    //     config.getFeatures().put(checkNotNull(featureName), Boolean.TRUE);
    // }

    //TODO
    // /**
    //  * Disables the resteasy feature with the given name.
    //  *
    //  * @param featureName the name of the feature to be disabled
    //  * @see com.sun.resteasy.api.core.ResourceConfig
    //  */
    // public void disable(String featureName) {
    //     config.getFeatures().put(checkNotNull(featureName), Boolean.FALSE);
    // }

    // TODO
    // /**
    //  * Sets the given resteasy property.
    //  *
    //  * @param name  the name of the resteasy property
    //  * @param value the value of the resteasy property
    //  * @see com.sun.resteasy.api.core.ResourceConfig
    //  */
    // public void property(String name, @Nullable Object value) {
    //     config.getProperties().put(checkNotNull(name), value);
    // }

    //TODO
    // /**
    //  * Gets the given resteasy property.
    //  *
    //  * @param name the name of the resteasy property
    //  * @see com.sun.resteasy.api.core.ResourceConfig
    //  */
    // @SuppressWarnings("unchecked")
    // public <T> T getProperty(String name) {
    //     return (T) config.getProperties().get(name);
    // }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

}
