package rs.gopro.mobile_store.ui.components;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class CustomerAutocompleteAdapter extends SimpleCursorAdapter {

	private static final String[] CUSTOMER_PROJECTION = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.CUSTOMER_NO, MobileStoreContract.Customers.NAME };
	
	
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
		final String str = cursor.getString(codeIndex) + " - " + cursor.getString(nameIndex);
		return str;
	}
}
