package commands;

import records.MapQuery;
import records.QueryParameters;
import records.Shard;
import records.ViewRecord;

import java.io.IOException;
import java.util.List;

/**
 * @date May 2016
 *
 * Accepts the query command of format:
 * -s TITLE,REV,DATE -o DATE,TITLE
 * or
 * -s TITLE,REV,DATE -f DATE=2014-04-01
 *
 */
public class Query {
	public static void main(String[] args) throws IOException {
		//Parse the args
		QueryParameters queryParameters = new QueryParameters(args);

		if (queryParameters.selectFields.isEmpty()){
			throw new RuntimeException("You must select at least one field");
		}

		//Create a map/reduce handler
		MapQuery mapQuery = new MapQuery();

		//Choose the shards to apply the query to (for now, all of them, no optimization)
		List<Shard> shards = mapQuery.getAllShards();

		List<ViewRecord> results = mapQuery.apply(shards, queryParameters);

		for (ViewRecord result : results){
			//Only output the fields present in the select
			System.out.println(queryParameters.outputSelectedFields(result));

		}
	}
}
