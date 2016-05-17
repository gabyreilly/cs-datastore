package tests;

import commands.Import;
import commands.Query;
import dependencies.DatastoreModule;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;

/**
 * @date May 2016
 *
 * Tests the Query command
 */
public class QueryTest {

	@Test
	public void testQueryMain() throws Exception {
		//Create the shards
		String fileName = "testQueryMain";

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

		//Call Query with the command line args:
		String[] queryArgs = new String[]{"-s", "TITLE,REV,DATE","-o", "DATE,TITLE"};
		Query.main(queryArgs);

		//It is difficult to make assertions about System.out, but this will at least check for no
		// NPEs, and all the components called in that function are unit tested already.

	}
}
