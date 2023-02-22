package com.entando.hub.catalog.config.filter;

import com.entando.hub.catalog.service.CategoryService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;


@Component
@Order(1)   // TODO check if this order is correct
public class ApiKeyFilter extends GenericFilterBean {

    // change this with the proper service
    private CategoryService service;

    @Autowired
    public ApiKeyFilter(CategoryService service) {
        this.service = service;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String apiKey = httpRequest.getHeader("entando-api-key");

        // change this to check the real permission
        if (service.getCategory(apiKey).isPresent()) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are trying to access a private catalog without the required permissions");
        }
    }
}
