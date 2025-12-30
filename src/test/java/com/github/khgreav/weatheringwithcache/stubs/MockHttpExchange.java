package com.github.khgreav.weatheringwithcache.stubs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class MockHttpExchange extends HttpExchange {

    private final Headers requestHeaders = new Headers();

    @Override
    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("Unimplemented method 'getAttribute'");
    }

    @Override
    public HttpContext getHttpContext() {
        throw new UnsupportedOperationException("Unimplemented method 'getHttpContext'");
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        throw new UnsupportedOperationException("Unimplemented method 'getLocalAddress'");
    }

    @Override
    public HttpPrincipal getPrincipal() {
        throw new UnsupportedOperationException("Unimplemented method 'getPrincipal'");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Unimplemented method 'getProtocol'");
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        throw new UnsupportedOperationException("Unimplemented method 'getRemoteAddress'");
    }

    @Override
    public InputStream getRequestBody() {
        throw new UnsupportedOperationException("Unimplemented method 'getRequestBody'");
    }

    @Override
    public String getRequestMethod() {
        throw new UnsupportedOperationException("Unimplemented method 'getRequestMethod'");
    }

    @Override
    public URI getRequestURI() {
        throw new UnsupportedOperationException("Unimplemented method 'getRequestURI'");
    }

    @Override
    public OutputStream getResponseBody() {
        throw new UnsupportedOperationException("Unimplemented method 'getResponseBody'");
    }

    @Override
    public int getResponseCode() {
        throw new UnsupportedOperationException("Unimplemented method 'getResponseCode'");
    }

    @Override
    public Headers getResponseHeaders() {
        throw new UnsupportedOperationException("Unimplemented method 'getResponseHeaders'");
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'sendResponseHeaders'");
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException("Unimplemented method 'setAttribute'");
    }

    @Override
    public void setStreams(InputStream i, OutputStream o) {
        throw new UnsupportedOperationException("Unimplemented method 'setStreams'");
    }
    
}
