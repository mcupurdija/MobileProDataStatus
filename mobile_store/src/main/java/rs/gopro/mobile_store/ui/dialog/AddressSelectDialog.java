package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerAddresses;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
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
	private CursorAdapter mAdapter;
	private Button syncAllLinesForDoc;
	private ProgressBar mDialogLoader;
	private String customerNo;
	private String customerId;
	
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
		customerId = intent.getCharSequenceExtra(
				EXTRA_CUSTOMER_ID).toString();
//		if (mLoadCustomerAddressesUri == null) {
//			return;
//		}
		mAdapter = new CustomerAddressLineAdaper(getActivity());
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
		listView.setAdapter(mAdapter);

		mDialogLoader = (ProgressBar) view
				.findViewById(R.id.dialog_load_progress);

		syncAllLinesForDoc = (Button) view
				.findViewById(R.id.invoice_lines_sync_button);
		syncAllLinesForDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Cursor cursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.buildCustomersUri(customerId), new String[] { Customers._ID, Customers.CONTACT_COMPANY_NO }, null, null, null);
				if (cursor.moveToFirst()) {
					String potentialCustomerNo = cursor.getString(1);
					Intent syncAddressIntent = new Intent(getActivity(), NavisionSyncService.class);
					GetContactsSyncObject contactsSyncObject = new GetContactsSyncObject("", "", potentialCustomerNo, "", DateUtils.getWsDummyDate());
					syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, contactsSyncObject);
					getActivity().startService(syncAddressIntent);
					mDialogLoader.setVisibility(View.VISIBLE);
				}
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
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
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
