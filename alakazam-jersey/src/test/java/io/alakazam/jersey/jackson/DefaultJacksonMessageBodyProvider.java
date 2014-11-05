package io.alakazam.jersey.jackson;

import io.alakazam.jackson.Jackson;

import javax.validation.Validation;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultJacksonMessageBodyProvider extends JacksonMessageBodyProvider {
    public DefaultJacksonMessageBodyProvider() {
        super(Jackson.newObjectMapper(), Validation.buildDefaultValidatorFactory().getValidator());
    }
}
