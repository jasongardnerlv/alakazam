package io.alakazam.testing.app;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import io.alakazam.testing.junit.AlakazamAppRule;
import io.alakazam.testing.junit.AlakazamAppRuleTest;
import io.alakazam.testing.junit.TestApplication;
import io.alakazam.testing.junit.TestConfiguration;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.ACCEPT_ENCODING;
import static javax.ws.rs.core.HttpHeaders.CONTENT_ENCODING;
import static javax.ws.rs.core.HttpHeaders.VARY;
import static org.fest.assertions.api.Assertions.assertThat;

public class GzipDefaultVaryBehaviourTest {

    @ClassRule
    public static final AlakazamAppRule<TestConfiguration> RULE =
            new AlakazamAppRule<>(TestApplication.class, AlakazamAppRuleTest.resourceFilePath("test-config.yaml"));

    @Test
    public void testDefaultVaryHeader() {
        final ClientResponse clientResponse = new Client().resource("http://localhost:" +
                RULE.getLocalPort()
                +"/test")
                .header(ACCEPT_ENCODING, "gzip")
                .get(ClientResponse.class);

        assertThat(clientResponse.getHeaders().get(VARY)).isEqualTo(asList(ACCEPT_ENCODING));
        assertThat(clientResponse.getHeaders().get(CONTENT_ENCODING)).isEqualTo(asList("gzip"));
    }
}
