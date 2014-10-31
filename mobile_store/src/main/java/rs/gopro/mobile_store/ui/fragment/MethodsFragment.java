package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.Methods;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MethodsFragment extends Fragment implements LoaderCallbacks<Cursor>, TextWatcher {

	private String searchQuery;
	
	private MetodeCustomCursorAdapter cursorAdapter;
	private EditText etInputMetode;
	private ListView lvMetode;
	
	private MainContextualActionBarCallback actionBarCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_metode, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		etInputMetode = (EditText) view.findViewById(R.id.etInputMetode);
		lvMetode = (ListView) view.findViewById(R.id.lvMetode);
		
		etInputMetode.addTextChangedListener(this);
		
		cursorAdapter = new MetodeCustomCursorAdapter(getActivity());
		lvMetode.setAdapter(cursorAdapter);
		lvMetode.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				long methodId = cursorAdapter.getItemId(position);
				infoDijalog(methodId);
			}
		});
		
		lvMetode.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				long methodId = cursorAdapter.getItemId(position);
				actionBarCallback.onLongClickItem(String.valueOf(methodId), null);
				return true;
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		actionBarCallback = (MainContextualActionBarCallback) activity;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (searchQuery != null) {
			return new CursorLoader(getActivity(), Methods.buildMethodsSearchUri(searchQuery), MethodQuery.PROJECTION, null, null, null);
		}
		return new CursorLoader(getActivity(), Methods.CONTENT_URI, MethodQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursorAdapter != null) {
			cursorAdapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (cursorAdapter != null) {
			cursorAdapter.swapCursor(null);
		}
	}
	
	@Override
	public void afterTextChanged(Editable editable) {
		String query = editable.toString();
		if (query.trim().length() > 0) {
			searchQuery = query;
		} else {
			searchQuery = null;
		}
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	private class MetodeCustomCursorAdapter extends CursorAdapter {

		public MetodeCustomCursorAdapter(Context context) {
			super(context, null, false);
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.list_item_sale_order_block, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			
			final String itemNo = cursor.getString(MethodQuery.ITEM_NO);
			final String itemDescription = cursor.getString(MethodQuery.ITEM_DESCRIPTION);
			final String predmet = cursor.getString(MethodQuery.SUBJECT);
			final int razred = cursor.getInt(MethodQuery.CLASS);
			
			String[] razredArray = getResources().getStringArray(R.array.skola_razred_array);

			TextView no = (TextView) view.findViewById(R.id.block_time);
			TextView title = (TextView) view.findViewById(R.id.block_title);
        	TextView subtitle = (TextView) view.findViewById(R.id.block_subtitle);
        	
        	no.setText(itemNo);
        	title.setText(itemDescription);
        	subtitle.setText(String.format("%s %s", predmet, razredArray[razred]));
		}
		
	}
	
	public void infoDijalog(long methodId) {
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_kontakt_info);
		
		String itemNo, skolaCustomerNo, profesorCustomerNo, profesor2CustomerNo, predmet, skolskaGodina, komentar;
		int razred, velicinaSkole;
		
		TextView tvKontaktInfoIme2 = (TextView) dialog.findViewById(R.id.tvKontaktInfoIme2);
		TextView tvKontaktInfoTelefon = (TextView) dialog.findViewById(R.id.tvKontaktInfoTelefon);
		TextView tvKontaktInfoEmail = (TextView) dialog.findViewById(R.id.tvKontaktInfoEmail);
		TextView tvKontaktInfoKompanija = (TextView) dialog.findViewById(R.id.tvKontaktInfoKompanija);
		TextView tvKontaktInfoOdeljenje = (TextView) dialog.findViewById(R.id.tvKontaktInfoOdeljenje);
		TextView tvKontaktInfoPozicija = (TextView) dialog.findViewById(R.id.tvKontaktInfoPozicija);
		TextView tvKontaktInfoExtra = (TextView) dialog.findViewById(R.id.tvKontaktInfoExtra);

		Cursor cursor = getActivity().getContentResolver().query(Methods.buildMethodsUri((int)methodId), MethodQuery.PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			
			predmet = cursor.getString(MethodQuery.SUBJECT);
			razred = cursor.getInt(MethodQuery.CLASS);
			velicinaSkole = cursor.getInt(MethodQuery.SCHOOL_SIZE);
			skolskaGodina = cursor.getString(MethodQuery.SCHOOL_YEAR);
			komentar = cursor.getString(MethodQuery.COMMENT);
			
			String[] razredArray = getResources().getStringArray(R.array.skola_razred_array);
			String[] velicinaArray = getResources().getStringArray(R.array.skola_velicina_array);
			
			itemNo = cursor.getString(MethodQuery.ITEM_NO);
			if (itemNo != null && !itemNo.isEmpty()) {
				dialog.setTitle(String.format("%s - %s", itemNo, cursor.getString(MethodQuery.ITEM_DESCRIPTION)));
			} else {
				dialog.setTitle("");
			}
			
			skolaCustomerNo = cursor.getString(MethodQuery.SCHOOL_CUSTOMER_NO);
			if (skolaCustomerNo != null && !skolaCustomerNo.isEmpty()) {
				tvKontaktInfoIme2.setText(String.format("%s:\t\t\t\t\t\t%s - %s, %s", getString(R.string.method_label_skola), skolaCustomerNo, cursor.getString(MethodQuery.SCHOOL_CUSTOMER_NAME), velicinaArray[velicinaSkole]));
				tvKontaktInfoIme2.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoIme2.setVisibility(View.GONE);
			}
			
			profesorCustomerNo = cursor.getString(MethodQuery.PROFESSOR1_CUSTOMER_NO);
			if (profesorCustomerNo != null && !profesorCustomerNo.isEmpty()) {
				tvKontaktInfoKompanija.setText(String.format("%s:\t\t\t\t%s - %s", getString(R.string.method_label_profesor1), profesorCustomerNo, cursor.getString(MethodQuery.PROFESSOR1_CUSTOMER_NAME)));
				tvKontaktInfoKompanija.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoKompanija.setVisibility(View.GONE);
			}
			
			profesor2CustomerNo = cursor.getString(MethodQuery.PROFESSOR2_CUSTOMER_NO);
			if (profesor2CustomerNo != null && !profesor2CustomerNo.isEmpty()) {
				tvKontaktInfoOdeljenje.setText(String.format("%s:\t\t\t\t%s - %s", getString(R.string.method_label_profesor1), profesor2CustomerNo, cursor.getString(MethodQuery.PROFESSOR2_CUSTOMER_NAME)));
				tvKontaktInfoOdeljenje.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoOdeljenje.setVisibility(View.GONE);
			}
			
			if (predmet != null && !predmet.isEmpty()) {
				tvKontaktInfoTelefon.setText(String.format("%s:\t\t\t\t\t%s", getString(R.string.method_label_predmet), predmet));
				tvKontaktInfoTelefon.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoTelefon.setVisibility(View.GONE);
			}
			
			if (razred > -1) {
				tvKontaktInfoEmail.setText(String.format("%s:\t\t\t\t\t%s", getString(R.string.method_label_razred), razredArray[razred]));
				tvKontaktInfoEmail.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoEmail.setVisibility(View.GONE);
			}
			
			if (skolskaGodina != null && !skolskaGodina.isEmpty()) {
				tvKontaktInfoPozicija.setText(String.format("%s:\t%s", getString(R.string.method_label_skolska_godina), skolskaGodina));
				tvKontaktInfoPozicija.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoPozicija.setVisibility(View.GONE);
			}
			
			if (komentar != null && !komentar.isEmpty()) {
				tvKontaktInfoExtra.setText(String.format("%s:\t\t\t\t%s", getString(R.string.method_label_komentar), komentar));
				tvKontaktInfoExtra.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoExtra.setVisibility(View.GONE);
			}
		}
		cursor.close();
		dialog.show();
	}
	
	private interface MethodQuery {
		String[] PROJECTION = { 
			Tables.METHODS + "." + BaseColumns._ID,
			Tables.METHODS + "." + Methods.SUBJECT,
			Tables.METHODS + "." + Methods.CLASS,
			Tables.METHODS + "." + Methods.SCHOOL_SIZE,
			Tables.METHODS + "." + Methods.SCHOOL_YEAR,
			Tables.METHODS + "." + Methods.COMMENT,
			Tables.ITEMS + "." + Items.ITEM_NO, 
			Tables.ITEMS + "." + Items.DESCRIPTION,
			"SCH." + Customers.CUSTOMER_NO,
			"SCH." + Customers.NAME,
			"PR1." + Customers.CUSTOMER_NO,
			"PR1." + Customers.NAME,
			"PR2." + Customers.CUSTOMER_NO,
			"PR2." + Customers.NAME
		};
		
//		int _ID = 0;
		int SUBJECT = 1;
		int CLASS = 2;
		int SCHOOL_SIZE = 3;
		int SCHOOL_YEAR = 4;
		int COMMENT = 5;
		int ITEM_NO = 6;
		int ITEM_DESCRIPTION = 7;
		int SCHOOL_CUSTOMER_NO = 8;
		int SCHOOL_CUSTOMER_NAME = 9;
		int PROFESSOR1_CUSTOMER_NO = 10;
		int PROFESSOR1_CUSTOMER_NAME = 11;
		int PROFESSOR2_CUSTOMER_NO = 12;
		int PROFESSOR2_CUSTOMER_NAME = 13;
	}

}
