/*
 * Created by sahmad on 07/02/19 12:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
public class DiseaseMapper {

    private ModelMapper modelMapper;

    public DiseaseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        // Protein to String(Accession) Converter
        Converter<Set<Protein>, List<String>> proteinToAccessionString = new Converter<Set<Protein>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<Set<Protein>, List<String>> context) {
                Set<Protein> proteins = context.getSource();
                return proteins != null ? proteins.stream().map(pr -> pr.getAccession()).collect(Collectors.toList()) : null;
            }
        };

        // List<Synonym> to List<String> converter
        Converter<List<Synonym>, List<String>> synonymToStr = new Converter<List<Synonym>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<List<Synonym>, List<String>> ctx) {
                List<Synonym> syns = ctx.getSource();
                List<String> synsStr = null;
                if (syns != null) {
                    synsStr = syns.stream().map(syn -> syn.getName()).collect(Collectors.toList());
                }
                return synsStr;
            }
        };

        // List<Variant> to List<String>
        Converter<List<Variant>, List<String>> varsToStr = new Converter<List<Variant>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<List<Variant>, List<String>> ctx) {
                List<Variant> variants = ctx.getSource();
                List<String> varsStr = null;
                if (variants != null) {
                    varsStr = variants.stream().map(var -> var.getFeatureId()).collect(Collectors.toList());
                }
                return varsStr;
            }
        };

        PropertyMap<Disease, DiseaseDTO> propertyMap = new PropertyMap<Disease, DiseaseDTO>() {
            @Override
            protected void configure() {
                map().setDescription(source.getDesc());
                using(proteinToAccessionString).map(source.getProteins()).setProteins(null);
                using(synonymToStr).map(source.getSynonyms()).setSynonyms(null);
                using(varsToStr).map(source.getVariants()).setVariants(null);
            }
        };
        modelMapper.addMappings(propertyMap);
    }

    public DiseaseDTO toDiseaseDTO(Disease disease){
        return this.modelMapper.map(disease, DiseaseDTO.class);
    }

    public List<DiseaseDTO> toDiseaseDTOList(List<Disease> diseases){
        return this.modelMapper.map(diseases, new TypeToken<List<DiseaseDTO>>(){}.getType());
    }
}
