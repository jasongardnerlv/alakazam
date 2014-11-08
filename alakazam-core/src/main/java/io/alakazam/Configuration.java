package io.alakazam;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import io.alakazam.server.DefaultServerFactory;
import io.alakazam.server.ServerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * An object representation of the YAML configuration file. Extend this with your own configuration
 * properties, and they'll be parsed from the YAML file as well.
 * <p/>
 * For example, given a YAML file with this:
 * <pre>
 * name: "Random Person"
 * age: 43
 * # ... etc ...
 * </pre>
 * And a configuration like this:
 * <pre>
 * public class ExampleConfiguration extends Configuration {
 *     \@NotNull
 *     private String name;
 *
 *     \@Min(1)
 *     \@Max(120)
 *     private int age;
 *
 *     \@JsonProperty
 *     public String getName() {
 *         return name;
 *     }
 *
 *     \@JsonProperty
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 *
 *     \@JsonProperty
 *     public int getAge() {
 *         return age;
 *     }
 *
 *     \@JsonProperty
 *     public void setAge(int age) {
 *         this.age = age;
 *     }
 * }
 * </pre>
 * <p/>
 * Alakazam will parse the given YAML file and provide an {@code ExampleConfiguration} instance
 * to your application whose {@code getName()} method will return {@code "Random Person"} and whose
 * {@code getAge()} method will return {@code 43}.
 *
 * @see <a href="http://www.yaml.org/YAML_for_ruby.html">YAML Cookbook</a>
 */
public class Configuration {
    @Valid
    @NotNull
    private ServerFactory server = new DefaultServerFactory();

    /**
     * Returns the server-specific section of the configuration file.
     *
     * @return server-specific configuration parameters
     */
    @JsonProperty("server")
    public ServerFactory getServerFactory() {
        return server;
    }

    /**
     * Sets the HTTP-specific section of the configuration file.
     */
    @JsonProperty("server")
    public void setServerFactory(ServerFactory factory) {
        this.server = factory;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("server", server)
                      .toString();
    }
}
