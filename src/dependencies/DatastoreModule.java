package dependencies;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @date May 2016
 *
 * I considered using Dependency Injection here, but it was a lot of extra classes
 * just to inject a String into Shard.
 *
 * If there were more dependencies, using a library like Guice would help manage those
 * and control them in test vs prod runs.
 */
public class DatastoreModule {
	/**
	 * Get the folder where datastore files go
	 * No trailing slash
	 *
	 * @return
	 */
	public static String datastorePath() {
		String folderName = "datastore";

		if (isJUnitTest()) {
			folderName = "test-datastore";
		}
		return String.format("%s%s%s",
							 System.getProperty("user.dir"),
							 File.separator,
							 folderName);
	}

	/**
	 * Get the folder where input files go
	 * For the Import command, this will be passed in the param,
	 * but it is useful to have it available for tests.
	 *
	 * No trailing slash
	 *
	 * @return
	 */
	public static String inputPath() {
		String folderName = "input";

		if (isJUnitTest()) {
			folderName = "test-input";
		}
		return String.format("%s%s%s",
							 System.getProperty("user.dir"),
							 File.separator,
							 folderName);
	}

	private static boolean isJUnitTest() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		List<StackTraceElement> list = Arrays.asList(stackTrace);
		for (StackTraceElement element : list) {
			if (element.getClassName().startsWith("org.junit.")) {
				return true;
			}
		}
		return false;
	}
}
