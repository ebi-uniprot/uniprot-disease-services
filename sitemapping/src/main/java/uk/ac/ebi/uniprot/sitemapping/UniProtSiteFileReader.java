package uk.ac.ebi.uniprot.sitemapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMapRow;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
 */

public class UniProtSiteFileReader {

	public static List<UniProtSiteMapRow> read(String filename) throws IOException {
		Path path = Paths.get(filename);
		List<String> lines = Files.readAllLines(path);
		return lines.stream()
				.map(UniProtSiteMapRow::of)
				.filter(val -> val != null)
				.collect(Collectors.toList());
	}
}
