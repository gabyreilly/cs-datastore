package tests;

import commands.Import;
import dependencies.DatastoreModule;
import records.Shard;

import java.io.File;
import java.io.FileWriter;

import static org.junit.Assert.*;

/**
 * @date May 2016
 *
 * Tests the import command
 */
public class ImportTest {

	@org.junit.Test
	public void testMain() throws Exception {
		String fileName = "testMain";

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

		//Now call main with that filepath
		String[] args = new String[]{filePath};
		Import.main(args);

		//Make assertions based on the assumption that shards are grouped per day
		Shard april1 = new Shard("2014-04-01");
		Shard april2 = new Shard("2014-04-02");
		Shard april3 = new Shard("2014-04-03");

		assertEquals(1, april1.getRecords().size());
		assertEquals(2, april2.getRecords().size());
		assertEquals(1, april3.getRecords().size());

	}
}