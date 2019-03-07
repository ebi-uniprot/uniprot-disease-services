package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Publication;
import uk.ac.ebi.uniprot.ds.rest.dto.PublicationDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PublicationsToPublicationDTOs implements Converter<List<Publication>, List<PublicationDTO>> {
    @Override
    public List<PublicationDTO> convert(MappingContext<List<Publication>, List<PublicationDTO>> context) {
        List<Publication> pubs = context.getSource();
        List<PublicationDTO> pubDTOs = null;
        if(pubs != null){
            pubDTOs = pubs
                    .stream()
                    .map(pub -> new PublicationDTO(pub.getPubType(), pub.getPubId()))
                    .collect(Collectors.toList());
        }

        return pubDTOs;
    }
}
