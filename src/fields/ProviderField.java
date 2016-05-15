package fields;

import records.ViewRecord;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class ProviderField  implements Field<String> {
	@Override
	public String getName() {
		return "PROVIDER";
	}

	@Override
	public String setValue(ViewRecord viewRecord, String inputField) {
		if (inputField.length() > 64){
			return "Max size for provider is 64 chars";
		}
		viewRecord.provider = inputField;
		return null;
	}

	@Override
	public String getValue(ViewRecord viewRecord) {
		return viewRecord.provider;
	}

	@Override
	public String getReportValue(ViewRecord viewRecord) {
		return viewRecord.provider;
	}
}