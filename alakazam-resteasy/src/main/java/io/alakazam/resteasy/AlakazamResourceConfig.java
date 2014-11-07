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

    private static Set<Object> singletons = Sets.newHashSet();

    public static void addSingleton(Object obj) {
        singletons.add(obj);
    }

    public static void removeClass(Class<?> clazz) {
        for (Object singleton : singletons) {
            if (clazz.isAssignableFrom(singleton.getClass())) {
                singletons.remove(singleton);
            }
        }
    }

    public static Set<Object> getAllSingletons() {
        return singletons;
    }

    private String urlPattern;

    public AlakazamResourceConfig() {
        super();
        urlPattern = "/*";
        getSingletons().add(new LoggingExceptionMapper<Throwable>() {});
        getSingletons().add(new ConstraintViolationExceptionMapper());
        getSingletons().add(new JsonProcessingExceptionMapper());
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet();
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
