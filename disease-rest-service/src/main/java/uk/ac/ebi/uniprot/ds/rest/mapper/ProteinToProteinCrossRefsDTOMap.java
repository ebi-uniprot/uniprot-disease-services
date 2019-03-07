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
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinCrossRefsDTO;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.*;
import java.util.stream.Collectors;

public class ProteinToProteinCrossRefsDTOMap extends PropertyMap<Protein, ProteinCrossRefsDTO> {

    @Override
    protected void configure() {
        using(new ProteinCrossRefsToProteinCrossRefDTOs()).map(source.getProteinCrossRefs()).setXrefs(null);

    }

    private class ProteinCrossRefsToProteinCrossRefDTOs implements
            Converter<List<ProteinCrossRef>, List<ProteinCrossRefsDTO.ProteinCrossRef>> {

        @Override
        public List<ProteinCrossRefsDTO.ProteinCrossRef> convert(MappingContext<List<ProteinCrossRef>,
                List<ProteinCrossRefsDTO.ProteinCrossRef>> context) {

            List<ProteinCrossRef> proteinXRefs = context.getSource();
            List<ProteinCrossRefsDTO.ProteinCrossRef> proteinXRefDTOs = null;

            if(proteinXRefs != null){

                proteinXRefDTOs = proteinXRefs.stream().map(xref -> new ProteinCrossRefsDTO
                        .ProteinCrossRef(xref.getPrimaryId(), xref.getDbType(), xref.getDesc()))
                        .collect(Collectors.toList());

                // sort by dbType
                Collections.sort(proteinXRefDTOs, (xref1, xref2) -> xref1.getDbType().compareToIgnoreCase(xref2.getDbType()));

            }

            return proteinXRefDTOs;
        }

    }
}
