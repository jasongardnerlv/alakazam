package io.alakazam.configuration;

import javax.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ConfigurationFactoryFactory<T> {
    public ConfigurationFactory<T> create(Class<T> klass,
            Validator validator,
            ObjectMapper objectMapper,
            String propertyPrefix);
}
