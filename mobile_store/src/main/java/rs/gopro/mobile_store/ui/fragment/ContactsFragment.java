package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsFragment extends Fragment implements LoaderCallbacks<Cursor>, TextWatcher {

	private KontaktiCustomCursorAdapter cursorAdapter;
	private EditText etInputKontakti;
	private ListView lvKontakti;
	
	private MainContextualActionBarCallback actionBarCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_kontakti, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		etInputKontakti = (EditText) view.findViewById(R.id.etInputKontakti);
		lvKontakti = (ListView) view.findViewById(R.id.lvKontakti);
		
		etInputKontakti.addTextChangedListener(this);
		
		cursorAdapter = new KontaktiCustomCursorAdapter(getActivity(), R.layout.list_item_sale_order_block, null, ContactsQuery.PROJECTION, null, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				Cursor cursor = null;
				if (getActivity() != null) {
					if (constraint.toString().trim().length() > 0) {
						cursor = getActivity().getContentResolver().query(Contacts.buildCustomSearchUri(constraint.toString()), ContactsQuery.PROJECTION, null, null, Contacts.DEFAULT_SORT);
					} else {
						cursor = getActivity().getContentResolver().query(Contacts.CONTENT_URI, ContactsQuery.PROJECTION, null, null, Contacts.DEFAULT_SORT);
					}
				}
				return cursor;
			}
		});
		
		lvKontakti.setAdapter(cursorAdapter);
		lvKontakti.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				long contactId = cursorAdapter.getItemId(position);
				infoDijalog(contactId);
			}
		});
		
		lvKontakti.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				long contactId = cursorAdapter.getItemId(position);
				actionBarCallback.onLongClickItem(String.valueOf(contactId), null);
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
		CursorLoader cursorLoader = new CursorLoader(getActivity(), Contacts.CONTENT_URI, ContactsQuery.PROJECTION, null, null, Contacts.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(!cursor.isClosed()) {
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
		if (editable.toString().trim().length() == 0) {
			cursorAdapter.getFilter().filter("noNoOrName");
		} else {
			cursorAdapter.getFilter().filter(editable.toString());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	private class KontaktiCustomCursorAdapter extends SimpleCursorAdapter {

		public KontaktiCustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			
			final String contactNo = cursor.getString(ContactsQuery.CONTACT_NO);
			final String contactName = cursor.getString(ContactsQuery.NAME);
			final String phone = cursor.getString(ContactsQuery.PHONE);

			TextView no = (TextView) view.findViewById(R.id.block_time);
			TextView title = (TextView) view.findViewById(R.id.block_title);
        	TextView subtitle = (TextView) view.findViewById(R.id.block_subtitle);
			
        	if (contactNo != null && !contactNo.isEmpty()) {
        		no.setText(contactNo);
			}
        	
        	title.setText(contactName);
        	
			if (phone != null && !phone.isEmpty()) {
				subtitle.setText(phone);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.list_item_sale_order_block, parent, false);
			bindView(v, context, cursor);
			return v;
		}
	}
	
	public void infoDijalog(long contactId) {
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_kontakt_info);
		
		String name = null, name2, phone, email, companyNo = null;
		int department, position;
		String[] departmantArray = getResources().getStringArray(R.array.contact_department_array);
		String[] positionArray = getResources().getStringArray(R.array.contact_position_array);
		
		TextView tvKontaktInfoIme2 = (TextView) dialog.findViewById(R.id.tvKontaktInfoIme2);
		TextView tvKontaktInfoTelefon = (TextView) dialog.findViewById(R.id.tvKontaktInfoTelefon);
		TextView tvKontaktInfoEmail = (TextView) dialog.findViewById(R.id.tvKontaktInfoEmail);
		TextView tvKontaktInfoKompanija = (TextView) dialog.findViewById(R.id.tvKontaktInfoKompanija);
		TextView tvKontaktInfoOdeljenje = (TextView) dialog.findViewById(R.id.tvKontaktInfoOdeljenje);
		TextView tvKontaktInfoPozicija = (TextView) dialog.findViewById(R.id.tvKontaktInfoPozicija);

		Cursor cursor = getActivity().getContentResolver().query(Contacts.buildContactsUri(String.valueOf(contactId)), ContactsQuery.PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			name = cursor.getString(ContactsQuery.NAME);
			name2 = cursor.getString(ContactsQuery.NAME2);
			phone = cursor.getString(ContactsQuery.PHONE);
			email = cursor.getString(ContactsQuery.EMAIL);
			companyNo = cursor.getString(ContactsQuery.COMPANY_NO);
			department = cursor.getInt(ContactsQuery.DEPARTMENT);
			position = cursor.getInt(ContactsQuery.POSITION);

			if (name2 != null && !name2.isEmpty()) {
				tvKontaktInfoIme2.setText(String.format("%s\t\t\t\t%s", "Ime 2:", name2));
				tvKontaktInfoIme2.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoIme2.setVisibility(View.GONE);
			}
			
			if (phone != null && !phone.isEmpty()) {
				tvKontaktInfoTelefon.setText(String.format("%s\t\t\t%s", "Telefon:", phone));
				tvKontaktInfoTelefon.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoTelefon.setVisibility(View.GONE);
			}
			
			if (email != null && !email.isEmpty()) {
				tvKontaktInfoEmail.setText(String.format("%s\t\t\t\t%s", "Email:", email));
				tvKontaktInfoEmail.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoEmail.setVisibility(View.GONE);
			}
			
			if (department > 0) {
				tvKontaktInfoOdeljenje.setText(String.format("%s\t\t%s", "Odeljenje:", departmantArray[department]));
				tvKontaktInfoOdeljenje.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoOdeljenje.setVisibility(View.GONE);
			}
			
			if (position > 0) {
				tvKontaktInfoPozicija.setText(String.format("%s\t\t\t%s", "Pozicija:", positionArray[position]));
				tvKontaktInfoPozicija.setVisibility(View.VISIBLE);
			} else {
				tvKontaktInfoPozicija.setVisibility(View.GONE);
			}
		}
		if (companyNo != null) {
			cursor = getActivity().getContentResolver().query(Customers.CONTENT_URI, CustomerQuery.PROJECTION, Customers.CUSTOMER_NO + "=?", new String[] { companyNo }, null);
			if (cursor.moveToFirst()) {
				tvKontaktInfoKompanija.setText(String.format("%s\t%s - %s", "Kompanija:", cursor.getString(CustomerQuery.CUSTOMER_NO), cursor.getString(CustomerQuery.NAME)));
				tvKontaktInfoKompanija.setVisibility(View.VISIBLE);
			}
		} else {
			tvKontaktInfoKompanija.setVisibility(View.GONE);
		}
		cursor.close();
		
		if (name != null) {
			dialog.setTitle(name);
		}
		dialog.show();
	}
	
	private interface ContactsQuery {
		String[] PROJECTION = { 
				Contacts._ID,
				Contacts.CONTACT_NO,
				Contacts.NAME,
				Contacts.NAME2,
				Contacts.PHONE,
				Contacts.EMAIL,
				Contacts.DEPARTMENT,
				Contacts.POSITION,
				Contacts.COMPANY_NO
		};
		
//		int _ID = 0;
		int CONTACT_NO = 1;
		int NAME = 2;
		int NAME2 = 3;
		int PHONE = 4;
		int EMAIL = 5;
		int DEPARTMENT = 6;
		int POSITION = 7;
		int COMPANY_NO = 8;
	}
	
	private interface CustomerQuery {
		String[] PROJECTION = { 
				Customers._ID,
				Customers.CUSTOMER_NO,
				Customers.NAME
		};
		
//		int _ID = 0;
		int CUSTOMER_NO = 1;
		int NAME = 2;
	}

}
