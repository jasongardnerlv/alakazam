package io.alakazam.resteasy;

import io.alakazam.resteasy.dummy.DummyResource;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class AlakazamResourceConfigTest {
    // static {
    //     LoggingFactory.bootstrap();
    // }

    // @Test
    // public void findsResourceClassInPackage() {
    //     final AlakazamResourceConfig rc = AlakazamResourceConfig.forTesting(new MetricRegistry());
    //     rc.init(new PackageNamesScanner(new String[] { DummyResource.class.getPackage().getName() }));

    //     assertThat(rc.getRootResourceClasses())
    //             .containsOnly(DummyResource.class);
    // }

    // @Test
    // public void findsResourceClassesInPackageAndSubpackage() {
    //     final AlakazamResourceConfig rc = AlakazamResourceConfig.forTesting(new MetricRegistry());
    //     rc.init(new PackageNamesScanner(new String[] { getClass().getPackage().getName() }));

    //     assertThat(rc.getRootResourceClasses())
    //             .contains
    //                     (DummyResource.class, TestResource.class);
    // }

    @Path("/dummy")
    public static class TestResource {
        @GET
        public String foo() {
            return "bar";
        }
    }
}
