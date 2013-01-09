package rs.gopro.mobile_store.test;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import rs.gopro.mobile_store.ws.model.ItemsDomain;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class CsvTest {

	public CsvTest() {
		// TODO Auto-generated constructor stub
	}

	private ColumnPositionMappingStrategy<ItemsDomain> strat;

	@Before
	public void setUp() throws Exception {
		strat = new ColumnPositionMappingStrategy<ItemsDomain>();
		strat.setType(ItemsDomain.class);
	}

	@Test
	public void testParse() {
		String s = "" + "kyle,123456,emp123,1\n" + "jimmy,abcnum,cust09878,2";

		String[] columns = new String[] { "item_no", "description", "unit_of_measure" };
		strat.setColumnMapping(columns);

		CsvToBean<ItemsDomain> csv = new CsvToBean<ItemsDomain>();
		List<ItemsDomain> list = csv.parse(strat, new StringReader(s));
		assertNotNull(list);
		assertTrue(list.size() == 2);
		ItemsDomain bean = list.get(0);
		assertEquals("kyle", bean.getItem_no());
		assertEquals("123456", bean.getDescription());
		assertEquals("emp123", bean.getUnit_of_measure());
		//assertEquals(1, bean.getNum());
	}

	//@Test
	public void testParseWithTrailingSpaces() {
		String s = "" + "kyle  ,123456  ,emp123  ,  1   \n"
				+ "jimmy,abcnum,cust09878,2   ";

		String[] columns = new String[] { "name", "orderNumber", "id", "num" };
		strat.setColumnMapping(columns);

		CsvToBean<ItemsDomain> csv = new CsvToBean<ItemsDomain>();
		List<ItemsDomain> list = csv.parse(strat, new StringReader(s));
		assertNotNull(list);
		assertTrue(list.size() == 2);
		ItemsDomain bean = list.get(0);
		assertEquals("kyle  ", bean.getItem_no());
		assertEquals("123456  ", bean.getDescription());
		assertEquals("emp123  ", bean.getUnit_of_measure());
//		assertEquals(1, bean.getNum());
	}

	@Test
	public void testGetColumnMapping() {
		String[] columnMapping = strat.getColumnMapping();
		assertNotNull(columnMapping);
		assertEquals(0, columnMapping.length);

		String[] columns = new String[] { "name", "orderNumber", "id" };
		strat.setColumnMapping(columns);

		columnMapping = strat.getColumnMapping();
		assertNotNull(columnMapping);
		assertEquals(3, columnMapping.length);
		assertArrayEquals(columns, columnMapping);

	}

	@Test
	public void testGetColumnNames() {

		String[] columns = new String[] { "name", null, "id" };
		strat.setColumnMapping(columns);

//		assertEquals("name", strat.getColumnName(0));
//		assertEquals(null, strat.getColumnName(1));
//		assertEquals("id", strat.getColumnName(2));
//		assertEquals(null, strat.getColumnName(3));
	}

	@Test
	public void testGetColumnNamesArray() {

		String[] columns = new String[] { "name", null, "id" };
		strat.setColumnMapping(columns);
		String[] mapping = strat.getColumnMapping();

		assertEquals(3, mapping.length);
		assertEquals("name", mapping[0]);
		assertEquals(null, mapping[1]);
		assertEquals("id", mapping[2]);
	}

	@Test
	public void getColumnNamesHandlesNull() {
//		strat.setColumnMapping(null);
//
//		assertEquals(null, strat.getColumnName(0));
//		assertEquals(null, strat.getColumnName(1));
//		assertNull(strat.getColumnMapping());
	}

}
