package rs.gopro.mobile_store.ui.components;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Cities;
import rs.gopro.mobile_store.provider.Tables;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

public class CitiesAutocompleteCursorAdapter extends CursorAdapter implements
		Filterable {

	private static final String[] CITY_PROJECTION = new String[] {
		MobileStoreContract.Cities._ID,
		MobileStoreContract.Cities.ZIP,
		MobileStoreContract.Cities.CITY
	};

	private Context mContext;
	private LayoutInflater mInflater;
	
	public CitiesAutocompleteCursorAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}
	
	public CitiesAutocompleteCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater =  LayoutInflater.from(context);
	}

	public CitiesAutocompleteCursorAdapter(Context context, Cursor c,
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
					Cities.buildAutocompleteSearchUri(constraint == null ? ""
							: constraint.toString()), CITY_PROJECTION, null, null, Tables.CITIES + "." + Cities._ID + " ASC");
		}
		return cursor;
	}
	
	private String getResult(Cursor cursor) {
		final int zipIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Cities.ZIP);
		final int cityIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Cities.CITY);
		final String result = cursor.getString(zipIndex) + " - " + cursor.getString(cityIndex);
		return result;
	}

}
