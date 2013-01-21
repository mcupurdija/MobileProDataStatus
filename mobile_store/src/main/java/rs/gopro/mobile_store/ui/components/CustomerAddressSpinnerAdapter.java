package rs.gopro.mobile_store.ui.components;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomerAddressSpinnerAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	
	//private Map<Integer, Integer> dataPositions = new HashMap<Integer, Integer>();
	private SparseIntArray dataPositions = new SparseIntArray();
	
	public CustomerAddressSpinnerAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater =  LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final TextView view = (TextView) mInflater.inflate(android.R.layout.simple_spinner_item, parent, false);
		view.setText(getResult(cursor));
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view).setText(getResult(cursor));
	}
	
	@Override
	public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
		final TextView view = (TextView) mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		view.setText(getResult(cursor));
		return view;
	}
	
	private String getResult(Cursor cursor) {
		dataPositions.put(cursor.getInt(cursor.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses._ID)), cursor.getPosition());
		final int codeIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.ADDRESS_NO);
		final int nameIndex = cursor
				.getColumnIndexOrThrow(MobileStoreContract.CustomerAddresses.ADDRESS);
		final String result = cursor.getString(codeIndex) + " - "
				+ cursor.getString(nameIndex);
		return result;
	}
	
	public int getIdPostition(int id) {
		return dataPositions.get(id, -1);
	}

}
