package fields;

import records.ViewRecord;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public interface Field<T extends Comparable> {
	String getName();

	/**
	 *	Null if the value is OK, return an error string if there is a validation problem
	 */
	String setValue(ViewRecord viewRecord, String inputField);

	/**
	 * The actual value (if the field is not a string, this is relevant for sorts)
	 *
	 * @return
	 * @param viewRecord
	 */
	T getValue(ViewRecord viewRecord);

	/**
	 * The string value that can be saved into the data store
	 * @return
	 * @param viewRecord
	 */
	String getReportValue(ViewRecord viewRecord);

}
