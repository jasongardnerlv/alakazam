package io.alakazam.resteasy.setup;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import io.alakazam.jetty.MutableServletContextHandler;
import io.alakazam.resteasy.AlakazamResourceConfig;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.core.ResourceMethodRegistry;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class RestEasyEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEasyEnvironment.class);
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

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void logEndpoints() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=========================  Registered REST Resources  =========================");
        for (Object obj : AlakazamResourceConfig.getAllSingletons()) {
            Class clazz = obj.getClass();
            String path = ((Path)clazz.getAnnotation(Path.class)).value();
            for (Method method : clazz.getMethods()) {
                for (String verb : getHttpMethods(method)) {
                    sb.append("\n" + String.format("%-7s %s (%s)", verb, path, clazz.getCanonicalName()));
                }
            }
        }
        sb.append("\n===============================================================================");
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

}
