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
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinPathwaysDTO;
import uk.ac.ebi.uniprot.ds.common.model.Pathway;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;
import java.util.stream.Collectors;

public class ProteinToProteinPathwaysDTOMap extends PropertyMap<Protein, ProteinPathwaysDTO> {

    @Override
    protected void configure() {
        using(new PathwaysToPathwayDTOs()).map(source.getPathways()).setPathways(null);

    }

    private class PathwaysToPathwayDTOs implements Converter<List<Pathway>, List<ProteinPathwaysDTO.PathwayDTO>>{
        @Override
        public List<ProteinPathwaysDTO.PathwayDTO> convert(MappingContext<List<Pathway>,
                List<ProteinPathwaysDTO.PathwayDTO>> context) {

            List<Pathway> pathways = context.getSource();
            List<ProteinPathwaysDTO.PathwayDTO> pathwayDTOs = null;

            if(pathways != null){
                pathwayDTOs = pathways.stream().map(pathway -> new ProteinPathwaysDTO.PathwayDTO(pathway.getPrimaryId(), pathway.getDesc()))
                        .collect(Collectors.toList());

            }

            return pathwayDTOs;
        }
    }
}
