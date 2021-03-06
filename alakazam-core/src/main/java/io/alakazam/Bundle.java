package io.alakazam;

import io.alakazam.setup.Bootstrap;
import io.alakazam.setup.Environment;

/**
 * A reusable bundle of functionality, used to define blocks of application behavior.
 */
public interface Bundle {
    /**
     * Initializes the application bootstrap.
     *
     * @param bootstrap the application bootstrap
     */
    void initialize(Bootstrap<?> bootstrap);

    /**
     * Initializes the application environment.
     *
     * @param environment the application environment
     */
    void run(Environment environment);
}
