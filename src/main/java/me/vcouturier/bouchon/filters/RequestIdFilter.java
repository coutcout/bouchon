package me.vcouturier.bouchon.filters;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = {
        "/*"
})
public class RequestIdFilter extends HttpFilter {

    public static final String REQUEST_ID_HEADER = "request-id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ThreadContext.put(REQUEST_ID_HEADER, UUID.randomUUID().toString());
        chain.doFilter(request, response);
    }
}
