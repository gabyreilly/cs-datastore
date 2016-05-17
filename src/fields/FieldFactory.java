package fields;

import java.util.HashMap;
import java.util.Map;

/**
 * @date May 2016
 *
 * Converts a string name from the command line input to the Field
 *
 */
public class FieldFactory {

	public static Field<? extends Comparable> getFromName(String name){
		Map<String, Field<? extends Comparable>> allNames = new HashMap<>();
		allNames.put(new DateField().getName(), new DateField());
		allNames.put(new ProviderField().getName(), new ProviderField());
		allNames.put(new RevField().getName(), new RevField());
		allNames.put(new StbField().getName(), new StbField());
		allNames.put(new TitleField().getName(), new TitleField());
		allNames.put(new ViewTimeField().getName(), new ViewTimeField());

		Field<? extends Comparable> found = allNames.get(name);

		if (null == found){
			throw new RuntimeException("Could not understand input name " + name);
		}

		return found;

	}
}
