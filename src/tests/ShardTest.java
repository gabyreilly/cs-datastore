package tests;

import fields.ViewTimeField;
import org.junit.Test;
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
 * tests the member methods of shards
 */
public class ShardTest {

	@Test
	public void testGetRecords() throws Exception {
		String shardId = "testGetRecords";

		String filePath = Shard.getFilePathStatic(shardId);

		//The Reader reads from a file on the file system, go ahead and create it
		File testFile = new File(filePath);
		testFile.createNewFile();

		try (FileWriter fileWriter = new FileWriter(filePath)) {
			fileWriter.write("stb1|the matrix|warner bros|2014-04-01|4.00|1:30\n" +
							 "stb1|unbreakable|buena vista|2014-04-03|6.00|2:05\n" +
							 "stb2|the hobbit|warner bros|2014-04-02|8.00|2:45\n" +
							 "stb3|the matrix|warner bros|2014-04-02|4.00|1:05");
		}

		Shard shard = new Shard(shardId);
		List<ViewRecord> viewRecords = shard.getRecords();
		assertEquals(4, viewRecords.size());
	}


	@Test
	public void testUpsert() throws Exception {
		String shardId = "testGetRecords";

		String filePath = Shard.getFilePathStatic(shardId);

		//The Reader reads from a file on the file system, go ahead and create it
		File testFile = new File(filePath);
		testFile.createNewFile();

		try (FileWriter fileWriter = new FileWriter(filePath)) {
			fileWriter.write("stb1|the matrix|warner bros|2014-04-01|4.00|1:30\n" +
							 "stb1|unbreakable|buena vista|2014-04-03|6.00|2:05\n" +
							 "stb2|the hobbit|warner bros|2014-04-02|8.00|2:45\n" +
							 "stb3|the matrix|warner bros|2014-04-02|4.00|1:05");
		}

		Shard shard = new Shard(shardId);

		//Make two new ViewRecords, first one matches an existing ID (changes the time)
		// and the second one is all new
		List<ViewRecord> newViewRecords = new ArrayList<>();
		ViewRecord update = new ViewRecord();
		update.createFromRow("stb1|unbreakable|buena vista|2014-04-03|6.00|6:05");
		newViewRecords.add(update);

		ViewRecord insert = new ViewRecord();
		insert.createFromRow("stb1|movie movie|buena vista|2014-04-03|6.00|2:05");
		newViewRecords.add(insert);

		shard.upsertAll(newViewRecords);

		List<ViewRecord> viewRecords = shard.getRecords();
		assertEquals(5, viewRecords.size());

		//Find the updated record
		ViewRecord match = viewRecords.stream()
				.filter(r -> r.title.equals("unbreakable"))
				.findFirst().orElse(null);

		assertNotNull(match);
		//It should have the new value
		assertEquals("6:05", new ViewTimeField().getReportValue(match));

	}

}