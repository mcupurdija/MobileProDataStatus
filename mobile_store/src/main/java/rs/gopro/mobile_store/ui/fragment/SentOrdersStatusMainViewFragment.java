package rs.gopro.mobile_store.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.widget.SimpleSelectionedListAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SalesHeadersSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SentOrdersStatusMainViewFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {

	private static final String NO_CUSTOMER_FLAG = "noCustomer";
	
	private SimpleSelectionedListAdapter adapter;
	private SentOrdersStatusListAdapter sentOrdersStatusListAdapter;
	
	private EditText customerNoEditText;
	private Spinner shipmentStatusSpinner;
	
	private Button syncAllOrdersButton;
	
	private String customer_no = NO_CUSTOMER_FLAG;
	private int shipmentStatus = -1;
	
	protected String salesPersonNo;
	protected String salesPersonId;
	
	private ProgressDialog sentOrdersStatusProgressDialog;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			sentOrdersStatusProgressDialog.dismiss();
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}
			Loader<Cursor> loader = getLoaderManager().getLoader(0);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};
	
	
	public SentOrdersStatusMainViewFragment() {
	}
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SalesHeadersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				// TODO some nice info here
//				SalesDocumentsSyncObject syncObject = (SalesDocumentsSyncObject) syncResult.getComplexResult();
				
			}
		} else {
			AlertDialog alertDialog = new AlertDialog.Builder(
					getActivity()).create();

		    // Setting Dialog Title
		    alertDialog.setTitle(getResources().getString(R.string.dialog_title_error_in_sync));
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
		sentOrdersStatusListAdapter = new SentOrdersStatusListAdapter(getActivity());
		adapter = new SimpleSelectionedListAdapter(getActivity(),
				R.layout.list_item_report_document_header, sentOrdersStatusListAdapter);
		setListAdapter(adapter);
		
		salesPersonId = SharedPreferencesUtil.getSalePersonId(getActivity());
        salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_list_with_empty_container, container, false);
		inflater.inflate(R.layout.content_empty_waiting_sync,
				(ViewGroup) rootView.findViewById(android.R.id.empty), true);
		rootView.setBackgroundColor(Color.WHITE);
		ListView listView = (ListView) rootView.findViewById(android.R.id.list);
		listView.setItemsCanFocus(true);
		listView.setCacheColorHint(Color.WHITE);
		listView.setSelector(android.R.color.transparent);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			customerNoEditText = (EditText) getActivity().findViewById(R.id.customer_filter_value);
			shipmentStatusSpinner = (Spinner) getActivity().findViewById(R.id.sent_orders_status_shipment_status_spinner);
		
			syncAllOrdersButton = (Button) getActivity().findViewById(R.id.sent_orders_status_sync_open_headers_button);
		
			syncAllOrdersButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					SalesHeadersSyncObject syncObject = new SalesHeadersSyncObject("", Integer.valueOf(-1), "", "", rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), salesPersonNo, Integer.valueOf(-1));
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
					getActivity().startService(intent);
					sentOrdersStatusProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_sent_orders_status_load), getActivity().getResources().getString(R.string.dialog_body_sent_orders_status_load), true);
				}
			});

			ArrayAdapter<CharSequence> shipmentStatusAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_status_for_shipment_array, android.R.layout.simple_spinner_item);
			shipmentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			customerNoEditText.addTextChangedListener(this);
			shipmentStatusSpinner.setOnItemSelectedListener(this);
	
			shipmentStatusSpinner.setAdapter(shipmentStatusAdapter);
		}
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getContentResolver().registerContentObserver(
				MobileStoreContract.SentOrdersStatus.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (customer_no.equals(NO_CUSTOMER_FLAG) && shipmentStatus == -1) {
        	baseUri = MobileStoreContract.SentOrdersStatus.CONTENT_URI;
        } else {
        	baseUri = MobileStoreContract.SentOrdersStatus.buildCustomSearchUri(Uri.encode(customer_no), String.valueOf(shipmentStatus));
        }
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				baseUri,
				SentOrdersStatusQuery.PROJECTION, null, null,
				MobileStoreContract.SentOrdersStatus.DEFAULT_SORT);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		List<SimpleSelectionedListAdapter.Section> sections = new ArrayList<SimpleSelectionedListAdapter.Section>();
		cursor.moveToFirst();
		long previouspostingDate = -1;
		long postingDate = -1;
		while (!cursor.isAfterLast()) {
			String orderDate = cursor.getString(SentOrdersStatusQuery.ORDER_DATE);
			if (orderDate != null) {
				postingDate = UIUtils.getDateTime(
						cursor.getString(SentOrdersStatusQuery.ORDER_DATE)).getTime();
				if (!UIUtils.isSameDay(previouspostingDate, postingDate)) {
					String title = DateUtils.formatDateTime(getActivity(),
							postingDate, DateUtils.FORMAT_ABBREV_MONTH
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_SHOW_WEEKDAY);
					sections.add(new SimpleSelectionedListAdapter.Section(cursor
							.getPosition(), title));
	
				}
			} else {
				sections.add(new SimpleSelectionedListAdapter.Section(cursor
						.getPosition(), "-"));
			}
			previouspostingDate = postingDate;
			cursor.moveToNext();
		}
		sentOrdersStatusListAdapter.changeCursor(cursor);
		SimpleSelectionedListAdapter.Section[] dummy = new SimpleSelectionedListAdapter.Section[sections
				.size()];
		adapter.setSections(sections.toArray(dummy));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final Cursor cursor = (Cursor) sentOrdersStatusListAdapter.getItem(position);
		final String itemId = String.valueOf(cursor.getString(1));
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();
		alertDialog.setTitle("Dokument br:" + itemId);
		alertDialog.setMessage("Stavke dokumenta trenutno nisu dostupne.");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// Set the Icon for the Dialog
		// alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();	
		IntentFilter salesHeadersSyncObject = new IntentFilter(SalesHeadersSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, salesHeadersSyncObject);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		shipmentStatus = shipmentStatusSpinner.getSelectedItemPosition();		
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		// Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String customer_no_new = !TextUtils.isEmpty(customerNoEditText.getText().toString()) ? customerNoEditText.getText().toString() : "noCustomer";
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (customer_no == null && customer_no_new == null) {
        	customer_no = "noCustomer";
            return;
        }
        if (customer_no != null && customer_no.equals(customer_no_new)) {
            return;
        }
        customer_no = customer_no_new;
        getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public class SentOrdersStatusListAdapter extends CursorAdapter {

		public SentOrdersStatusListAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_report_document_block, parent, false);
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final Integer sentOrdersId = cursor.getInt(SentOrdersStatusQuery._ID);
			final String docNo = cursor.getString(SentOrdersStatusQuery.SENT_ORDER_NO);
			final String customerNo = cursor.getString(SentOrdersStatusQuery.CUSTOMER_NO);
			final String customerName = cursor.getString(SentOrdersStatusQuery.CUSTOMER_NAME);
			
			String[] shipment_statuses = getResources().getStringArray(R.array.order_status_for_shipment_array);
			String[] fin_control_statuses = getResources().getStringArray(R.array.financial_control_status_array);
			String[] order_condition_statuses = getResources().getStringArray(R.array.order_condition_status_array);
			String[] order_value_statuses = getResources().getStringArray(R.array.order_value_status_array);
			
			final int shipment_status = cursor.getInt(SentOrdersStatusQuery.ORDER_STATUS_FOR_SHIPMENT);
			final int fin_control_status = cursor.getInt(SentOrdersStatusQuery.FIN_CONTROL_STATUS);
			final int order_condition_status = cursor.getInt(SentOrdersStatusQuery.ORDER_CONDITION_STATUS);
			final int order_value_status = cursor.getInt(SentOrdersStatusQuery.ORDER_VALUE_STATUS);
			
			final int doc_type = cursor.getInt(SentOrdersStatusQuery.DOCUMENT_TYPE);
			
			final String order_date = cursor.getString(SentOrdersStatusQuery.ORDER_DATE);
