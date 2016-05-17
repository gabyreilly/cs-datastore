package tests;

import commands.Import;
import dependencies.DatastoreModule;
import fields.DateField;
import fields.TitleField;
import org.junit.Test;
import records.MapQuery;
import records.QueryParameters;
import records.Shard;
import records.ViewRecord;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class MapQueryTest {

	@Test
	public void testGetShardsAndApply() throws Exception {
		//Create the shards
		String fileName = "testApply";

		String filePath = String.format("%s%s%s.data", DatastoreModule.inputPath(),
										File.separator,
										fileName);

		//The Reader reads from a file on the file system, go ahead and create it
		File testFile = new File(filePath);
		testFile.getParentFile().mkdirs();
		testFile.createNewFile();

		try (FileWriter fileWriter = new FileWriter(filePath)) {
			fileWriter.write("STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME\n" +
							 "stb1|the matrix|warner bros|2014-04-01|4.00|1:30\n" +
							 "stb1|unbreakable|buena vista|2014-04-03|6.00|2:05\n" +
							 "stb2|the hobbit|warner bros|2014-04-02|8.00|2:45\n" +
							 "stb3|the matrix|warner bros|2014-04-02|4.00|1:05");
		}

		//Clear out the test datatstore
		DatastoreModule.clearTestFolder();

		//Now call main with that filepath for the input we created
		String[] args = new String[]{filePath};
		Import.main(args);

		//Now assert things about the shards available in MapQuery.getShards
		MapQuery mapQuery = new MapQuery();
		List<Shard> allShards = mapQuery.getAllShards();

		//We expect  3 shards to be there
		//Make assertions based on the assumption that shards are grouped per day

		assertEquals(3, allShards.size());

		//Create a query that sorts by Date "then by" Title
		QueryParameters queryParameters = new QueryParameters();
		queryParameters.orderByFields = new ArrayList<>();
		queryParameters.orderByFields.add(new DateField());
		queryParameters.orderByFields.add(new TitleField());

		List<ViewRecord> results = mapQuery.apply(allShards, queryParameters);

		assertEquals(4, results.size());
		//  STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME
		//  stb1|the matrix|warner bros|2014-04-01|4.00|1:30
		//  stb2|the hobbit|warner bros|2014-04-02|8.00|2:45
		//  stb3|the matrix|warner bros|2014-04-02|4.00|1:05
		//  stb1|unbreakable|buena vista|2014-04-03|6.00|2:05
		assertEquals("stb1", results.get(0).stb);
		assertEquals("the matrix", results.get(0).title);

		assertEquals("stb2", results.get(1).stb);
		assertEquals("the hobbit", results.get(1).title);

		assertEquals("stb3", results.get(2).stb);
		assertEquals("the matrix", results.get(2).title);

		assertEquals("stb1", results.get(3).stb);
		assertEquals("unbreakable", results.get(3).title);

		//Create a new query that only has a Filter
		queryParameters = new QueryParameters();
		queryParameters.filterFields.put(new DateField(), "2014-04-01");

		//Use a new MapQuery too, so we get new UUIDs
		mapQuery = new MapQuery();
		results = mapQuery.apply(allShards, queryParameters);

		//There should be only 1 result
		assertEquals(1, results.size());
		assertEquals("stb1", results.get(0).stb);
		assertEquals("the matrix", results.get(0).title);

	}
}