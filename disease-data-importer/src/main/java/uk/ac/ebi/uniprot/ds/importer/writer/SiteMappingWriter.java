package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.SiteMappingDAO;
import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;

import java.util.List;

/**
 * @author lgonzales
 * @since 10/09/2020
 */
public class SiteMappingWriter implements ItemWriter<SiteMapping> {

    @Autowired
    private SiteMappingDAO siteMappingDAO;

    @Override
    public void write(List<? extends SiteMapping> items) {
        this.siteMappingDAO.saveAll((List<SiteMapping>) items);
    }

}