//			final long dateOfCreation = UIUtils.getDateTime(
//					cursor.getString(InvoicesQuery.CREATED_DATE)).getTime();
//			final long postingDate = UIUtils.getDateTime(
//					cursor.getString(InvoicesQuery.POSTING_DATE)).getTime();
			final TextView titleView = (TextView) view
					.findViewById(R.id.block_title);
			final TextView statusView = (TextView) view
					.findViewById(R.id.block_status);
			final TextView subtitleView = (TextView) view
					.findViewById(R.id.block_subtitle1);
			final TextView subtitleView2 = (TextView) view
					.findViewById(R.id.block_subtitle2);
			final View primaryTouchTargetView = view
					.findViewById(R.id.list_item_middle_container);

			final Resources res = getResources();

			String[] doc_types = getResources().getStringArray(R.array.sent_orders_status_doc_type_array);

			primaryTouchTargetView.setOnLongClickListener(null);
			UIUtils.setActivatedCompat(primaryTouchTargetView, false);

			View.OnClickListener allSessionsListener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final Uri invoiceLineUri = MobileStoreContract.InvoiceLine
							.buildInvoiceLinesUri(sentOrdersId.toString());
					showDialog(invoiceLineUri);
				}
			};

			titleView.setText(customerNo + " - " + customerName + "   " + getString(R.string.invoice_document_no) + " " + docNo);
			titleView.setTextColor(res.getColorStateList(R.color.body_text_1));
			
			statusView.setText(shipment_statuses[shipment_status]);
			statusView.setBackgroundResource(R.drawable.border_normal);
			
			subtitleView.setText(getString(R.string.invoice_document_type) + " " + doc_types[doc_type]
					+ " - " + getString(R.string.sent_orders_status_order_date) + " " + rs.gopro.mobile_store.util.DateUtils.toUIfromDbDate(order_date));
			subtitleView.setTextColor(res.getColorStateList(R.color.body_text_2));
			
			subtitleView2.setText("Statusi:"
							+ " " + fin_control_statuses[fin_control_status] + ";" + order_condition_statuses[order_condition_status]
							+ ";" + order_value_statuses[order_value_status]);
			subtitleView2.setTextColor(res.getColorStateList(R.color.body_text_2));
			
			primaryTouchTargetView.setOnClickListener(allSessionsListener);
			primaryTouchTargetView.setEnabled(true);
		}

	}
	
	private interface SentOrdersStatusQuery {

		String[] PROJECTION = { Tables.SENT_ORDERS_STATUS + "." + BaseColumns._ID,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.DOCUMENT_TYPE,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.CUSTOMER_ID,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.SENT_ORDER_NO,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.ORDER_CONDITION_STATUS,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.ORDER_STATUS_FOR_SHIPMENT,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.ORDER_VALUE_STATUS,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.FIN_CONTROL_STATUS,
				Tables.SENT_ORDERS_STATUS + "." + MobileStoreContract.SentOrdersStatus.ORDER_DATE,

				Tables.CUSTOMERS + "." + MobileStoreContract.Customers.CUSTOMER_NO,
				Tables.CUSTOMERS + "." + MobileStoreContract.Customers.NAME
		};

		int _ID = 0;
		int DOCUMENT_TYPE = 1;
//		int CUSTOMER_ID = 2;
		int SENT_ORDER_NO = 3;
		int ORDER_CONDITION_STATUS = 4;
		int ORDER_STATUS_FOR_SHIPMENT = 5;
		int ORDER_VALUE_STATUS = 6;
		int FIN_CONTROL_STATUS = 7;
		int ORDER_DATE = 8;

		int CUSTOMER_NO = 9;
		int CUSTOMER_NAME = 10;
	}
	
	private void showDialog(Uri invoiceLineUri) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		InvoicesPreviewDialog fragment = new InvoicesPreviewDialog();
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(new Intent(Intent.ACTION_VIEW,
						invoiceLineUri)));
		fragment.show(ft, "invoice_dialog");
	}
}
