/*
 * Created by sahmad on 04/02/19 22:45
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.filter;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CorrelationHeaderFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationHeaderFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String correlationId = httpServletRequest.getHeader(RequestCorrelation.CORRELATION_ID_HEADER);

        if (correlationId == null) {

            correlationId = RandomStringUtils.random(10, true, true);
            LOGGER.info("Generated correlationId {} ", correlationId);
        } else {
            LOGGER.info("Found correlationId in Header {} ", correlationId);
        }

        RequestCorrelation.setCorrelationId(correlationId);

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
