package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerAddresses;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerAddressesSyncObject;
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
import android.net.Uri;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AddressSelectDialog extends DialogFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public static final String EXTRA_CUSTOMER_NO = "rs.gopro.mobile_store.extra.EXTRA_CUSTOMER_NO";
	public static final String EXTRA_CUSTOMER_ID = "rs.gopro.mobile_store.extra.EXTRA_CUSTOMER_ID";

	private Uri mLoadCustomerAddressesUri;
//	private CursorAdapter mAdapter;
	private Button syncAllLinesForDoc;
	private ProgressBar mDialogLoader;
	private String customerNo;
	private String customerId;
	
	private ArrayAdapter<CustomerAddressSelectionEntry> mAdapterForList;
	
	public interface AddressSelectDialogListener {
        void onAddressSelected(int address_id, String address, String address_no, String city, String post_code, String phone_no, String contact);
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
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (mDialogLoader != null) {
				mDialogLoader.setVisibility(View.GONE);
			}
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
		mLoadCustomerAddressesUri = intent.getData();
		customerNo = intent.getCharSequenceExtra(EXTRA_CUSTOMER_NO)
				.toString();
//		customerId = intent.getCharSequenceExtra(
//				EXTRA_CUSTOMER_ID).toString();
//		if (mLoadCustomerAddressesUri == null) {
//			return;
//		}
//		mAdapter = new CustomerAddressLineAdaper(getActivity());
		mAdapterForList = new ArrayAdapter<AddressSelectDialog.CustomerAddressSelectionEntry>(getActivity(), android.R.layout.simple_expandable_list_item_1);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_invoice,
				container);
		ListView listView = (ListView) view
				.findViewById(R.id.invoice_dialog_list);
		// ListView listView2 = new ListView(getActivity());
		listView.setAdapter(mAdapterForList);

		mDialogLoader = (ProgressBar) view
				.findViewById(rs.gopro.mobile_store.R.id.dialog_load_progress);

		syncAllLinesForDoc = (Button) view
				.findViewById(R.id.invoice_lines_sync_button);
		syncAllLinesForDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Cursor cursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(customerId), new String[] { Customers._ID, Customers.CONTACT_COMPANY_NO }, null, null, null);
//				if (cursor.moveToFirst()) {
//					String potentialCustomerNo = cursor.getString(1);
					
					Intent syncAddressIntent = new Intent(getActivity(), NavisionSyncService.class);
					CustomerAddressesSyncObject addressesSyncObject = new CustomerAddressesSyncObject("", customerNo, "", DateUtils.getWsDummyDate());
					syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, addressesSyncObject);
					getActivity().startService(syncAddressIntent);
					
					mDialogLoader.setVisibility(View.VISIBLE);
//				}
//				cursor.close();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				CustomerAddressSelectionEntry selectedEntry = mAdapterForList.getItem(position);
				AddressSelectDialogListener activity = (AddressSelectDialogListener) getActivity();
				activity.onAddressSelected(selectedEntry.getId(), selectedEntry.getAddress(), selectedEntry.getAddress_no(), selectedEntry.getCity(), selectedEntry.getPost_code(), selectedEntry.getPhone_no(), selectedEntry.getContanct());
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
		dialog.setTitle(getString(R.string.invoice_line_dialog_alternative_title));
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
		return new CursorLoader(getActivity(), CustomerAddresses.CONTENT_URI,
				CustomerAddressQuery.PROJECTION, CustomerAddresses.CUSTOMER_NO+"=?", new String[] { customerNo },
				MobileStoreContract.CustomerAddresses.DEFAULT_SORT);
//		query(MobileStoreContract.Customers.buildCustomersUri(customerId), new String[] { Customers._ID, Customers.CONTACT_COMPANY_NO }, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null && data.moveToFirst()) {
			int i = 1;
			mAdapterForList.add(new CustomerAddressSelectionEntry(i++, "Podrazumevana adresa", ""));
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

	
	private class CustomerAddressLineAdaper extends CursorAdapter {

		public CustomerAddressLineAdaper(Context context) {
			super(context, null, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final int address_id = cursor.getInt(CustomerAddressQuery._ID);
			final String address_no = cursor
					.getString(CustomerAddressQuery.ADDRESS_NO);
			final String address = cursor.getString(CustomerAddressQuery.ADDRESS);
			final String city = cursor.getString(CustomerAddressQuery.CITY);
			final String post_code = cursor.getString(CustomerAddressQuery.POST_CODE);
			final String contact = cursor.getString(CustomerAddressQuery.CONTANCT);
			final String phone_no = cursor.getString(CustomerAddressQuery.PHONE_NO);

			TextView title1 = (TextView) view
					.findViewById(R.id.invoice_line_title1);
			TextView title2 = (TextView) view
					.findViewById(R.id.invoice_line_title2);
			TextView subtitle1 = (TextView) view
					.findViewById(R.id.invoice_line_subtitle);
			TextView subtitle2 = (TextView) view
					.findViewById(R.id.invoice_line_subtitle2);
			TextView subtitle3 = (TextView) view
					.findViewById(R.id.invoice_line_subtitle3);

			title1.setText(address);
			title2.setText(address_no);
			subtitle1.setText(city);
			subtitle2.setText(post_code);
			subtitle3.setText(phone_no);
			
			View.OnClickListener allSessionsListener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddressSelectDialogListener activity = (AddressSelectDialogListener) getActivity();
					activity.onAddressSelected(address_id, address, address_no, city, post_code, phone_no, contact);
					AddressSelectDialog.this.dismiss();
				}
			};
			
			view.setOnClickListener(allSessionsListener);
			view.setEnabled(true);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			AddressSelectDialog.this.getDialog().setTitle(
					getString(R.string.invoice_line_dialog_title));
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_invoice_line, parent, false);
		}

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
		
		

		public CustomerAddressSelectionEntry(int line_no, String address,
				String city) {
			super();
			this.line_no = line_no;
			this.address = address;
			this.city = city;
		}

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
		
		public void setId(int _id) {
			this._id = _id;
		}
		
		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public String getCity() {
			return city;
		}
		
		public void setCity(String city) {
			this.city = city;
		}
		
		@Override
		public String toString() {
			return line_no + " - " + address + " - " + city;
		}

		public int getLine_no() {
			return line_no;
		}

		public void setLine_no(int line_no) {
			this.line_no = line_no;
		}

		public String getPost_code() {
			return post_code;
		}

		public void setPost_code(String post_code) {
			this.post_code = post_code;
		}

		public String getPhone_no() {
			return phone_no;
		}

		public void setPhone_no(String phone_no) {
			this.phone_no = phone_no;
		}

		public String getAddress_no() {
			return address_no;
		}

		public void setAddress_no(String address_no) {
			this.address_no = address_no;
		}

		public String getContanct() {
			return contanct;
		}

		public void setContanct(String contanct) {
			this.contanct = contanct;
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

}
