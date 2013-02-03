package rs.gopro.mobile_store.ui.components;

import java.util.HashMap;
import java.util.Map;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

public class ItemAutocompleteCursorAdapter extends CursorAdapter implements
		Filterable {

	private static final String[] ITEM_PROJECTION = new String[] {
		MobileStoreContract.Items._ID,
		MobileStoreContract.Items.ITEM_NO,
		MobileStoreContract.Items.DESCRIPTION
	};

	private Context mContext;
	private LayoutInflater mInflater;
	private Map<String, Integer> dataPositions = new HashMap<String, Integer>();
	
	public ItemAutocompleteCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}
	
	public ItemAutocompleteCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}

	public ItemAutocompleteCursorAdapter(Context context, Cursor c,
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
					Items.buildAutocompleteSearchUri(constraint == null ? ""
							: constraint.toString()), ITEM_PROJECTION,
					null, null, null);
		}
		return cursor;
	}

	private String getResult(Cursor cursor) {
		final int codeIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.Items.ITEM_NO);
		final int nameIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.Items.DESCRIPTION);
		final String result = cursor.getString(codeIndex) + " - "
				+ cursor.getString(nameIndex);
		dataPositions.put(result, Integer.valueOf(cursor.getInt(cursor
				.getColumnIndexOrThrow(MobileStoreContract.Items._ID))));
		return result;
	}
	
	public int getIdForTitle(String data) {
		if (dataPositions.containsKey(data)) {
			return dataPositions.get(data);
		} else return -1; 
	}
	
	public void setIdForTitle(String data, int id) {
		dataPositions.put(data, Integer.valueOf(id));
	}
}