package io.alakazam.jaxws;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.junit.Test;

import javax.xml.ws.handler.Handler;

import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ClientBuilderTest {

    @Test
    public void buildClient() {

        Handler handler = mock(Handler.class);

        Interceptor<? extends Message> inInterceptor = mock(Interceptor.class);
        Interceptor<? extends Message> inFaultInterceptor = mock(Interceptor.class);
        Interceptor<? extends Message> outInterceptor = mock(Interceptor.class);
        Interceptor<? extends Message> outFaultInterceptor = mock(Interceptor.class);

        ClientBuilder<Object> builder = new ClientBuilder<>(Object.class, "address")
                .connectTimeout(1234)
                .receiveTimeout(5678)
                .handlers(handler, handler)
                .cxfInInterceptors(inInterceptor, inInterceptor)
                .cxfInFaultInterceptors(inFaultInterceptor, inFaultInterceptor)
                .cxfOutInterceptors(outInterceptor, outInterceptor)
                .cxfOutFaultInterceptors(outFaultInterceptor, outFaultInterceptor);

        assertThat(builder.getAddress(), equalTo("address"));
        assertThat(builder.getServiceClass(), equalTo(Object.class));
        assertThat(builder.getConnectTimeout(), equalTo(1234));
        assertThat(builder.getReceiveTimeout(), equalTo(5678));
//        assertThat(builder.getCxfInInterceptors(), contains(inInterceptor, inInterceptor));
//        assertThat(builder.getCxfInFaultInterceptors(), contains(inFaultInterceptor, inFaultInterceptor));
//        assertThat(builder.getCxfOutInterceptors(), contains(outInterceptor, outInterceptor));
//        assertThat(builder.getCxfOutFaultInterceptors(), contains(outFaultInterceptor, outFaultInterceptor));
    }
}
