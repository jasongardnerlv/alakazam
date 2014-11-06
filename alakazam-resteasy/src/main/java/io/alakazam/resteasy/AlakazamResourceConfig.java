package io.alakazam.resteasy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import io.alakazam.resteasy.errors.LoggingExceptionMapper;
import io.alakazam.resteasy.jackson.JsonProcessingExceptionMapper;
import io.alakazam.resteasy.validation.ConstraintViolationExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlakazamResourceConfig extends Application {
    private static final String NEWLINE = String.format("%n");
    private static final Logger LOGGER = LoggerFactory.getLogger(AlakazamResourceConfig.class);
    private String urlPattern;
    private final Set<Class<?>> classes = Sets.newHashSet();
    private final Set<Object> singletons = Sets.newHashSet();

    public AlakazamResourceConfig() {
        super();
        urlPattern = "/*";
        getSingletons().add(new LoggingExceptionMapper<Throwable>() {});
        getSingletons().add(new ConstraintViolationExceptionMapper());
        getSingletons().add(new JsonProcessingExceptionMapper());
    }

    // TODO
    // public void logAll() {
    //     logResources();
    //     logProviders();
    //     logEndpoints();
    // }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public Set<Class<?>> getClasses() {
        return classes;
    }

    public Set<Object> getSingletons() {
        return singletons;
    }

    // TODO
    // private void logResources() {
    //     final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

    //     for (Class<?> klass : getClasses()) {
    //         if (ResourceConfig.isRootResourceClass(klass)) {
    //             builder.add(klass.getCanonicalName());
    //         }
    //     }

    //     for (Object o : getSingletons()) {
    //         if (ResourceConfig.isRootResourceClass(o.getClass())) {
    //             builder.add(o.getClass().getCanonicalName());
    //         }
    //     }

    //     for (Object o : getExplicitRootResources().values()) {
    //         if (o instanceof Class) {
    //             builder.add(((Class<?>)o).getCanonicalName());
    //         } else {
    //             builder.add(o.getClass().getCanonicalName());
    //         }
    //     }

    //     LOGGER.debug("resources = {}", builder.build());
    // }

    // TODO
    // private void logProviders() {
    //     final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

    //     for (Class<?> klass : getClasses()) {
    //         if (ResourceConfig.isProviderClass(klass)) {
    //             builder.add(klass.getCanonicalName());
    //         }
    //     }

    //     for (Object o : getSingletons()) {
    //         if (ResourceConfig.isProviderClass(o.getClass())) {
    //             builder.add(o.getClass().getCanonicalName());
    //         }
    //     }

    //     LOGGER.debug("providers = {}", builder.build());
    // }

    // TODO
    // private void logEndpoints() {
    //     final StringBuilder msg = new StringBuilder(1024);
    //     msg.append("The following paths were found for the configured resources:");
    //     msg.append(NEWLINE).append(NEWLINE);

    //     final ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();

    //     for (Object o : getSingletons()) {
    //         if (ResourceConfig.isRootResourceClass(o.getClass())) {
    //             builder.add(o.getClass());
    //         }
    //     }
    //     for (Class<?> klass : getClasses()) {
    //         if (ResourceConfig.isRootResourceClass(klass)) {
    //             builder.add(klass);
    //         }
    //     }

    //     String rootPath = urlPattern;
    //     if (rootPath.endsWith("/*")) {
    //         rootPath = rootPath.substring(0, rootPath.length() - 1);
    //     }

    //     for (Class<?> klass : builder.build()) {
    //         final List<String> endpoints = Lists.newArrayList();
    //         populateEndpoints(endpoints, rootPath, klass, false);

    //         for (String line : Ordering.natural().sortedCopy(endpoints)) {
    //             msg.append(line).append(NEWLINE);
    //         }
    //     }
    //     for (Map.Entry<String, Object> entry : getExplicitRootResources().entrySet()) {
    //         final Class<?> klass  = entry.getValue() instanceof Class ?
    //                 (Class<?>) entry.getValue() :
    //                 entry.getValue().getClass();
    //         final AbstractResource resource =
    //                 new AbstractResource(entry.getKey(),
    //                                      IntrospectionModeller.createResource(klass));

    //         final List<String> endpoints = Lists.newArrayList();
    //         populateEndpoints(endpoints, rootPath, klass, false, resource);

    //         for (String line : Ordering.natural().sortedCopy(endpoints)) {
    //             msg.append(line).append(NEWLINE);
    //         }
    //     }

    //     LOGGER.info(msg.toString());
    // }

    // TODO
    // private void populateEndpoints(List<String> endpoints, String basePath, Class<?> klass,
    //                                boolean isLocator) {
    //     populateEndpoints(endpoints, basePath, klass, isLocator, IntrospectionModeller.createResource(klass));
    // }

    // TODO
    // private void populateEndpoints(List<String> endpoints, String basePath, Class<?> klass,
    //                                boolean isLocator, AbstractResource resource) {
        // if (!isLocator) {
        //     basePath = normalizePath(basePath, resource.getPath().getValue());
        // }

        // for (AbstractResourceMethod method : resource.getResourceMethods()) {
        //     endpoints.add(formatEndpoint(method.getHttpMethod(), basePath, klass));
        // }

        // for (AbstractSubResourceMethod method : resource.getSubResourceMethods()) {
        //     final String path = normalizePath(basePath, method.getPath().getValue());
        //     endpoints.add(formatEndpoint(method.getHttpMethod(), path, klass));
        // }

        // for (AbstractSubResourceLocator locator : resource.getSubResourceLocators()) {
        //     final String path = normalizePath(basePath, locator.getPath().getValue());
        //     populateEndpoints(endpoints, path, locator.getMethod().getReturnType(), true);
        // }
    // }

    // private String formatEndpoint(String method, String path, Class<?> klass) {
    //     return String.format("    %-7s %s (%s)", method, path, klass.getCanonicalName());
    // }

    // private String normalizePath(String basePath, String path) {
    //     if (basePath.endsWith("/")) {
    //         return path.startsWith("/") ? basePath + path.substring(1) : basePath + path;
    //     }
    //     return path.startsWith("/") ? basePath + path : basePath + "/" + path;
    // }
}
