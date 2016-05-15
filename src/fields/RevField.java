package fields;

import records.ViewRecord;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @date May 2016
 *
 * TODO: Please document the purpose of this class
 */
public class RevField implements Field<BigDecimal> {

	@Override
	public String getName() {
		return "REV";
	}

	@Override
	public String setValue(ViewRecord viewRecord, String inputField) {
		//If we do store this to SQL, we should use pennies instead.
		// This is because many SQL data stores don't handle double compares well.

		try {
			viewRecord.rev = new BigDecimal(inputField);
		} catch (NumberFormatException ex){
			return "REV must be a decimal format";
		}

		return null;
	}

	@Override
	public BigDecimal getValue(ViewRecord viewRecord) {
		return viewRecord.rev;
	}

	@Override
	public String getReportValue(ViewRecord viewRecord) {
		return new DecimalFormat("#0.00").format(viewRecord.rev);
	}
}
