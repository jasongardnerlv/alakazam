package io.alakazam.jaxws;

import io.alakazam.jetty.setup.ServletEnvironment;
import io.alakazam.lifecycle.ServerLifecycleListener;
import io.alakazam.lifecycle.setup.LifecycleEnvironment;
import io.alakazam.setup.Bootstrap;
import io.alakazam.setup.Environment;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class JAXWSBundleTest {

    Environment environment = mock(Environment.class);
    Bootstrap bootstrap = mock(Bootstrap.class);
    ServletEnvironment servletEnvironment = mock(ServletEnvironment.class);
    ServletRegistration.Dynamic servlet = mock(ServletRegistration.Dynamic.class);
    JAXWSEnvironment jaxwsEnvironment = mock(JAXWSEnvironment.class);
    LifecycleEnvironment lifecycleEnvironment = mock(LifecycleEnvironment.class);

    @Before
    public void setUp() {
        when(environment.servlets()).thenReturn(servletEnvironment);
        when(environment.lifecycle()).thenReturn(lifecycleEnvironment);
        when(servletEnvironment.addServlet(anyString(), any(HttpServlet.class))).thenReturn(servlet);
        when(jaxwsEnvironment.buildServlet()).thenReturn(mock(HttpServlet.class));
        when(jaxwsEnvironment.getDefaultPath()).thenReturn("/soap");
    }

    @Test
    public void constructorArgumentChecks() {
        try {
            new JAXWSBundle(null, null);
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        try {
            new JAXWSBundle("soap", null);
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }
    }

    @Test
    public void initializeAndRun() {
        JAXWSBundle jaxwsBundle = new JAXWSBundle("/soap", jaxwsEnvironment);

        try {
            jaxwsBundle.run(null);
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        jaxwsBundle.initialize(bootstrap);

        jaxwsBundle.run(environment);
        verify(servletEnvironment).addServlet(eq("CXF Servlet"), any(Servlet.class));
        verify(lifecycleEnvironment).addServerLifecycleListener(any(ServerLifecycleListener.class));
        verify(servlet).addMapping("/soap/*");
    }

    @Test
    public void publishEndpoint() {

        JAXWSBundle jaxwsBundle = new JAXWSBundle("/soap", jaxwsEnvironment);
        Object service = new Object();
        try {
            jaxwsBundle.publishEndpoint(new EndpointBuilder("foo", null));
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        try {
            jaxwsBundle.publishEndpoint(new EndpointBuilder(null, service));
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        try {
            jaxwsBundle.publishEndpoint(new EndpointBuilder("   ", service));
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        EndpointBuilder builder = mock(EndpointBuilder.class);
        jaxwsBundle.publishEndpoint(builder);
        verify(jaxwsEnvironment).publishEndpoint(builder);
    }

    @Test
    public void getClient() {

        JAXWSBundle jaxwsBundle = new JAXWSBundle("/soap", jaxwsEnvironment);

        Class<?> cls = Object.class;
        String url = "http://foo";

        try {
            jaxwsBundle.getClient(new ClientBuilder<>(null, null));
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        try {
            jaxwsBundle.getClient(new ClientBuilder<>(null, url));
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        try {
            jaxwsBundle.getClient(new ClientBuilder<>(cls, "   "));
            fail();
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

        ClientBuilder builder = new ClientBuilder<>(cls, url);
        jaxwsBundle.getClient(builder);
        verify(jaxwsEnvironment).getClient(builder);
    }
}
