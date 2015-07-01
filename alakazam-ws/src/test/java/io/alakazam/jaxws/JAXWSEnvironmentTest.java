package io.alakazam.jaxws;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.test.TestUtilities;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.local.LocalTransportFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPBinding;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class JAXWSEnvironmentTest {

    private JAXWSEnvironment jaxwsEnvironment;
    private Invoker mockInvoker = mock(Invoker.class);
    private TestUtilities testutils = new TestUtilities(JAXWSEnvironmentTest.class);
    private DummyService service = new DummyService();

    private String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:res=\"http://jaxws.alakazam.io/\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
            "<res:foo></res:foo>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    // DummyInterface is used by getClient test
    @WebService
    interface DummyInterface {
        @WebMethod
        @SuppressWarnings("unused")
        public void foo();
    }

    // TestInterceptor is used for testing CXF interceptors
    class TestInterceptor extends AbstractPhaseInterceptor<Message> {
        private int invocationCount = 0;

        public TestInterceptor(String phase) {
            super(phase);
        }

        public int getInvocationCount() {
            return this.invocationCount;
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            invocationCount++;
        }
    }

    @Before
    public void setup() {
        jaxwsEnvironment = new JAXWSEnvironment("soap") {
        };

        testutils.setBus(jaxwsEnvironment.bus);
        testutils.addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        testutils.addNamespace("a", "http://jaxws.alakazam.io/");
    }

    @After
    public void teardown() {
        jaxwsEnvironment.bus.shutdown(false);
    }

    @Test
    public void buildServlet() {
        Object result = jaxwsEnvironment.buildServlet();
        assertThat(result, is(instanceOf(CXFNonSpringServlet.class)));
        assertThat(((CXFNonSpringServlet) result).getBus(), is(instanceOf(Bus.class)));
    }

    @Test
    public void publishEndpoint() throws Exception {

        jaxwsEnvironment.publishEndpoint(new EndpointBuilder("local://path", service));

        Node soapResponse = testutils.invoke("local://path",
                LocalTransportFactory.TRANSPORT_ID, soapRequest.getBytes());

        //        verify(mockInvoker).invoke(any(Exchange.class), any());

        testutils.assertValid("/soap:Envelope/soap:Body/a:fooResponse", soapResponse);
    }

    @Test
    public void publishEndpointWithCxfInterceptors() throws Exception {

        TestInterceptor inInterceptor = new TestInterceptor(Phase.UNMARSHAL);
        TestInterceptor inInterceptor2 = new TestInterceptor(Phase.PRE_INVOKE);
        TestInterceptor outInterceptor = new TestInterceptor(Phase.MARSHAL);

        jaxwsEnvironment.publishEndpoint(
                new EndpointBuilder("local://path", service)
                        .cxfInInterceptors(inInterceptor, inInterceptor2)
                        .cxfOutInterceptors(outInterceptor));

        Node soapResponse = testutils.invoke("local://path",
                LocalTransportFactory.TRANSPORT_ID, soapRequest.getBytes());

        //        verify(mockInvoker).invoke(any(Exchange.class), any());
        assertThat(inInterceptor.getInvocationCount(), equalTo(1));
        assertThat(inInterceptor2.getInvocationCount(), equalTo(1));
        assertThat(outInterceptor.getInvocationCount(), equalTo(1));

        testutils.assertValid("/soap:Envelope/soap:Body/a:fooResponse", soapResponse);

        soapResponse = testutils.invoke("local://path",
                LocalTransportFactory.TRANSPORT_ID, soapRequest.getBytes());

//        verify(mockInvoker, times(2)).invoke(any(Exchange.class), any());
        assertThat(inInterceptor.getInvocationCount(), equalTo(2));
        assertThat(inInterceptor2.getInvocationCount(), equalTo(2));
        assertThat(outInterceptor.getInvocationCount(), equalTo(2));

        testutils.assertValid("/soap:Envelope/soap:Body/a:fooResponse", soapResponse);
    }


    @Test
    public void publishEndpointWithMtom() throws Exception {

        jaxwsEnvironment.publishEndpoint(
                new EndpointBuilder("local://path", service)
                        .enableMtom());

        byte[] response = testutils.invokeBytes("local://path", LocalTransportFactory.TRANSPORT_ID, soapRequest.getBytes());

        //        verify(mockInvoker).invoke(any(Exchange.class), any());

        MimeMultipart mimeMultipart = new MimeMultipart(new ByteArrayDataSource(response,
                "application/xop+xml; charset=UTF-8; type=\"text/xml\""));
        assertThat(mimeMultipart.getCount(), equalTo(1));
        testutils.assertValid("/soap:Envelope/soap:Body/a:fooResponse",
                StaxUtils.read(mimeMultipart.getBodyPart(0).getInputStream()));
    }

    @Test
    public void publishEndpointWithInvalidArguments() throws Exception {

        try {
            jaxwsEnvironment.publishEndpoint(new EndpointBuilder("foo", null));
        } catch (IllegalArgumentException e) {
        }

        try {
            jaxwsEnvironment.publishEndpoint(new EndpointBuilder(null, service));
        } catch (IllegalArgumentException e) {
        }

        try {
            jaxwsEnvironment.publishEndpoint(new EndpointBuilder("   ", service));
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void getClient() {
        String address = "http://address";
        Handler handler = mock(Handler.class);

        // simple
        DummyInterface clientProxy = jaxwsEnvironment.getClient(
                new ClientBuilder<>(DummyInterface.class, address)
        );
        assertThat(clientProxy, is(instanceOf(Proxy.class)));

        Client c = ClientProxy.getClient(clientProxy);
        assertThat(c.getEndpoint().getEndpointInfo().getAddress(), equalTo(address));
        assertThat(c.getEndpoint().getService().get("endpoint.class").equals(DummyInterface.class), equalTo(true));
        assertThat(((BindingProvider) clientProxy).getBinding().getHandlerChain().size(), equalTo(0));

        HTTPClientPolicy httpclient = ((HTTPConduit) c.getConduit()).getClient();
        assertThat(httpclient.getConnectionTimeout(), equalTo(500L));
        assertThat(httpclient.getReceiveTimeout(), equalTo(2000L));

        // with timeouts, handlers, interceptors and MTOM

        TestInterceptor inInterceptor = new TestInterceptor(Phase.UNMARSHAL);
        TestInterceptor inInterceptor2 = new TestInterceptor(Phase.PRE_INVOKE);
        TestInterceptor outInterceptor = new TestInterceptor(Phase.MARSHAL);

        clientProxy = jaxwsEnvironment.getClient(
                new ClientBuilder<>(DummyInterface.class, address)
                        .connectTimeout(123)
                        .receiveTimeout(456)
                        .handlers(handler)
                        .cxfInInterceptors(inInterceptor, inInterceptor2)
                        .cxfOutInterceptors(outInterceptor)
                        .enableMtom());
        c = ClientProxy.getClient(clientProxy);
        assertThat(c.getEndpoint().getEndpointInfo().getAddress(), equalTo(address));
        assertThat(c.getEndpoint().getService().get("endpoint.class").equals(DummyInterface.class), equalTo(true));

        httpclient = ((HTTPConduit) c.getConduit()).getClient();
        assertThat(httpclient.getConnectionTimeout(), equalTo(123L));
        assertThat(httpclient.getReceiveTimeout(), equalTo(456L));

        //        assertThat(((BindingProvider)clientProxy).getBinding().getHandlerChain(), contains(handler));

        BindingProvider bp = (BindingProvider) clientProxy;
        SOAPBinding binding = (SOAPBinding) bp.getBinding();
        assertThat(binding.isMTOMEnabled(), equalTo(true));
    }
}
