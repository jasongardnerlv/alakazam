package io.alakazam.jetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.alakazam.configuration.ConfigurationFactory;
import io.alakazam.jackson.Jackson;
import io.alakazam.logging.ConsoleAppenderFactory;
import io.alakazam.logging.FileAppenderFactory;
import io.alakazam.logging.SyslogAppenderFactory;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import java.io.File;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class RequestLogFactoryTest {
    private RequestLogFactory requestLog;

    @Before
    public void setUp() throws Exception {
        final ObjectMapper objectMapper = Jackson.newObjectMapper();
        objectMapper.getSubtypeResolver().registerSubtypes(ConsoleAppenderFactory.class,
                                                           FileAppenderFactory.class,
                                                           SyslogAppenderFactory.class);
        this.requestLog = new ConfigurationFactory<>(RequestLogFactory.class,
                                                     Validation.buildDefaultValidatorFactory()
                                                                       .getValidator(),
                                                     objectMapper, "alkzm")
                .build(new File(Resources.getResource("yaml/requestLog.yml").toURI()));
    }

    @Test
    public void defaultTimeZoneIsUTC() {
        assertThat(requestLog.getTimeZone())
            .isEqualTo(TimeZone.getTimeZone("UTC"));
    }
}
