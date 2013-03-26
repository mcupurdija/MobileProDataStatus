package rs.gopro.mobile_store.ui.components;

import java.util.HashMap;
import java.util.Map;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

public class ContactsAutocompleteCursorAdapter extends CursorAdapter implements
		Filterable {

	private static final String[] CONTACT_PROJECTION = new String[] {
		MobileStoreContract.Contacts._ID,
		MobileStoreContract.Contacts.CONTACT_NO,
		MobileStoreContract.Contacts.NAME,
		MobileStoreContract.Contacts.PHONE,
		MobileStoreContract.Contacts.EMAIL
	};

	private Context mContext;
	private LayoutInflater mInflater;
//	private Map<String, Integer> dataPositions = new HashMap<String, Integer>();
	
	public ContactsAutocompleteCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}
	
	public ContactsAutocompleteCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}

	public ContactsAutocompleteCursorAdapter(Context context, Cursor c,
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
					Contacts.buildCustomSearchUri(constraint == null ? "noNoOrName"
							: constraint.toString()), CONTACT_PROJECTION,
					null, null, null);
		}
		return cursor;
	}

	private String getResult(Cursor cursor) {
		final int codeIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.Contacts.CONTACT_NO);
		final int nameIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.Contacts.NAME);
		final String result = cursor.getString(codeIndex) + " - "
				+ cursor.getString(nameIndex);
//		dataPositions.put(result, Integer.valueOf(cursor.getInt(cursor
//				.getColumnIndexOrThrow(MobileStoreContract.Customers._ID))));
		return result;
	}
	
//	public int getIdForTitle(String data) {
//		if (dataPositions.containsKey(data)) {
//			return dataPositions.get(data);
//		} else return -1; 
//	}
	
//	public void setIdForTitle(String data, int id) {
//		dataPositions.put(data, Integer.valueOf(id));
//	}
}
