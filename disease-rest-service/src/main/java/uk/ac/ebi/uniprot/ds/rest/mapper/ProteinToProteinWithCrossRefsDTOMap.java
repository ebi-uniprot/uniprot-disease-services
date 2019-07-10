/*
 * Created by sahmad on 07/02/19 12:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinCrossRefDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinWithCrossRefsDTO;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.*;
import java.util.stream.Collectors;

public class ProteinToProteinWithCrossRefsDTOMap extends PropertyMap<Protein, ProteinWithCrossRefsDTO> {

    @Override
    protected void configure() {
        using(new ProteinCrossRefsToProteinCrossRefDTOs()).map(source.getProteinCrossRefs()).setXrefs(null);

    }

    private static class ProteinCrossRefsToProteinCrossRefDTOs implements
            Converter<List<ProteinCrossRef>, List<ProteinCrossRefDTO>> {

        @Override
        public List<ProteinCrossRefDTO> convert(MappingContext<List<ProteinCrossRef>,
                List<ProteinCrossRefDTO>> context) {

            List<ProteinCrossRef> proteinXRefs = context.getSource();
            List<ProteinCrossRefDTO> proteinXRefDTOs = null;

            if(proteinXRefs != null){

                proteinXRefDTOs = proteinXRefs.stream().map(xref ->
                        new ProteinCrossRefDTO(xref.getPrimaryId(), xref.getDbType(), xref.getDescription(), xref.getProteinAccessions()))
                        .collect(Collectors.toList());

                // sort by dbType
                Collections.sort(proteinXRefDTOs, (xref1, xref2) -> xref1.getDbType().compareToIgnoreCase(xref2.getDbType()));

            }

            return proteinXRefDTOs;
        }

    }
}
