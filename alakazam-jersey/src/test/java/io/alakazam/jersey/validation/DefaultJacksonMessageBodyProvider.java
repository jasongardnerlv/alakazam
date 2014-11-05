package io.alakazam.jersey.validation;

import io.alakazam.jackson.Jackson;
import io.alakazam.jersey.jackson.JacksonMessageBodyProvider;

import javax.validation.Validation;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultJacksonMessageBodyProvider extends JacksonMessageBodyProvider {
    public DefaultJacksonMessageBodyProvider() {
        super(Jackson.newObjectMapper(), Validation.buildDefaultValidatorFactory().getValidator());
    }
}

