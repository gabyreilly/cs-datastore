package fields;

import records.ViewRecord;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class TitleField implements Field<String> {

	@Override
	public String getName() {
		return "TITLE";
	}

	@Override
	public String setValue(ViewRecord viewRecord, String inputField) {
		if (inputField.length() > 64){
			return "Max size for title is 64 chars";
		}
		viewRecord.title = inputField;
		return null;
	}

	@Override
	public String getValue(ViewRecord viewRecord) {
		return viewRecord.title;
	}

	@Override
	public String getReportValue(ViewRecord viewRecord) {
		return viewRecord.title;
	}
}