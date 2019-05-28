package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProteinToProteinDTOMap extends PropertyMap<Protein, ProteinDTO> {
	@Override
	protected void configure() {
		map().setDescription(source.getDesc());
		using(new VariantsToFeatureIdsConverter()).map(source.getVariants()).setVariants(null);
		using(new InteractionsToAccessionsConverter()).map(source.getInteractions()).setInteractions(null);
		using(new DiseaseToDiseaseIdConverter()).map(source.getDiseases()).setDiseases(null);
		using(new ProteinCrossRefsPathwaysToPrimaryIds()).map(source.getProteinCrossRefs()).setPathways(null);
		using(new GeneCoordinatesToGeneCoordindateDTOsCoverter()).map(source.getGeneCoordinates())
				.setGeneCoordinates(null);
		using(new PublicationsToPublicationDTOs()).map(source.getPublications()).setPublications(null);

	}

	private class InteractionsToAccessionsConverter implements Converter<List<Interaction>, List<String>> {
		@Override
		public List<String> convert(MappingContext<List<Interaction>, List<String>> context) {
			List<Interaction> ints = context.getSource();
			List<String> intsStr = null;
			if (ints != null) {
				intsStr = ints.stream().map(in -> in.getAccession()).collect(Collectors.toList());
			}
			return intsStr;
		}
	}

	private class DiseaseToDiseaseIdConverter implements Converter<List<Disease>, List<ProteinDTO.DiseaseNameNoteDTO>> {
		@Override
		public List<ProteinDTO.DiseaseNameNoteDTO> convert(
				MappingContext<List<Disease>, List<ProteinDTO.DiseaseNameNoteDTO>> context) {
			List<Disease> diseases = context.getSource();

			List<ProteinDTO.DiseaseNameNoteDTO> diseaseNames = null;
			if (diseases != null) {
				diseaseNames = diseases.stream().map(dis -> {
					ProteinDTO.DiseaseNameNoteDTO.DiseaseNameNoteDTOBuilder builder = ProteinDTO.DiseaseNameNoteDTO
							.builder();
					return builder.diseaseName(dis.getDiseaseId()).note(dis.getNote()).build();

				}).collect(Collectors.toList());
			}

			return diseaseNames;
		}
	}

	private class ProteinCrossRefsToPrimaryIds implements Converter<List<ProteinCrossRef>, List<String>> {
		@Override
		public List<String> convert(MappingContext<List<ProteinCrossRef>, List<String>> context) {
			List<ProteinCrossRef> ints = context.getSource();
			List<String> intsStr = null;
			if (ints != null) {
				intsStr = ints.stream().map(in -> in.getPrimaryId()).collect(Collectors.toList());
			}
			return intsStr;
		}
	}

	private class ProteinCrossRefsPathwaysToPrimaryIds implements Converter<List<ProteinCrossRef>, List<String>> {
		private static final String REACTOME = "Reactome";

		@Override
		public List<String> convert(MappingContext<List<ProteinCrossRef>, List<String>> context) {
			List<ProteinCrossRef> ints = context.getSource();
			List<String> intsStr = null;
			if (ints != null) {
				intsStr = ints.stream().filter(val ->val.getDbType().equals(REACTOME)).map(in -> in.getPrimaryId()).collect(Collectors.toList());
			}
			return intsStr;
		}
	}
	
	private class GeneCoordinatesToGeneCoordindateDTOsCoverter
			implements Converter<List<GeneCoordinate>, List<ProteinDTO.GeneCoordinateDTO>> {

		@Override
		public List<ProteinDTO.GeneCoordinateDTO> convert(
				MappingContext<List<GeneCoordinate>, List<ProteinDTO.GeneCoordinateDTO>> context) {
			List<GeneCoordinate> geneCoordList = context.getSource();
			List<ProteinDTO.GeneCoordinateDTO> dtoList = new ArrayList<>();
			for (GeneCoordinate geneCoord : geneCoordList) {
				ProteinDTO.GeneCoordinateDTO.GeneCoordinateDTOBuilder builder = ProteinDTO.GeneCoordinateDTO.builder();
				builder.chromosome(geneCoord.getChromosomeNumber());
				builder.start(geneCoord.getStartPos()).end(geneCoord.getEndPos());
				builder.ensemblGeneId(geneCoord.getEnGeneId()).ensemblTranscriptId(geneCoord.getEnTranscriptId());
				builder.ensemblTranslationId(geneCoord.getEnTranslationId());
				ProteinDTO.GeneCoordinateDTO dto = builder.build();
				dtoList.add(dto);
			}
			return dtoList;
		}
	}
}
