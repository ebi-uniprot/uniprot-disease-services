/*
 * Created by sahmad on 1/24/19 9:02 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {

    Optional<T> get(Long id);

    List<T> getAll(Integer offset, Integer maxReturn);

    void createOrUpdate(T t);

    void delete(T t);

    void deleteById(Long id);

}
