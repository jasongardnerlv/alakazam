package io.alakazam.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class DummyService {

    @WebMethod
    public void foo() {
    }
}
