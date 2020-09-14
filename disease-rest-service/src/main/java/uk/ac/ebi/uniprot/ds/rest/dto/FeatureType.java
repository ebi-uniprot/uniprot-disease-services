package uk.ac.ebi.uniprot.ds.rest.dto;

/**
 *
 * @author jluo
 * @date: 02-Sep-2020
 *
*/

public enum FeatureType {
	MUTAGENESIS("Mutagenesis"),
	VARIANT("Natural variant");
	
	private final String name;
	FeatureType(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public static FeatureType type(String name) {
		for(FeatureType type: FeatureType.values()) {
			if(type.getName().equals(name))
				return type;
		}
		throw new IllegalArgumentException("FeatureType are not supported: " + name);
	}
	
}

