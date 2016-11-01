package rs.gopro.mobile_store.ui.components;

import org.apache.commons.lang3.StringUtils;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class CustomerAutocompleteAdapter extends SimpleCursorAdapter {

	private static final String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME, MobileStoreContract.Customers.NAME_2, MobileStoreContract.Customers.CITY };
	
	
	public CustomerAutocompleteAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	/**
	 * With default customer columns.
	 * {@inheritDoc}
	 * @param context
	 * @param layout
	 * @param c
	 * @param to
	 * @param flags
	 */
	public CustomerAutocompleteAdapter(Context context, int layout, Cursor c, int[] to, int flags) {
		super(context, layout, c, CUSTOMER_PROJECTION, to, flags);
	}
	
	@Override
	public CharSequence convertToString(Cursor cursor) {
		final int codeIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		final int nameIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
		final int name2Index = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME_2);
		final int cityIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.CITY);
		String name2 = cursor.getString(name2Index);
		String city = cursor.getString(cityIndex);
		return cursor.getString(codeIndex) + " - " + cursor.getString(nameIndex) + (StringUtils.isNotEmpty(name2) ? " (" + name2 + ")" : "") + (StringUtils.isNotEmpty(city) ? ", " + city : "");

	}
}
