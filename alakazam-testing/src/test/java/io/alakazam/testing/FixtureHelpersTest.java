package io.alakazam.testing;

import org.junit.Test;

import static io.alakazam.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;

public class FixtureHelpersTest {
    @Test
    public void readsTheFileAsAString() throws Exception {
        assertThat(fixture("fixtures/fixture.txt"))
                .isEqualTo("YAY FOR ME");
    }
}
