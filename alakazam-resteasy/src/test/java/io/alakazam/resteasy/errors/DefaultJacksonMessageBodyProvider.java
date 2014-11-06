package io.alakazam.resteasy.errors;

import io.alakazam.jackson.Jackson;
import io.alakazam.resteasy.jackson.JacksonMessageBodyProvider;

import javax.validation.Validation;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultJacksonMessageBodyProvider extends JacksonMessageBodyProvider {
    public DefaultJacksonMessageBodyProvider() {
        super(Jackson.newObjectMapper(), Validation.buildDefaultValidatorFactory().getValidator());
    }
}
