package io.alakazam.testing.junit;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.alakazam.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class TestConfiguration extends Configuration {

    @JsonProperty
    @NotEmpty
    private String message;

    public String getMessage() {
        return message;
    }
}
