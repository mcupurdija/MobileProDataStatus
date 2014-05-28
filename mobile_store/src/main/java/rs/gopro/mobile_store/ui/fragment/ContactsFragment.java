package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.ui.AddContactActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactsFragment extends Fragment implements LoaderCallbacks<Cursor>, TextWatcher {

	private KontaktiCustomCursorAdapter cursorAdapter;
	private EditText etInputKontakti;
	private ListView lvKontakti;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_kontakti, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		etInputKontakti = (EditText) view.findViewById(R.id.etInputKontakti);
		lvKontakti = (ListView) view.findViewById(R.id.lvKontakti);
		
		etInputKontakti.addTextChangedListener(this);
		
		cursorAdapter = new KontaktiCustomCursorAdapter(getActivity(), R.layout.kontakt_stavka, null, ContactsQuery.PROJECTION, null, 0);
		cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				Cursor cursor = null;
				if (getActivity() != null) {
					cursor = getActivity().getContentResolver().query(Contacts.buildCustomSearchUri(constraint.toString()), ContactsQuery.PROJECTION, null, null, MobileStoreContract.Contacts.DEFAULT_SORT);
				}
				return cursor;
			}
		});
		
		lvKontakti.setAdapter(cursorAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), MobileStoreContract.Contacts.CONTENT_URI, ContactsQuery.PROJECTION, null, null, MobileStoreContract.Contacts.DEFAULT_SORT);
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
	
	private class KontaktiCustomCursorAdapter extends SimpleCursorAdapter {

		public KontaktiCustomCursorAdapter(Context context, int layout,
				Cursor c, String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			final String id = cursor.getString(ContactsQuery._ID);
			final String ime = cursor.getString(ContactsQuery.NAME);
			final String phone = cursor.getString(ContactsQuery.PHONE);
			final String phone2 = cursor.getString(ContactsQuery.MOBILE_PHONE);
			final String email = cursor.getString(ContactsQuery.EMAIL);
			TextView tvKontaktIme = (TextView) view.findViewById(R.id.tvKontaktIme);
			TextView tvKontaktTelefon = (TextView) view.findViewById(R.id.tvKontaktTelefon);
			TextView tvKontaktEmail = (TextView) view.findViewById(R.id.tvKontaktEmail);
			//RelativeLayout layoutKontaktInfo = (RelativeLayout) view.findViewById(R.id.layoutKontaktInfo);
			RelativeLayout layoutKontaktEdit = (RelativeLayout) view.findViewById(R.id.layoutKontaktEdit);
			RelativeLayout layoutKontaktDelete = (RelativeLayout) view.findViewById(R.id.layoutKontaktDelete);
			
			tvKontaktIme.setText(ime);
			if (!phone.isEmpty() && !phone2.isEmpty()) {
				tvKontaktTelefon.setText(phone + "; " + phone2);
			} else if (!phone.isEmpty()) {
				tvKontaktTelefon.setText(phone);
			} else if (!phone2.isEmpty()) {
				tvKontaktTelefon.setText(phone2);
			}
			tvKontaktEmail.setText(email);
			
			layoutKontaktEdit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent editContactIntent = new Intent(getActivity(), AddContactActivity.class);
					editContactIntent.putExtra(AddContactActivity.CONTACT_ID, id);
					startActivity(editContactIntent);
				}
			});
			layoutKontaktDelete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ConfirmDialog(id, ime);
				}
			});
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.kontakt_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}
	}
	
	private void ConfirmDialog(final String id, final String ime) {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
	    adb.setTitle(getString(R.string.potvrda_brisanja) + " [" + ime + "]");

	    adb.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	getActivity().getContentResolver().delete(MobileStoreContract.Contacts.buildContactsUri(id), null, null);
	        }
	    });

	    adb.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	        }
	    });
	    adb.show();
	}
	
	private interface ContactsQuery {
		String[] PROJECTION = { MobileStoreContract.Contacts._ID,
				MobileStoreContract.Contacts.CONTACT_NO,
				MobileStoreContract.Contacts.NAME,
				MobileStoreContract.Contacts.PHONE,
				MobileStoreContract.Contacts.MOBILE_PHONE,
				MobileStoreContract.Contacts.EMAIL };
		
		int _ID = 0;
//		int CONTACT_NO = 1;
		int NAME = 2;
		int PHONE = 3;
		int MOBILE_PHONE = 4;
		int EMAIL = 5;
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

}
