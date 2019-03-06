/*
 * Created by sahmad on 07/02/19 12:17
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.ac.ebi.uniprot.ds.rest.filter.CorrelationHeaderFilter;
import uk.ac.ebi.uniprot.ds.rest.mapper.DiseaseToDiseaseDTOMap;
import uk.ac.ebi.uniprot.ds.rest.mapper.ProteinToProteinDTOMap;
import uk.ac.ebi.uniprot.ds.rest.mapper.ProteinToProteinDiseasesDTOMap;
import uk.ac.ebi.uniprot.ds.rest.mapper.ProteinToProteinCrossRefsDTOMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;

@SpringBootApplication
@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.common", "uk.ac.ebi.uniprot.ds.rest"})
public class RestServiceSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestServiceSpringBootApplication.class, args);
    }

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new DiseaseToDiseaseDTOMap());
        modelMapper.addMappings(new ProteinToProteinDTOMap());
        modelMapper.addMappings(new ProteinToProteinCrossRefsDTOMap());
        modelMapper.addMappings(new ProteinToProteinDiseasesDTOMap());
        return modelMapper;
    }

    @Bean
    public FilterRegistrationBean correlationHeaderFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new CorrelationHeaderFilter());
        filterRegBean.setUrlPatterns(Arrays.asList("/*"));

        return filterRegBean;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    static final String ALLOW_ALL_ORIGINS = "*";

    /**
     * Defines a simple request filter that adds an Access-Control-Allow-Origin header with the value '*' to REST
     * requests.
     * <p>
     * The reason for explicitly providing an all origins value, '*', is that web-caching of requests from one
     * origin interferes with those from another origin, even when the same resource is fetched.
     *
     * @return
     */
    @Bean
    public OncePerRequestFilter originsFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                    throws ServletException, IOException {

                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW_ALL_ORIGINS);
                chain.doFilter(request, response);

            }
        };
    }
}