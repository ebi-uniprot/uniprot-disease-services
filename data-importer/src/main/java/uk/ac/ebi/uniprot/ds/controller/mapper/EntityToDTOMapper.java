/*
 * Created by sahmad on 05/02/19 15:28
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class EntityToDTOMapper {
    @Bean
    public PropertyMap<Disease, DiseaseDTO> diseaseToDiseaseDTOMap() {
        // List<Synonym> to List<String> converter
        Converter<List<Synonym>, List<String>> synonymsToNames = new Converter<List<Synonym>, List<String>>() {
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

        PropertyMap<Disease, DiseaseDTO> diseaseToDiseaseDTOMap = new PropertyMap<Disease, DiseaseDTO>() {
            @Override
            protected void configure() {
                map().setDescription(source.getDesc());
                map().setDiseaseName(source.getName());
                using(proteinsToAccessions()).map(source.getProteins()).setProteins(null);
                using(synonymsToNames).map(source.getSynonyms()).setSynonyms(null);
                using(variantsToFeatureIds()).map(source.getVariants()).setVariants(null);
            }
        };

        return diseaseToDiseaseDTOMap;
    }

    @Bean
    public Converter<List<Variant>, List<String>> variantsToFeatureIds() {

        // List<Variant> to List<String>
        Converter<List<Variant>, List<String>> variantsToFeatureIds = new Converter<List<Variant>, List<String>>() {
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
        return variantsToFeatureIds;
    }

    @Bean
    public PropertyMap<Protein, ProteinDTO> proteinToProteinDTOMap() {

        Converter<List<Interaction>, List<String>> interactionsToAccessions = new Converter<List<Interaction>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<List<Interaction>, List<String>> context) {
                List<Interaction> ints = context.getSource();
                List<String> intsStr = null;
                if (ints != null) {
                    intsStr = ints.stream().map(in -> in.getAccession()).collect(Collectors.toList());
                }
                return intsStr;
            }
        };

        Converter<Set<Disease>, List<String>> diseaseToStr = new Converter<Set<Disease>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<Set<Disease>, List<String>> context) {
                Set<Disease> diseases = context.getSource();

                List<String> diseaseNames = null;
                if (diseases != null) {
                    diseaseNames = diseases.stream().map(dis -> dis.getDiseaseId()).collect(Collectors.toList());
                }

                return diseaseNames;
            }
        };

        Converter<List<Pathway>, List<String>> pathwaysToPrimaryIds = new Converter<List<Pathway>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<List<Pathway>, List<String>> context) {
                List<Pathway> ints = context.getSource();
                List<String> intsStr = null;
                if (ints != null) {
                    intsStr = ints.stream().map(in -> in.getPrimaryId()).collect(Collectors.toList());
                }
                return intsStr;
            }
        };

        PropertyMap<Protein, ProteinDTO> proteinToProteinDTOMap = new PropertyMap<Protein, ProteinDTO>() {
            @Override
            protected void configure() {
                map().setDescription(source.getDesc());
                using(variantsToFeatureIds()).map(source.getVariants()).setVariants(null);
                using(interactionsToAccessions).map(source.getInteractions()).setInteractions(null);
                using(diseaseToStr).map(source.getDiseases()).setDiseases(null);
                using(pathwaysToPrimaryIds).map(source.getPathways()).setPathways(null);
            }
        };
        return proteinToProteinDTOMap;
    }

    // Protein to String(Accession) Converter
    @Bean
    public Converter<Set<Protein>, List<String>> proteinsToAccessions() {
        return new Converter<Set<Protein>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<Set<Protein>, List<String>> context) {
                Set<Protein> proteins = context.getSource();
                return proteins != null ? proteins.stream().map(pr -> pr.getAccession()).collect(Collectors.toList()) : null;
            }
        };
    }
}
