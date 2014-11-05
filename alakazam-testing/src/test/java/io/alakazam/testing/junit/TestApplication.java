package io.alakazam.testing.junit;

import io.alakazam.Application;
import io.alakazam.setup.Bootstrap;
import io.alakazam.setup.Environment;

public class TestApplication extends Application<TestConfiguration> {

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap) {
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new TestResource(configuration.getMessage()));
    }
}
