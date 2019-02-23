/*
 * Created by sahmad on 30/01/19 10:08
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.GeneCoordinateDAO;
import uk.ac.ebi.uniprot.ds.common.model.GeneCoordinate;

import java.util.ArrayList;
import java.util.List;
public class GeneCoordinateWriter implements ItemWriter<List<GeneCoordinate>> {

    @Autowired
    private GeneCoordinateDAO geneCoordinateDAO;
    @Override
    public void write(List<? extends List<GeneCoordinate>> items) throws Exception {

        List<GeneCoordinate> geneCoordinates = new ArrayList<>();
        for(List<GeneCoordinate> gcs : items){
            geneCoordinates.addAll(gcs);
        }

        this.geneCoordinateDAO.saveAll(geneCoordinates);

    }
}
