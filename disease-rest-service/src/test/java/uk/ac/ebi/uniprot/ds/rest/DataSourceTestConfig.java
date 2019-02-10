/*
 * Created by sahmad on 07/02/19 16:44
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@TestConfiguration
public class DataSourceTestConfig {

    @Autowired
    private Environment env;
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        System.out.println(env.getProperty("spring.datasource.url"));
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    /*@Bean
    public ProteinDAO proteinDAO(){
        return new ProteinDAO() {
            @Override
            public Optional<Protein> findByProteinId(String proteinId) {
                return Optional.empty();
            }

            @Override
            public Optional<Protein> findProteinByAccession(String accession) {
                return Optional.empty();
            }

            @Override
            public List<Protein> findAll() {
                return null;
            }

            @Override
            public List<Protein> findAll(Sort sort) {
                return null;
            }

            @Override
            public List<Protein> findAllById(Iterable<Long> longs) {
                return null;
            }

            @Override
            public <S extends Protein> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends Protein> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public void deleteInBatch(Iterable<Protein> entities) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public Protein getOne(Long aLong) {
                return null;
            }

            @Override
            public <S extends Protein> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends Protein> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public Page<Protein> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends Protein> S save(S s) {
                return null;
            }

            @Override
            public Optional<Protein> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(Protein protein) {

            }

            @Override
            public void deleteAll(Iterable<? extends Protein> iterable) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public <S extends Protein> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends Protein> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends Protein> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends Protein> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public List<Protein> getProteinsByAccessions(List<String> accessions) {
                return null;
            }
        };
    }*/
}
