package io.alakazam.jersey.errors;

import javax.ws.rs.ext.Provider;

@Provider
public class DefaultLoggingExceptionMapper extends LoggingExceptionMapper<Throwable> {
}
