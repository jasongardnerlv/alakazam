package io.alakazam.jackson;

import org.apache.logging.log4j.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class Log4jModuleTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        mapper.registerModule(new Log4jModule());
    }

    @Test
    public void mapsStringsToLevels() throws Exception {
        assertThat(mapper.readValue("\"info\"", Level.class))
                .isEqualTo(Level.INFO);
    }

    @Test
    public void mapsFalseToOff() throws Exception {
        assertThat(mapper.readValue("\"false\"", Level.class))
                .isEqualTo(Level.OFF);
    }

    @Test
    public void mapsTrueToAll() throws Exception {
        assertThat(mapper.readValue("\"true\"", Level.class))
                .isEqualTo(Level.ALL);
    }
}
