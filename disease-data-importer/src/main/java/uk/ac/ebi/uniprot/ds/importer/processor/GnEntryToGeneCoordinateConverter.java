package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GenomicLocationType;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnCoordinateType;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnEntry;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.GeneCoordinate;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class GnEntryToGeneCoordinateConverter implements ItemProcessor<GnEntry, List<GeneCoordinate>> {

    @Autowired
    private ProteinDAO proteinDAO;

    @Override
    public List<GeneCoordinate> process(GnEntry item) {
        List<GeneCoordinate> geneCoordinateList = new ArrayList<>();
        // get the protein by protein id
        Optional<Protein> optProtein = this.proteinDAO.findProteinByAccession(item.getAccession());
        if (optProtein.isPresent()) {
            geneCoordinateList = convertToGeneCoordinateList(item, optProtein.get());

        } else {
            log.debug("Unable to find protein {}", item.getAccession());
        }
        return geneCoordinateList;
    }

    private List<GeneCoordinate> convertToGeneCoordinateList(GnEntry entry, Protein protein) {
        List<GeneCoordinate> geneCoordList = new ArrayList<>();

        List<GnCoordinateType> gnCoordTypeList = entry.getGnCoordinate();

        for (GnCoordinateType gnCoordType : gnCoordTypeList) {
            GenomicLocationType glocType = gnCoordType.getGenomicLocation();
            GeneCoordinate.GeneCoordinateBuilder builder = GeneCoordinate.builder();
            builder.chromosomeNumber(glocType.getChromosome());
            builder.startPos(glocType.getStart()).endPos(glocType.getEnd());
            builder.enGeneId(gnCoordType.getEnsemblGeneId()).enTranslationId(gnCoordType.getEnsemblTranslationId());
            builder.enTranscriptId(gnCoordType.getEnsemblTranscriptId());
            builder.protein(protein);
            GeneCoordinate gcoord = builder.build();
            protein.getGeneCoordinates().add(gcoord);
            geneCoordList.add(gcoord);
        }

        return geneCoordList;
    }
}
