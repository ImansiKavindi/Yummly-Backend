package com.yummly.web.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // Log request details
        logger.info(
            "Request: {} {} (ContentType: {}, Headers: {})",
            req.getMethod(),
            req.getRequestURI(),
            req.getContentType(),
            Collections.list(req.getHeaderNames())
                .stream()
                .map(name -> name + "=" + req.getHeader(name))
                .collect(Collectors.joining(", "))
        );
        
        // Continue processing
        chain.doFilter(request, response);
        
        // Log response status
        logger.info("Response Status: {}", res.getStatus());
    }
} 