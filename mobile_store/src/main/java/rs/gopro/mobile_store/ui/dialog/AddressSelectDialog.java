package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerAddresses;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerAddressesSyncObject;
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

public class AddressSelectDialog extends DialogFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final String EXTRA_DIALOG_ID = "rs.gopro.mobile_store.extra.DIALOG_ID";
	public static final String EXTRA_CUSTOMER_NO = "rs.gopro.mobile_store.extra.EXTRA_CUSTOMER_NO";

	private Button syncAllLinesForDoc;
	private ProgressBar mDialogLoader;
	private String customerNo;
	private int dialogId;
	
	private String default_address = "";
	private String default_city = "";
	private String default_post_code = "";
	
	private ArrayAdapter<CustomerAddressSelectionEntry> mAdapterForList;
	
	public interface AddressSelectDialogListener {
        void onAddressSelected(int dialogId, int address_id, String address, String address_no, String city, String post_code, String phone_no, String contact);
    }
	
	public AddressSelectDialog() {
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
		// success or error we should hide progress
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
		customerNo = intent.getStringExtra(EXTRA_CUSTOMER_NO);
		dialogId = intent.getIntExtra(EXTRA_DIALOG_ID, 0);

		mAdapterForList = new ArrayAdapter<AddressSelectDialog.CustomerAddressSelectionEntry>(getActivity(), android.R.layout.simple_expandable_list_item_1);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("DIALOG_ID", dialogId);
		outState.putString("CUSTOMER_NO", customerNo);
		
		outState.putString("DEFAULT_ADDRESS", default_address);
		outState.putString("DEFAULT_CITY", default_city);
		outState.putString("DEFAULT_POST_CODE", default_post_code);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			dialogId = savedInstanceState.getInt("DIALOG_ID");
			customerNo = savedInstanceState.getString("CUSTOMER_NO");
			
			default_address = savedInstanceState.getString("DEFAULT_ADDRESS");
			default_city = savedInstanceState.getString("DEFAULT_CITY");
			default_post_code = savedInstanceState.getString("DEFAULT_POST_CODE");
		}
		View view = inflater.inflate(R.layout.dialog_fragment_customer_address_selector,
				container);
		ListView listView = (ListView) view
				.findViewById(R.id.customer_address_dialog_list);
		listView.setAdapter(mAdapterForList);

		mDialogLoader = (ProgressBar) view
				.findViewById(R.id.customer_address_dialog_load_progress);

		syncAllLinesForDoc = (Button) view
				.findViewById(R.id.dialog_customer_address_sync_button);
		syncAllLinesForDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent syncAddressIntent = new Intent(getActivity(), NavisionSyncService.class);
				CustomerAddressesSyncObject addressesSyncObject = new CustomerAddressesSyncObject("", customerNo, "", DateUtils.getWsDummyDate());
				syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, addressesSyncObject);
				getActivity().startService(syncAddressIntent);
				
				mDialogLoader.setVisibility(View.VISIBLE);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				CustomerAddressSelectionEntry selectedEntry = mAdapterForList.getItem(position);
				AddressSelectDialogListener activity = (AddressSelectDialogListener) getActivity();
				activity.onAddressSelected(dialogId, selectedEntry.getId(), selectedEntry.getAddress(), selectedEntry.getCity(), selectedEntry.getPost_code(), selectedEntry.getPhone_no(), selectedEntry.getAddress_no(), selectedEntry.getContanct());
				AddressSelectDialog.this.dismiss();
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
		dialog.setTitle(getString(R.string.title_address_selector));
		return dialog;
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter getAddressSyncObject = new IntentFilter(
				CustomerAddressesSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				onNotice, getAddressSyncObject);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				onNotice);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), CustomerAddresses.CONTENT_URI,
				CustomerAddressQuery.PROJECTION, CustomerAddresses.CUSTOMER_NO+"=?", new String[] { customerNo },
				MobileStoreContract.CustomerAddresses.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null && data.moveToFirst()) {
			mAdapterForList.clear();
			int i = 1;
			mAdapterForList.add(new CustomerAddressSelectionEntry(-1, i++, default_address, default_city, default_post_code, "", "", ""));
			do {
				int address_id = data.getInt(CustomerAddressQuery._ID);
				String address_no = data.getString(CustomerAddressQuery.ADDRESS_NO);
				String address = ""; String city = "";
				String post_code = data.getString(CustomerAddressQuery.POST_CODE);
				String contact = data.getString(CustomerAddressQuery.CONTANCT);
				String phone_no = data.getString(CustomerAddressQuery.PHONE_NO);
				if (!data.isNull(CustomerAddressQuery.ADDRESS)) {
					address = data.getString(CustomerAddressQuery.ADDRESS);
				}
				if (!data.isNull(CustomerAddressQuery.CITY)) {
					city = data.getString(CustomerAddressQuery.CITY);
				}
				mAdapterForList.add(new CustomerAddressSelectionEntry(address_id, i++, address, city, post_code, phone_no, address_no, contact));
			} while(data.moveToNext());
			
			data.close();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapterForList.clear();
	}

	private class CustomerAddressSelectionEntry {
		
		private int _id;
		private int line_no;
		private String address;
		private String city;
		
		private String post_code;
		private String phone_no;
		private String address_no;
		private String contanct;
		
//		public CustomerAddressSelectionEntry(int _id, int line_no, String address,
//				String city) {
//			super();
//			this._id = _id;
//			this.line_no = line_no;
//			this.address = address;
//			this.city = city;
//		}

		public CustomerAddressSelectionEntry(int _id, int line_no,
				String address, String city, String post_code, String phone_no,
				String address_no, String contanct) {
			super();
			this._id = _id;
			this.line_no = line_no;
			this.address = address;
			this.city = city;
			this.post_code = post_code;
			this.phone_no = phone_no;
			this.address_no = address_no;
			this.contanct = contanct;
		}

		public int getId() {
			return _id;
		}
		
		public String getAddress() {
			return address;
		}
		
		public String getCity() {
			return city;
		}
		
		@Override
		public String toString() {
			if (_id == -1) {
				return "(* Primarna) " + address_no + " - " + address + "\n" + city + " - " + post_code;
			}
			return address_no + " - " + address + "\n" + city + " - " + post_code;
		}

		public String getPost_code() {
			return post_code;
		}

		public String getPhone_no() {
			return phone_no;
		}

		public String getAddress_no() {
			return address_no;
		}

		public String getContanct() {
			return contanct;
		}
	}
	
	private interface CustomerAddressQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.CustomerAddresses.ADDRESS_NO,
				MobileStoreContract.CustomerAddresses.ADDRESS,
				MobileStoreContract.CustomerAddresses.CITY,
				MobileStoreContract.CustomerAddresses.POST_CODE,
				MobileStoreContract.CustomerAddresses.CONTANCT,
				MobileStoreContract.CustomerAddresses.PHONE_NO };

		int _ID = 0;
		int ADDRESS_NO = 1;
		int ADDRESS = 2;
		int CITY = 3;
		int POST_CODE = 4;
		int CONTANCT = 5;
		int PHONE_NO = 6;
	}

	public String getDefault_address() {
		return default_address;
	}

	public void setDefault_address(String default_address) {
		this.default_address = default_address;
	}

	public String getDefault_city() {
		return default_city;
	}

	public void setDefault_city(String default_city) {
		this.default_city = default_city;
	}

	public String getDefault_post_code() {
		return default_post_code;
	}

	public void setDefault_post_code(String default_post_code) {
		this.default_post_code = default_post_code;
	}
}
