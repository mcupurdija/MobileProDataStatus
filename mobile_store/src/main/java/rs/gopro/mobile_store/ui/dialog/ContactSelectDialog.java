package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.GetContactsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class ContactSelectDialog extends DialogFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final String EXTRA_DIALOG_ID = "rs.gopro.mobile_store.extra.DIALOG_ID";
	public static final String EXTRA_POTENTIAL_CUSTOMER_NO = "rs.gopro.mobile_store.extra.EXTRA_POTENTIAL_CUSTOMER_NO";

	private Button syncAllLinesForDoc;
	private ProgressBar mDialogLoader;
	private String potentialCustomerNo;
	private int dialogId;
	private String defaultName = "";
	private String defaultPhone_no = "";
	private String defaultEmail = "";
	
	private ArrayAdapter<CustomerContactSelectionEntry> mAdapterForList;
	
	public interface ContactSelectDialogListener {
        void onContactSelected(int dialogId, int contact_id, String contact_name, String contact_phone, String contact_email);
    }
	
	public ContactSelectDialog() {
	}

	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent
					.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			// itemLoadProgressDialog.dismiss();
			onSOAPResult(syncResult, intent.getAction());
		}
	};

	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (mDialogLoader != null) {
			mDialogLoader.setVisibility(View.GONE);
		}
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			getLoaderManager().restartLoader(0, null, this);
		} else {
			this.dismiss();
			AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
					.create();

			// Setting Dialog Title
			alertDialog.setTitle(getResources().getString(
					R.string.dialog_title_error_in_sync));
			// Setting Dialog Message
			alertDialog.setMessage(syncResult.getResult());
			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.ic_launcher);
			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to execute after dialog closed
				}
			});

			// Showing Alert Message
			alertDialog.show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
		reloadFromArguments(getArguments());
	}

	public void reloadFromArguments(Bundle arguments) {
		// Load new arguments
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
//		mLoadCustomerAddressesUri = intent.getData();
		potentialCustomerNo = intent.getStringExtra(EXTRA_POTENTIAL_CUSTOMER_NO);
		dialogId = intent.getIntExtra(EXTRA_DIALOG_ID, 0);

		mAdapterForList = new ArrayAdapter<ContactSelectDialog.CustomerContactSelectionEntry>(getActivity(), android.R.layout.simple_expandable_list_item_1);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("DIALOG_ID", dialogId);
		outState.putString("POTENTIAL_CUSTOMER_NO", potentialCustomerNo);
		
		outState.putString("DEFAULT_NAME", defaultName);
		outState.putString("DEFAULT_PHONE_NO", defaultPhone_no);
		outState.putString("DEFAULT_EMAIL", defaultEmail);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			dialogId = savedInstanceState.getInt("DIALOG_ID");
			potentialCustomerNo = savedInstanceState.getString("POTENTIAL_CUSTOMER_NO");
			
			defaultName = savedInstanceState.getString("DEFAULT_NAME");
			defaultPhone_no = savedInstanceState.getString("DEFAULT_PHONE_NO");
			defaultEmail = savedInstanceState.getString("DEFAULT_EMAIL");
		}
		View view = inflater.inflate(R.layout.dialog_fragment_contact_selector,
				container);
		ListView listView = (ListView) view
				.findViewById(R.id.contact_dialog_list);
		listView.setAdapter(mAdapterForList);

		mDialogLoader = (ProgressBar) view
				.findViewById(rs.gopro.mobile_store.R.id.contact_dialog_load_progress);

		syncAllLinesForDoc = (Button) view
				.findViewById(R.id.dialog_contact_sync_button);
		syncAllLinesForDoc.setText(getResources().getString(R.string.dialog_customer_contacts_sync));
		syncAllLinesForDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent syncContactIntent = new Intent(getActivity(), NavisionSyncService.class);
				GetContactsSyncObject contactsSyncObject = new GetContactsSyncObject("", "", potentialCustomerNo, "", DateUtils.getWsDummyDate());
				syncContactIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, contactsSyncObject);
				getActivity().startService(syncContactIntent);
				
				mDialogLoader.setVisibility(View.VISIBLE);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				CustomerContactSelectionEntry selectedEntry = mAdapterForList.getItem(position);
				ContactSelectDialogListener activity = (ContactSelectDialogListener) getActivity();
				activity.onContactSelected(dialogId, selectedEntry.getId(), selectedEntry.getName(), selectedEntry.getPhone_no(), selectedEntry.getEmail());
				ContactSelectDialog.this.dismiss();
			}
		});
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(getString(R.string.title_contact_selector));
		return dialog;
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter getContactsSyncObject = new IntentFilter(
				GetContactsSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				onNotice, getContactsSyncObject);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				onNotice);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), Contacts.CONTENT_URI,
				CustomerContactQuery.PROJECTION, Contacts.COMPANY_NO+"=?", new String[] { potentialCustomerNo },
				MobileStoreContract.Contacts.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null && data.moveToFirst()) {
			mAdapterForList.clear();
			int i = 1;
			mAdapterForList.add(new CustomerContactSelectionEntry(-1, i++, defaultName, defaultPhone_no, defaultEmail));
			do {
				int contact_id = data.getInt(CustomerContactQuery._ID);
				String name = data.getString(CustomerContactQuery.NAME);
				String phone =  data.getString(CustomerContactQuery.PHONE);
				String email = data.getString(CustomerContactQuery.EMAIL);
				
				mAdapterForList.add(new CustomerContactSelectionEntry(contact_id, i++, name, phone, email));
			} while(data.moveToNext());
			
			data.close();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapterForList.clear();
	}

	private class CustomerContactSelectionEntry {
		
		private int _id;
		private int line_no;
		private String name;
		
		private String phone_no;
		private String email;
		
		

		public CustomerContactSelectionEntry(int _id, int line_no, String name, String phone,
				String email) {
			super();
			this._id = _id;
			this.line_no = line_no;
			this.name = name;
			this.phone_no = phone;
			this.email = email;
		}

		public int getId() {
			return _id;
		}
		
		public String getName() {
			return name;
		}
		

		@Override
		public String toString() {
			if (_id == -1) {
				return "(* Primaran) " + cleanToString(name, email, phone_no);
			}
			return cleanToString(name, email, phone_no);
		}

		private String cleanToString(String name, String email, String phone_no) {
			StringBuilder contactCaption = new StringBuilder();
			if (name != null && name.length() > 0) {
				contactCaption.append(name);
			}
			if (phone_no != null && phone_no.length() > 0) {
				contactCaption.append(" - ").append(phone_no);
			}
			if (email != null && email.length() > 0) {
				contactCaption.append(" - ").append(email);
			}
			return contactCaption.toString();
		}
		
		public String getPhone_no() {
			return phone_no;
		}

		public String getEmail() {
			return email;
		}
	}
	
	private interface CustomerContactQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.Contacts.NAME,
				MobileStoreContract.Contacts.NAME2,
				MobileStoreContract.Contacts.PHONE,
				MobileStoreContract.Contacts.EMAIL 
		};

		int _ID = 0;
		int NAME = 1;
//		int NAME2 = 2;
		int PHONE = 3;
		int EMAIL = 4;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getDefaultPhone_no() {
		return defaultPhone_no;
	}

	public void setDefaultPhone_no(String defaultPhone_no) {
		this.defaultPhone_no = defaultPhone_no;
	}

	public String getDefaultEmail() {
		return defaultEmail;
	}

	public void setDefaultEmail(String defaultEmail) {
		this.defaultEmail = defaultEmail;
	}
}
