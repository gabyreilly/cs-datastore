package commands;

import records.Shard;
import records.ViewRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Import {

	public static void main(String[] args) {

		if (args.length != 1) {
			throw new RuntimeException("Usage: Import filepath");
		}

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0]))) {
			//Skip the header row and make sure there was something in it
			if (bufferedReader.readLine() == null) {
				throw new RuntimeException("File was empty at " + args[0]);
			}
			//Now start reading the data rows
			String row;
			List<ViewRecord> viewRecords = new ArrayList<>();
			int rowNum = 0; //Just used for error message

			while ((row = bufferedReader.readLine()) != null) {
				ViewRecord newRecord = new ViewRecord();
				String error = newRecord.createFromRow(row);
				//Using strings for validation instead of exceptions allows us
				// to continue on to the next row while just noting the error for the user
				// in system.out
				if (null != error) {
					System.out.println("Error with row " + 0 + ": " + error);
				} else {
					viewRecords.add(newRecord);
				}
			}

			//We have read in the data set, now group into their shards and save
			Map<String, List<ViewRecord>> groupedRecords =
					viewRecords.stream().collect(Collectors.groupingBy(
							ViewRecord::getShardId));

			for (String shardId : groupedRecords.keySet()){
				Shard shard = new Shard(shardId);
				shard.upsertAll(groupedRecords.get(shardId));
			}

		} catch (IOException e) {
			throw new RuntimeException("Could not read from file " + args[0]);
		}
	}
}
