package records;

import fields.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @date May 2016
 *
 * Represents a single instance of viewing history.
 *
 */
public class ViewRecord {
	public String stb;

	public String title;

	public String provider;

	public LocalDate date;

	public BigDecimal rev;

	public Integer viewTime; //In minutes



	/**
	 * Returns an error string if there is a problem,
	 * or null if the object is created correctly
	 *
	 * @param row
	 * @return
	 */
	public String createFromRow(String row) {
		//The row is a pipe-delimited row of format
		// STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME
		String[] split = row.split("\\|");

		if (split.length != 6){
			return "The row must be of format STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME";
		}

		List<String> errors = new ArrayList<>();

		String errorMsg = new StbField().setValue(this, split[0]);
		if (null != errorMsg){
			errors.add(errorMsg);
		}

		errorMsg = new TitleField().setValue(this, split[1]);
		if (null != errorMsg){
			errors.add(errorMsg);
		}

		errorMsg = new ProviderField().setValue(this, split[2]);
		if (null != errorMsg){
			errors.add(errorMsg);
		}

		errorMsg = new DateField().setValue(this, split[3]);
		if (null != errorMsg){
			errors.add(errorMsg);
		}


		errorMsg = new RevField().setValue(this, split[4]);
		if (null != errorMsg){
			errors.add(errorMsg);
		}

		errorMsg = new ViewTimeField().setValue(this, split[5]);
		if (null != errorMsg){
			errors.add(errorMsg);
		}

		if (errors.size() > 0){
			return errors.stream().collect(Collectors.joining(", "));
		}

		return null;
	}

	/**
	 * Get the row that should be persisted to the data store to represent this record
	 *
	 * @return
	 */
	public String getPersistedRow(){
		List<Field> fields = new ArrayList<>();

		fields.add(new StbField());
		fields.add(new TitleField());
		fields.add(new ProviderField());
		fields.add(new DateField());
		fields.add(new RevField());
		fields.add(new ViewTimeField());

		return fields.stream()
					 .map(f -> f.getReportValue(this))
					 .collect(Collectors.joining("|"));

	}

	/**
	 * This identifies an ID for a logical record, subsequent imports of the
	 * same ID should update the row
	 *
	 * @return
	 */
	public String getRowId(){
		return String.format("%s|%s|%s", stb, title, date);
	}

	public String getShardId(){
		return date.toString();
	}

}
