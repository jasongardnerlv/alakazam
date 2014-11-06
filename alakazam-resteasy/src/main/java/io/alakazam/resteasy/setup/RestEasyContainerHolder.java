package io.alakazam.resteasy.setup;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class RestEasyContainerHolder {
    private HttpServletDispatcher container;

    public RestEasyContainerHolder(HttpServletDispatcher container) {
        this.container = container;
    }

    public HttpServletDispatcher getContainer() {
        return container;
    }

    public void setContainer(HttpServletDispatcher container) {
        this.container = container;
    }
}
