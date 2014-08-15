package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerBusinessUnitsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.AlertDialog;
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
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BusinessUnitSelectDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private Button syncAllLinesForDoc;
	private ListView listView;
	private CursorAdapter cursorAdapter;
	private ProgressBar mDialogLoader;
	
	private String customerNo;
	
	public BusinessUnitSelectDialog() {
	}

	public static BusinessUnitSelectDialog newInstance(String customerNo) {
		BusinessUnitSelectDialog frag = new BusinessUnitSelectDialog();
        Bundle args = new Bundle();
        args.putString("customerNo", customerNo);
        frag.setArguments(args);
        return frag;
    }
	
	public interface BusinessUnitSelectDialogListener {
		void onBusinessUnitSelected(int unit_id, String address, String unit_no, String unit_name, String city, String post_code, String phone_no, String contact);
	}

	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
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
			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.setTitle(getResources().getString(R.string.dialog_title_error_in_sync));
			alertDialog.setMessage(syncResult.getResult());
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
				}
			});
			alertDialog.show();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.dialog_fragment_customer_business_unit_selector, container);
		getDialog().setTitle(getString(R.string.izaberiPoslovnuJedinicu));
		
		customerNo = getArguments().getString("customerNo", null);
		if (customerNo == null) {
			this.dismiss();
		}

		listView = (ListView) view.findViewById(R.id.customer_address_dialog_list);
		cursorAdapter = new BusinessUnitAdapter(getActivity());
		listView.setAdapter(cursorAdapter);

		mDialogLoader = (ProgressBar) view.findViewById(R.id.customer_address_dialog_load_progress);

		syncAllLinesForDoc = (Button) view.findViewById(R.id.dialog_customer_address_sync_button);
		syncAllLinesForDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent syncAddressIntent = new Intent(getActivity(), NavisionSyncService.class);
				CustomerBusinessUnitsSyncObject businessUnitsSyncObject = new CustomerBusinessUnitsSyncObject("", customerNo, "", DateUtils.getWsDummyDate());
				syncAddressIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, businessUnitsSyncObject);
				getActivity().startService(syncAddressIntent);
				
				mDialogLoader.setVisibility(View.VISIBLE);
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter getAddressSyncObject = new IntentFilter(CustomerBusinessUnitsSyncObject.BROADCAST_SYNC_ACTION);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, getAddressSyncObject);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}
	
	private class BusinessUnitAdapter extends CursorAdapter {
		
		public BusinessUnitAdapter(Context context) {
            super(context, null, false);
        }
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View parent, Context context, Cursor cursor) {
			
			TextView textView1 = (TextView) parent.findViewById(android.R.id.text1);
			textView1.setSingleLine();
        	TextView textView2 = (TextView) parent.findViewById(android.R.id.text2);
        	
        	final int unit_id = cursor.getInt(cursor.getColumnIndexOrThrow(CustomerBusinessUnits._ID));
        	final String address = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.ADDRESS));
			final String unit_no = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.UNIT_NO));
			final String unit_name = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.UNIT_NAME));
			final String city = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.CITY));
			final String post_code = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.POST_CODE));
			final String phone_no = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.PHONE_NO));
			final String contact = cursor.getString(cursor.getColumnIndexOrThrow(CustomerBusinessUnits.CONTACT));
        	
			textView1.setText(String.format("%s. %s", unit_no, unit_name));
			textView2.setText(String.format("%s, %s %s", address, post_code, city));
			
			parent.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						BusinessUnitSelectDialogListener listener = (BusinessUnitSelectDialogListener) getActivity().getSupportFragmentManager().findFragmentByTag("NEW_SALE_ORDER");
						listener.onBusinessUnitSelected(unit_id, address, unit_no, unit_name, city, post_code, phone_no, contact);
					} catch (Exception e) {
						BusinessUnitSelectDialogListener listener = (BusinessUnitSelectDialogListener) getActivity();
						listener.onBusinessUnitSelected(unit_id, address, unit_no, unit_name, city, post_code, phone_no, contact);
					}
					dismiss();
				}
			});
		}
		
	}

	private interface CustomerBusinessUnitsQuery {

		String[] PROJECTION = { 
				BaseColumns._ID,
				CustomerBusinessUnits.UNIT_NO,
				CustomerBusinessUnits.UNIT_NAME,
				CustomerBusinessUnits.ADDRESS,
				CustomerBusinessUnits.CITY,
				CustomerBusinessUnits.POST_CODE,
				CustomerBusinessUnits.CONTACT,
				CustomerBusinessUnits.PHONE_NO };

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), CustomerBusinessUnits.CONTENT_URI,
				CustomerBusinessUnitsQuery.PROJECTION, CustomerBusinessUnits.CUSTOMER_NO + "=?", new String[] { customerNo },
				MobileStoreContract.CustomerBusinessUnits.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null) {
			cursorAdapter.swapCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);
	}

}
