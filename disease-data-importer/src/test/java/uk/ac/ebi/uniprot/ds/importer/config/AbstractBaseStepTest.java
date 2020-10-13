package uk.ac.ebi.uniprot.ds.importer.config;

import java.util.List;

import uk.ac.ebi.uniprot.ds.common.model.BaseEntity;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author sahmad
 * @created 13/10/2020
 */
public abstract class AbstractBaseStepTest {
    protected void verifyCommonFields(List<? extends BaseEntity> entities) {
        // verify ids
        entities.stream().map(BaseEntity::getId).forEach(id -> assertThat(id, notNullValue()));
        // verify created at
        entities.stream().map(BaseEntity::getCreatedAt).forEach(id -> assertThat(id, notNullValue()));
        // verify updated at
        entities.stream().map(BaseEntity::getCreatedAt).forEach(id -> assertThat(id, notNullValue()));
    }
}
