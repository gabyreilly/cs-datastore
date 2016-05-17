package tests;

import fields.DateField;
import fields.TitleField;
import org.junit.Test;
import records.QueryParameters;
import records.ViewRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @date May 2016
 *
 * Tests the functionality of QueryParameters class
 */
public class QueryParametersTest {

	@Test
	public void testGetComparator() throws Exception {
		ViewRecord vr1 = new ViewRecord();
		vr1.createFromRow("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");

		ViewRecord vr2 = new ViewRecord();
		vr2.createFromRow("stb1|unbreakable|buena vista|2014-04-03|6.00|2:05");

		ViewRecord vr3 = new ViewRecord();
		vr3.createFromRow("stb2|the hobbit|warner bros|2014-04-02|8.00|2:45");

		ViewRecord vr4 = new ViewRecord();
		vr4.createFromRow("stb3|the matrix|warner bros|2014-04-02|4.00|1:05");


		List<ViewRecord> viewRecords = Arrays.asList(vr1, vr2, vr3, vr4);


		//Create a query that sorts by Date
		QueryParameters queryParameters = new QueryParameters();
		queryParameters.orderByFields = new ArrayList<>();
		queryParameters.orderByFields.add(new DateField());


		viewRecords.sort(queryParameters.getComparator());

		assertEquals(vr1, viewRecords.get(0));
		assertEquals(vr2, viewRecords.get(3));
		//The other two don 't end up in a defined order


		//Now keep the Date sort, and add "then by" Title
		queryParameters.orderByFields.add(new TitleField());

		viewRecords.sort(queryParameters.getComparator());

		assertEquals(vr1, viewRecords.get(0));
		assertEquals(vr3, viewRecords.get(1));
		assertEquals(vr4, viewRecords.get(2));
		assertEquals(vr2, viewRecords.get(3));

	}


	@Test
	public void testMatchesFilter() throws Exception {

		ViewRecord vr1 = new ViewRecord();
		vr1.createFromRow("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");

		//This filter should match
		QueryParameters queryParameters = new QueryParameters();
		queryParameters.filterFields.put(new TitleField(), "the matrix");

		assertTrue(queryParameters.matchesFilter(vr1));

		//This filter should not
		queryParameters.filterFields = new HashMap<>();
		queryParameters.filterFields.put(new DateField(), "2016-01-01");

		assertFalse(queryParameters.matchesFilter(vr1));
	}

	@Test
	public void testOutputSelectedFields() throws Exception {
		ViewRecord vr1 = new ViewRecord();
		vr1.createFromRow("stb1|the matrix|warner bros|2014-04-01|4.00|1:30");

		//Select only TITLE,REV,DATE
		QueryParameters queryParameters = new QueryParameters(new String[]{"-s", "TITLE,REV,DATE"});

		assertEquals("the matrix,4.00,2014-04-01", queryParameters.outputSelectedFields(vr1));

	}
}