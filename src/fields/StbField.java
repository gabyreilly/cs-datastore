package fields;

import records.ViewRecord;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class StbField implements Field<String> {
	@Override
	public String getName() {
		return "STB";
	}

	@Override
	public String setValue(ViewRecord viewRecord, String inputField) {
		if (inputField.length() > 64){
			return "Max size for STB is 64 chars";
		}
		viewRecord.stb = inputField;
		return null;
	}

	@Override
	public String getValue(ViewRecord viewRecord) {
		return viewRecord.stb;
	}

	@Override
	public String getReportValue(ViewRecord viewRecord) {
		return viewRecord.stb;
	}
}
