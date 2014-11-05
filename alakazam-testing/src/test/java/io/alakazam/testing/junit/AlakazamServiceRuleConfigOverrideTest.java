package io.alakazam.testing.junit;

import com.sun.jersey.api.client.Client;
import org.junit.ClassRule;
import org.junit.Test;

import static io.alakazam.testing.junit.ConfigOverride.config;
import static io.alakazam.testing.junit.AlakazamAppRuleTest.resourceFilePath;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AlakazamServiceRuleConfigOverrideTest {

    @ClassRule
    public static final AlakazamAppRule<TestConfiguration> RULE =
            new AlakazamAppRule<TestConfiguration>(TestApplication.class,
                                                     resourceFilePath("test-config.yaml"),
                                                     config("message", "A new way to say Hooray!"));

    @Test
    public void supportsConfigAttributeOverrides() {
        final String content = new Client().resource("http://localhost:" + RULE.getLocalPort() + "/test")
                                           .get(String.class);

        assertThat(content, is("A new way to say Hooray!"));
    }
}
