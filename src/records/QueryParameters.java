package records;

import fields.Field;
import fields.FieldFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @date May 2016
 *
 * Parses parameters from command line args and turns them into Fields
 */
public class QueryParameters {
	//Selected fields, in order
	public List<Field<? extends Comparable>> selectFields = new ArrayList<>();

	//Order by, in order.
	public List<Field<? extends Comparable>> orderByFields = new ArrayList<>();

	//The fields to filter mapped to the string that is the filter value
	// A record must qualify for all of the filters listed (AND)
	public Map<Field<? extends Comparable>, String> filterFields = new HashMap<>();

	public QueryParameters() {

	}

	/**
	 * Initialize from command line args
	 * @param args
	 */
	public QueryParameters(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-s")) {
				//-s TITLE,REV,DATE
				selectFields = Arrays.asList(args[i + 1].split(",")).stream()
									 .map(FieldFactory::getFromName)
									 .collect(Collectors.toList());
			} else if (args[i].equals("-o")) {
				//-o DATE,TITLE
				orderByFields = Arrays.asList(args[i + 1].split(",")).stream()
									  .map(FieldFactory::getFromName)
									  .collect(Collectors.toList());
			} else if (args[i].equals("-f")) {
				for (String filterField : Arrays.asList(args[i + 1].split(","))) {
					String[] split = filterField.split("=");

					//"DATE=2014-04-01"
					if (split.length == 2) {
						filterFields.put(FieldFactory.getFromName(split[0]), split[1]);
					}

				}
			}
		}
	}

		/**
		 * Does the current viewRecord qualify for every filter in the set?
		 *
		 * @param viewRecord
		 * @return
		 */

	public boolean matchesFilter(ViewRecord viewRecord) {
		return filterFields.entrySet().stream()
						   //For every item in the filterFields set, the key (Field) applied to the viewRecord must match the string value
						   .allMatch(f -> f.getKey().getReportValue(viewRecord).equals(f.getValue()));
	}

	/**
	 * Return a comparator based on the orderByFields
	 *
	 * @return
	 */
	public Comparator<ViewRecord> getComparator() {
		return getComparator(orderByFields);
	}

	private static Comparator<ViewRecord> getComparator(List<Field<? extends Comparable>> orderByFields) {
		return (o1, o2) -> {
			if (orderByFields.isEmpty()) {
				return 0;
			} else {
				Field<? extends Comparable> field = orderByFields.get(0);
				int compare = field.getValue(o1).compareTo(field.getValue(o2));
				if (compare != 0) {
					return compare;
				} else {
					//If "Tied" on the current comparison, continue down the list
					List<Field<? extends Comparable>> remainingFields = orderByFields.subList(1, orderByFields.size());
					return getComparator(remainingFields).compare(o1, o2);
				}
			}
		};
	}

	/**
	 * Using the parameters from the selected fields,
	 * output a comma-joined string of the fields from the record  (no trailing new line)
	 * @param viewRecord
	 * @return
	 */
	public String outputSelectedFields(ViewRecord viewRecord){
		return selectFields.stream()
				.map( sf -> sf.getReportValue(viewRecord))
				.collect(Collectors.joining(","));
	}

}
