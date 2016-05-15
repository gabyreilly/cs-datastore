package fields;

import records.ViewRecord;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class DateField implements Field<LocalDate> {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

	@Override
	public String getName() {
		return "DATE";
	}

	@Override
	public String setValue(ViewRecord viewRecord, String inputField) {
		try {
			viewRecord.date = LocalDate.parse(inputField, formatter);
		} catch (DateTimeParseException ex) {
			return "Required Date format: YYYY-MM-DD";
		}
		return null;
	}

	@Override
	public LocalDate getValue(ViewRecord viewRecord) {
		return viewRecord.date;
	}

	@Override
	public String getReportValue(ViewRecord viewRecord) {
		return formatter.format(viewRecord.date);
	}
}
