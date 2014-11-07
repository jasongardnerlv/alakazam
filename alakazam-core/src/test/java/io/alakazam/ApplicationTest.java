package io.alakazam;

import io.alakazam.setup.Bootstrap;
import io.alakazam.setup.Environment;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ApplicationTest {
    private static class FakeConfiguration extends Configuration {}

    private static class FakeApplication extends Application<FakeConfiguration> {
        @Override
        public void initialize(Bootstrap<FakeConfiguration> bootstrap) {}

        @Override
        public void run(FakeConfiguration configuration, Environment environment) {}

        @Override
        public void serverStarted() {}
    }

    private static class PoserApplication extends FakeApplication {}

    private static class WrapperApplication<C extends FakeConfiguration> extends Application<C> {
        private final Application<C> application;

        private WrapperApplication(Application<C> application) {
            this.application = application;
        }

        @Override
        public void initialize(Bootstrap<C> bootstrap) {
            this.application.initialize(bootstrap);
        }

        @Override
        public void run(C configuration, Environment environment) throws Exception {
            this.application.run(configuration, environment);
        }

        @Override
        public void serverStarted() {

        }
    }

    @Test
    public void hasAReferenceToItsTypeParameter() throws Exception {
        assertThat(new FakeApplication().getConfigurationClass())
                .isSameAs(FakeConfiguration.class);
    }

    @Test
    public void canDetermineConfiguration() throws Exception {
        assertThat(new PoserApplication().getConfigurationClass())
                .isSameAs(FakeConfiguration.class);
    }

    @Test
    public void canDetermineWrappedConfiguration() throws Exception {
        final PoserApplication application = new PoserApplication();
        assertThat(new WrapperApplication<>(application).getConfigurationClass())
                .isSameAs(FakeConfiguration.class);
    }
}
