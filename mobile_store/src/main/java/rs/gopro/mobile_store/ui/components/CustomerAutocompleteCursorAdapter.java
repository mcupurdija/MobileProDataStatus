package rs.gopro.mobile_store.ui.components;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

public class CustomerAutocompleteCursorAdapter extends CursorAdapter implements
		Filterable {

	private static final String[] CUSTOMER_PROJECTION = new String[] {
		MobileStoreContract.Customers._ID,
		MobileStoreContract.Customers.CUSTOMER_NO,
		MobileStoreContract.Customers.NAME,
		MobileStoreContract.Customers.PRIMARY_CONTACT_ID,
		MobileStoreContract.Customers.PHONE,
		MobileStoreContract.Customers.CONTACT_COMPANY_NO,
		MobileStoreContract.Customers.ADDRESS,
		MobileStoreContract.Customers.POST_CODE
	};
	
	private Context mContext;
	private LayoutInflater mInflater;
	
	public CustomerAutocompleteCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}
	
	public CustomerAutocompleteCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}

	public CustomerAutocompleteCursorAdapter(Context context, Cursor c,
			int flags) {
		super(context, c, flags);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final TextView view = (TextView) mInflater.inflate(R.layout.list_item_autocomplete, parent, false);
		view.setText(getResult(cursor));
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view).setText(getResult(cursor));
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		return getResult(cursor);
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		Cursor cursor = null;
		if (mContext.getContentResolver() != null && constraint != null) {
			cursor = mContext.getContentResolver().query(
					Customers.buildCustomSearchUri(constraint == null ? "noNoOrName"
							: constraint.toString(), "-1"), CUSTOMER_PROJECTION,
					Customers.IS_ACTIVE+"=?", new String[] { "1" }, null);
		}
		return cursor;
	}

	private String getResult(Cursor cursor) {
		final int codeIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.Customers.CUSTOMER_NO);
		final int nameIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
		final String result = cursor.getString(codeIndex) + " - "
				+ cursor.getString(nameIndex);
		return result;
	}
}
