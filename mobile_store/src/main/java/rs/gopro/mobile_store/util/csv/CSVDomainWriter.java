package rs.gopro.mobile_store.util.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;

import android.database.Cursor;

public class CSVDomainWriter {

	private static final String TAG = "CSVDomainWriter";
	
	private static final char SEPARATOR = ';';
	private static final char QUOTE = '"';
	
	public CSVDomainWriter() {
		// TODO Auto-generated constructor stub
	}

	public static List<String[]> parseCursor(Cursor cursor) {
		List<String[]> result = new ArrayList<String[]>();
		
		if (cursor.moveToFirst()) {
			do {
				List<String> lineResult = new ArrayList<String>();
				for (int i=0;i<cursor.getColumnCount();i++) {
					switch(cursor.getType(i)) {
					case Cursor.FIELD_TYPE_NULL:
						lineResult.add("");
						break;
					case Cursor.FIELD_TYPE_STRING:
						lineResult.add(cursor.getString(i));
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						lineResult.add(String.valueOf(cursor.getInt(i)));
						break;
					//case Cursor.FIELD_TYPE_FLOAT:
					default:
						lineResult.add(WsDataFormatEnUsLatin.parseForWsDouble(cursor.getDouble(i)));
						break;
					}
				}
				result.add(lineResult.toArray(new String[lineResult.size()]));
			} while (cursor.moveToNext());
		}
		
		return result;
	}
	
}
