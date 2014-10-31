package rs.gopro.mobile_store.ui.components;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.Tables;
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
					Items.buildAutocompleteSearchUri(constraint == null ? "" : constraint.toString()), ITEM_PROJECTION,
					null, null, Tables.ITEMS+"." + Items.ITEM_NO + " ASC");
		}
		return cursor;
	}
	
	private String getResult(Cursor cursor) {
		final int codeIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Items.ITEM_NO);
		final int nameIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Items.DESCRIPTION);
		final String result = cursor.getString(codeIndex) + " - " + cursor.getString(nameIndex);
		return result;
	}

}
