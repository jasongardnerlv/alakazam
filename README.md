Alakazam! [![Build Status](https://travis-ci.org/jasongardnerlv/alakazam.png?branch=master)](https://travis-ci.org/jasongardnerlv/alakazam)
==========

Web application server based on Jetty, RestEasy, and Jackson.

### Background

This project began as a fork of the **DropWizard** codebase.

http://www.dropwizard.io/

DropWizard is a great project, however some of the open source licenses used as
part of DW were an issue for a commercial project I was working on.  Alakazam
is the result of replacing some of those parts with more lenient equivalents.

Much deserved credit to Coda Hale and the DropWizard team.  If your
requirements allow, consider using DropWizard over Alakazam, where possible.

### Basic Usage

**Main Class**

```java
import io.alakazam.Application;
import io.alakazam.assets.ConfiguredAssetsBundle;
import io.alakazam.setup.Bootstrap;
import io.alakazam.setup.Environment;

public class MyMainClass extends Application<MyConfigClass> {

    public static void main(String[] args) throws Exception {
        new MyMainClass().run(args);
    }

    @Override
    public String getName() {
        return "my-web-server";
    }

    @Override
    public void initialize(Bootstrap<MyConfigClass> bootstrap) {
        //optionally register a Java resource directory to serve static files from
        bootstrap.addBundle(new ConfiguredAssetsBundle("/images/", "/images/"));
    }

    @Override
    public void run(MyConfigClass configuration, Environment environment) throws Exception {
        environment.resteasy().register(new MyRESTResource(), false);
    }

}
```

**Config Class**

```java
import io.alakazam.Configuration;
import io.alakazam.assets.AssetsBundleConfiguration;

public class MyConfigClass extends Configuration implements AssetsBundleConfiguration {

    @JsonProperty("myConfig")
    private String myConfig;

    public String getMyConfig() {
        return myConfig;
    }
}
```

**Config File (server-config.yml)**

```yaml
myConfig: "myConfigValue"

```

**REST Resource**

```java
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/myresource")
@Produces(MediaType.APPLICATION_JSON)
public class MyRESTResource {

    @GET
    public Response handleRequest() {
        String json = "{\"foo\":\"bar\"}";
        return Response
                .ok(json)
                .header("Content-Length", json.length())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=ISO-8859-15")
                .build();
    }

}
```

### Start the Server

```
java -cp <your-classpath> my.package.MyMainClass server server-config.yml
```