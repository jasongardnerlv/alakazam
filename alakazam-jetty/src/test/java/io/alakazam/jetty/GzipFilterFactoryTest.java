package io.alakazam.jetty;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import io.alakazam.configuration.ConfigurationFactory;
import io.alakazam.jackson.Jackson;
import io.alakazam.util.Size;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

public class GzipFilterFactoryTest {
    private GzipFilterFactory gzip;

    @Before
    public void setUp() throws Exception {
        this.gzip = new ConfigurationFactory<>(GzipFilterFactory.class,
                                               Validation.buildDefaultValidatorFactory()
                                                                 .getValidator(),
                                               Jackson.newObjectMapper(), "alkzm")
                .build(new File(Resources.getResource("yaml/gzip.yml").toURI()));
    }

    @Test
    public void canBeEnabled() throws Exception {
        assertThat(gzip.isEnabled())
                .isFalse();
    }

    @Test
    public void hasAMinimumEntitySize() throws Exception {
        assertThat(gzip.getMinimumEntitySize())
                .isEqualTo(Size.kilobytes(12));
    }

    @Test
    public void hasABufferSize() throws Exception {
        assertThat(gzip.getBufferSize())
                .isEqualTo(Size.kilobytes(32));
    }

    @Test
    public void hasExcludedUserAgents() throws Exception {
        assertThat(gzip.getExcludedUserAgents())
                .isEqualTo(ImmutableSet.of("IE"));
    }

    @Test
    public void hasCompressedMimeTypes() throws Exception {
        assertThat(gzip.getCompressedMimeTypes())
                .isEqualTo(ImmutableSet.of("text/plain"));
    }

    @Test
    public void varyIsOnlyForAcceptEncoding() throws Exception {
        assertThat(gzip.getVary())
                .isEqualTo("Accept-Encoding");
    }
}
