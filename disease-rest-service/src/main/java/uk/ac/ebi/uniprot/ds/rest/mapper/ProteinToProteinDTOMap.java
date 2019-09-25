package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProteinToProteinDTOMap extends PropertyMap<Protein, ProteinDTO> {
	@Override
	protected void configure() {
		map().setDescription(source.getDesc());
		using(new VariantsToFeatureIdsConverter()).map(source.getVariants()).setVariants(null);
		using(new InteractionsToAccessionsConverter()).map(source.getInteractions()).setInteractions(null);
		using(new DisProtToDiseaseNameNoteDTOConverter()).map(source.getDiseaseProteins()).setDiseases(null);
		using(new ProteinCrossRefsPathwaysToPrimaryIds()).map(source.getProteinCrossRefs()).setPathways(null);
		using(new GeneCoordinatesToGeneCoordindateDTOsCoverter()).map(source.getGeneCoordinates())
				.setGeneCoordinates(null);
		using(new PublicationsToPublicationDTOs()).map(source.getPublications()).setPublications(null);
		using(new ProteinToDrugs()).map(source).setDrugs(null);
		using(new IsExternallyMappedExtractor()).map(source.getDiseaseProteins()).setIsExternallyMapped(null);

	}
	private  static class IsExternallyMappedExtractor implements Converter<Set<DiseaseProtein>, Boolean>{

		@Override
		public Boolean convert(MappingContext<Set<DiseaseProtein>, Boolean> context) {
			Set<DiseaseProtein> dps = context.getSource();
			Optional<Boolean> optIsMapped = Optional.empty();
			if(!CollectionUtils.isEmpty(dps)){
				optIsMapped = dps.stream().map(dp -> dp.isMapped()).findFirst();
			}
			return optIsMapped.orElse(false);
		}
	}

	public static class ProteinToDrugs implements Converter<Protein, List<String>> {

		@Override
		public List<String> convert(MappingContext<Protein, List<String>> context) {
			Protein protein = context.getSource();
			List<String> drugNames = null;

			if(protein != null && protein.getProteinCrossRefs() != null){

				Set<Drug> drugs = protein.getProteinCrossRefs()
						.stream()
						.filter(xref -> xref.getDrugs() != null && !xref.getDrugs().isEmpty())
						.map(xref -> xref.getDrugs())
						.flatMap(List::stream)
						.collect(Collectors.toSet());

				if(drugs != null && !drugs.isEmpty()) { // drug --> name
					// get just the name
					drugNames = drugs.stream().map(d -> d.getName()).collect(Collectors.toList());
				}

			}

			return drugNames;
		}
	}

	private static class InteractionsToAccessionsConverter implements Converter<List<Interaction>, List<String>> {
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

	private static class DisProtToDiseaseNameNoteDTOConverter implements Converter<Set<DiseaseProtein>, List<ProteinDTO.DiseaseNameNoteDTO>> {
		@Override
		public List<ProteinDTO.DiseaseNameNoteDTO> convert(
				MappingContext<Set<DiseaseProtein>, List<ProteinDTO.DiseaseNameNoteDTO>> context) {
			Set<DiseaseProtein> disProts = context.getSource();

			List<ProteinDTO.DiseaseNameNoteDTO> diseaseNames = null;
			if (disProts != null && !disProts.isEmpty()) {
				diseaseNames = disProts.stream().map(dp -> {
					ProteinDTO.DiseaseNameNoteDTO.DiseaseNameNoteDTOBuilder builder = ProteinDTO.DiseaseNameNoteDTO
							.builder();
					return builder
							.diseaseName(dp.getDisease().getDiseaseId())
							.note(dp.getDisease().getNote())
							.build();

				}).collect(Collectors.toList());
			}

			return diseaseNames;
		}
	}

	private static class ProteinCrossRefsPathwaysToPrimaryIds implements Converter<List<ProteinCrossRef>, List<String>> {
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
	
	private static class GeneCoordinatesToGeneCoordindateDTOsCoverter
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
