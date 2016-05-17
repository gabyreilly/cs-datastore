package records;

import dependencies.DatastoreModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date May 2016
 *
 * Handles Map/Reduce of filter and sort across the shards.
 * DOES NOT HANDLE SELECT -- that should be done immediately before returning the view
 *
 * Uses temp files to handle intermediate shard data.  This is not optimized for
 * space OR for time since each file has to created and then read.
 *
 * Potential optimizations while still using flat files:
 *   Use MAX_PAGE_SIZE below to estimate how many rows we should keep from each shard.
 *   This would reduce the space for each query.
 *
 *
 */
public class MapQuery {

	//Since we have to assume the data set is too large to fit in memory, we also have to handle the case
	// where someone queries with a sort and no filter.  We can't return this all in one page, so there must be some maximum.
	// The implementation could continue to be enhanced to include a behind-the-scenes paging mechanism.
	private static final int MAX_PAGE_SIZE = 100;

	private final UUID uuid;

	public MapQuery() {
		uuid = UUID.randomUUID();
	}

	public List<Shard> getAllShards() {
		File datastoreFolder = new File(DatastoreModule.datastorePath());

		if (!datastoreFolder.exists() || !datastoreFolder.isDirectory()) {
			throw new RuntimeException("Could not find folder at " + DatastoreModule.datastorePath());
		}

		List<Shard> allShards = new ArrayList<>();

		for (File shardFile : datastoreFolder.listFiles()) {
			allShards.add(new Shard(shardFile));
		}

		return allShards;
	}

	/**
	 * A very rough map/reduce routine to apply sorts and filters (not selects)
	 * @param shards
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	public List<ViewRecord> apply(List<Shard> shards, QueryParameters parameters) throws IOException {

		//Create temp directory for temp shard files
		File tempFolder = new File(DatastoreModule.tempPath(uuid.toString()));
		tempFolder.mkdirs();

		//Read in each shard from the main data store, applying filter and sort, and copying to the temp folder

		List<String> allTempFilePaths = new ArrayList<>();

		for (Shard shard : shards) {
			List<ViewRecord> allRecords = shard.getRecords();

			//Apply filter and sort
			allRecords = allRecords.stream()
								   .filter(parameters::matchesFilter)
								   .sorted(parameters.getComparator())
								   .collect(Collectors.toList());

			String tempFilePath = shard.getTempFilePath(uuid.toString());
			allTempFilePaths.add(tempFilePath);

			//Save the sorted filtered records to disk
			shard.saveRecords(allRecords, tempFilePath);
		}

		//Now reduce the temp results into a single result set
		//How to handle sorting?  Check the first row on each file and find the lowest of all the first rows.
		// On the file where it was found, go on to the next line.

		//Open file readers for every temp file

		List<ViewRecord> finalResult = new ArrayList<>();
		List<BufferedReader> bufferedReaders = new ArrayList<>();

		try {

			Map<ViewRecord, BufferedReader> topRows = new HashMap<>();

			//Seed the topRows with the first row of each file
			for (String filePath : allTempFilePaths) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
				bufferedReaders.add(bufferedReader);

				String row = bufferedReader.readLine();
				if (null != row) {
					ViewRecord viewRecord = new ViewRecord();
					viewRecord.createFromRow(row);

					topRows.put(viewRecord, bufferedReader);
				}
			}

			ViewRecord smallest;

			while (topRows.size() <= MAX_PAGE_SIZE &&
				   ((smallest = topRows.keySet().stream()
										   .sorted(parameters.getComparator())
										   .findFirst().orElse(null)) != null)) {

				//Find the lowest viewRecord using the comparator
				//Grab the reader this came from
				BufferedReader incrementReader = topRows.get(smallest);

				//Save this row into the result set
				topRows.remove(smallest);
				finalResult.add(smallest);

				//Increment the reader for the next row
				String row = incrementReader.readLine();
				if (null != row) {
					ViewRecord viewRecord = new ViewRecord();
					viewRecord.createFromRow(row);

					topRows.put(viewRecord, incrementReader);
				}
			}

			//Delete the temp files we created
			for (File files : tempFolder.listFiles()){
				files.delete();
			}
			tempFolder.delete();

		} finally {
			for (BufferedReader bufferedReader : bufferedReaders) {
				bufferedReader.close();
			}
		}
		return finalResult;

	}

}
