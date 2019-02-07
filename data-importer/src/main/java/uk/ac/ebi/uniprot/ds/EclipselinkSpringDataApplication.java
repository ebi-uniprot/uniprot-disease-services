/*
 * Created by sahmad on 28/01/19 19:14
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.controller.filter.CorrelationHeaderFilter;
import uk.ac.ebi.uniprot.ds.controller.mapper.ProteinToProteinDiseasesDTOMap;
import uk.ac.ebi.uniprot.ds.controller.mapper.ProteinToProteinPathwaysDTOMap;
import uk.ac.ebi.uniprot.ds.model.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;

@SpringBootApplication
public class EclipselinkSpringDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(EclipselinkSpringDataApplication.class, args);
    }

    @Bean
    ModelMapper modelMapper(PropertyMap<Disease, DiseaseDTO> diseaseToDiseaseDTOMap, PropertyMap<Protein, ProteinDTO> proteinToProteinDTOMap) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(diseaseToDiseaseDTOMap);
        modelMapper.addMappings(proteinToProteinDTOMap);
        modelMapper.addMappings(new ProteinToProteinPathwaysDTOMap());
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
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
                response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW_ALL_ORIGINS);
                chain.doFilter(request, response);
            }
        };
    }
}
