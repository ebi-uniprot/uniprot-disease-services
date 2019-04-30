package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProteinCrossRefToDrugs implements ItemProcessor<ProteinCrossRef, List<Drug>> {

    private Connection dbConnxn;
    private PreparedStatement pStat;

    private static final String SQL_QUERY =     "  SELECT" +
                                                "   md.PREF_NAME AS name," +
                                                "   md.CHEMBL_ID AS source_id," +
                                                "   md.MOLECULE_TYPE AS molecule_type" +
                                                "   FROM CHEMBL_24_APP.MOLECULE_DICTIONARY md" +
                                                "   JOIN CHEMBL_24_APP.DRUG_MECHANISM dm ON md.molregno = dm.molregno" +
                                                "   JOIN CHEMBL_24_APP.TARGET_DICTIONARY td ON dm.tid = td.tid" +
                                                "   WHERE md.max_phase = 4" + // 4 for approved drug
                                                "   AND td.chembl_id = ?";

    public ProteinCrossRefToDrugs(Connection dbConnxn) throws SQLException {
        this.dbConnxn = dbConnxn;
        this.pStat = this.dbConnxn.prepareStatement(SQL_QUERY);
    }

    @Override
    public List<Drug> process(ProteinCrossRef xref) throws Exception {
        List<Drug> drugs = getDrugs(xref);
        log.debug("Total drugs found for {} is {}", xref.getPrimaryId(), drugs.size());
        return drugs;
    }

    private List<Drug> getDrugs(ProteinCrossRef xref) throws SQLException {
        String chEMBLProteinId = xref.getPrimaryId().trim();
        this.pStat.setString(1, chEMBLProteinId);
        List<Drug> drugs = new ArrayList<>();
        try (ResultSet result = this.pStat.executeQuery()) {
            while (result.next()) {
                Drug.DrugBuilder bldr = Drug.builder();
                bldr.sourceType(Constants.ChEMBL_STR).sourceId(result.getString(Constants.SOURCE_ID_STR));
                bldr.name(result.getString(Constants.NAME_STR));
                bldr.moleculeType(result.getString(Constants.MOLECULE_TYPE_STR));
                bldr.proteinCrossRef(xref);
                drugs.add(bldr.build());
            }
        }

        return drugs;
    }
}
