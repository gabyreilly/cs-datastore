package fields;

import records.ViewRecord;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class ViewTimeField implements Field<Integer>{

	@Override
	public String getName() {
		return "VIEW_TIME";
	}

	@Override
	public String setValue(ViewRecord viewRecord, String inputField) {
		String[] split = inputField.split(":");
		if (split.length != 2){
			return "Expect time format of hours:minutes";
		}
		viewRecord.viewTime = (Integer.valueOf(split[0]) * 60 + Integer.valueOf(split[1]));

		return null;
	}

	@Override
	public Integer getValue(ViewRecord viewRecord) {
		return viewRecord.viewTime;
	}

	@Override
	public String getReportValue(ViewRecord viewRecord) {
		return String.format("%d:%02d", viewRecord.viewTime / 60, viewRecord.viewTime % 60);
	}
}
