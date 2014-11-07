package io.alakazam.resteasy.setup;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import io.alakazam.resteasy.AlakazamResourceConfig;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.spi.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import java.util.Enumeration;

import static com.google.common.base.Preconditions.checkNotNull;

public class RestEasyEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEasyEnvironment.class);
    private final RestEasyContainerHolder holder;
    private String urlPattern;

    public RestEasyEnvironment(RestEasyContainerHolder holder, String urlPattern) {
        this.holder = holder;
        this.urlPattern = urlPattern;
    }

    public void disable() {
        holder.setContainer(null);
    }

    /**
     * Adds the given object as a RestEasy singleton component.
     *
     * @param component a RestEasy singleton component
     */
    public void register(Object component) {
        Dispatcher dispatcher = getResteasyDispatcher();
        Registry registry = null;
        if (dispatcher != null) {
            registry = dispatcher.getRegistry();
        }
        AlakazamResourceConfig.addSingleton(checkNotNull(component));
        if (registry != null) {
            registry.addSingletonResource(checkNotNull(component));
        }
    }

    /**
     * Removes all instances of the given class
     *
     * @param componentClass a RestEasy componentClass
     */
    public void unregister(Class<?> componentClass) {
        Dispatcher dispatcher = getResteasyDispatcher();
        Registry registry = null;
        if (dispatcher != null) {
            registry = dispatcher.getRegistry();
        }
        AlakazamResourceConfig.removeClass(checkNotNull(componentClass));
        if (registry != null) {
            registry.removeRegistrations(checkNotNull(componentClass));
        }
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public Set<Object> getResources() {
        return AlakazamResourceConfig.getAllSingletons();
    }

    public void logEndpoints() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n=========================  Registered REST Resources  =========================");
        for (Object obj : getResources()) {
            Class clazz = obj.getClass();
            String path = ((Path)clazz.getAnnotation(Path.class)).value();
            path = (path.startsWith("/")) ? path.substring(1) : path;
            for (Method method : clazz.getMethods()) {
                for (String verb : getHttpMethods(method)) {
                    sb.append("\n" + String.format("%-7s %s (%s)", verb, path, clazz.getCanonicalName()));
                }
            }
        }
        sb.append("\n===============================================================================\n");
        LOGGER.info(sb.toString());
    }

    private Set<String> getHttpMethods(Method method) {
        Set<String> methods = new HashSet<String>();
        for (Annotation annotation : method.getAnnotations()){
            HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (http != null) {
                methods.add(http.value());
            }
        }
        return methods;
    }

    private Dispatcher getResteasyDispatcher() {
        try {
            return holder.getContainer().getDispatcher();
        } catch (Exception e) {
            //not yet initialized
            return null;
        }
    }

}
