package records;

import dependencies.DatastoreModule;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class Shard {

	private String shardId;

	public Shard(String shardId) {
		this.shardId = shardId;
	}

	public String getFilePath(){
		return String.format("%s%s%s.data", DatastoreModule.datastorePath(),
							 File.separator,
							 shardId);
	}

	/**
	 * Read the entire shard into memory
	 *
	 * @return
	 * @throws IOException
	 */
	public List<ViewRecord> getRecords() throws IOException {
		String filePath = getFilePath();

		List<ViewRecord> records = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
			String row;

			while ((row = bufferedReader.readLine()) != null) {
				ViewRecord viewRecord = new ViewRecord();
				String error = viewRecord.createFromRow(row);
				//Any validation errors from the data store are exceptions
				if (null != error) {
					throw new RuntimeException("Parse error: " + error);
				}
				records.add(viewRecord);
			}
		} catch (FileNotFoundException ex) {
			//FileNotFound should be recoverable -- just return the empty list
			// and create the file on subsequent saveRecords
		}

		return records;
	}

	/**
	 * Save records to the shard's file, overwriting all previous data
	 *
	 * @param viewRecords
	 * @throws IOException
	 */
	public void saveRecords(List<ViewRecord> viewRecords) throws IOException {
		String filePath = getFilePath();

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
			for (ViewRecord viewRecord : viewRecords) {
				bufferedWriter.write(viewRecord.getPersistedRow());
				bufferedWriter.newLine();
			}
		}
	}

	/**
	 * Update the record, if it exists.  If it does not exist, insert it
	 *
	 * @param viewRecord
	 */
	public void upsert(ViewRecord viewRecord) throws IOException {

		List<ViewRecord> allRecords = this.getRecords();
		ViewRecord match = allRecords.stream()
									 .filter(r -> r.getRowId().equals(viewRecord.getRowId()))
									 .findFirst().orElse(null);

		//If there is an old version of this row, remove it
		if (null != match) {
			allRecords.remove(match);
		}

		//Insert the new version of the row
		allRecords.add(viewRecord);

		//Save to file
		saveRecords(allRecords);

	}

	/**
	 * For each record in the newRecords parameter,
	 * Update a row if its ID already exists (delete then insert)
	 * or insert if the row does not already exist
	 *
	 * @param newRecords
	 * @throws IOException
	 */
	public void upsertAll(List<ViewRecord> newRecords) throws IOException {
		List<ViewRecord> existingRecords = this.getRecords();
		List<String> newIds = newRecords.stream()
										.map(ViewRecord::getRowId)
										.collect(Collectors.toList());

		//Remove any old record whose ID is in the new upsert list
		List<ViewRecord> mergedRecords = existingRecords.stream()
														.filter(r -> !newIds.contains(r.getRowId()))
														.collect(Collectors.toList());

		//Insert the new version of the rows
		mergedRecords.addAll(newRecords);

		//Save to file
		saveRecords(mergedRecords);
	}

	public static String getFilePathStatic(String shardId) {
		String workingDirectory = System.getProperty("user.dir");

		return String.format("%s%s%s.data", DatastoreModule.datastorePath(),
							 File.separator,
							 shardId);
	}


}
