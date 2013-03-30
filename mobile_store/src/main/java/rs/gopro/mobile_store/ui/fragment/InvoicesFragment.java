package rs.gopro.mobile_store.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.ui.dialog.InvoicesPreviewDialog;
import rs.gopro.mobile_store.ui.widget.SimpleSelectionedListAdapter;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SalesDocumentsSyncObject;
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

public class InvoicesFragment extends ListFragment implements
		LoaderCallbacks<Cursor>, TextWatcher, OnItemSelectedListener {

	private SimpleSelectionedListAdapter adapter;
	private InvoicesListAdapter invoicesAdapter;
	
	private EditText customerNo;
	private Spinner openStatus;
	private Spinner documentType;
	
	private Button syncAllDocButton;
	private Button syncOpenForCustomerButton;
	
	private int isOpen = -1;
	private int document_type = -1;
	private String customer_no = "noCustomer";
	
	protected String salesPersonNo;
	protected String salesPersonId;
	
	private ProgressDialog invoicesLoadProgressDialog;
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			invoicesLoadProgressDialog.dismiss();
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (SalesDocumentsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
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
		invoicesAdapter = new InvoicesListAdapter(getActivity());
		adapter = new SimpleSelectionedListAdapter(getActivity(),
				R.layout.list_item_report_document_header, invoicesAdapter);
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
			customerNo = (EditText) getActivity().findViewById(R.id.customer_filter_value);
			documentType = (Spinner) getActivity().findViewById(R.id.invoice_document_type_spinner);
			openStatus = (Spinner) getActivity().findViewById(R.id.invoice_document_open_status_spinner);
			
			syncAllDocButton = (Button) getActivity().findViewById(R.id.invoice_sync_per_days_button);
			syncOpenForCustomerButton = (Button) getActivity().findViewById(R.id.invoice_sync_open_docs_per_customer_button);
			
			syncAllDocButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					Date today = new Date();
					SalesDocumentsSyncObject syncObject = new SalesDocumentsSyncObject("", Integer.valueOf(-1), "", "", rs.gopro.mobile_store.util.DateUtils.getPreviousDateIgnoringWeekend(today), rs.gopro.mobile_store.util.DateUtils.getTodayDateIgnoringWeekend(today), rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), salesPersonNo,Integer.valueOf(-1));
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
					getActivity().startService(intent);
					invoicesLoadProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_invoices_load), getActivity().getResources().getString(R.string.dialog_body_invoices_load), true, true);
				}
			});
			syncOpenForCustomerButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), NavisionSyncService.class);
					SalesDocumentsSyncObject syncObject = new SalesDocumentsSyncObject("", Integer.valueOf(-1), "", customerNo.getText().toString(), rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), salesPersonNo,Integer.valueOf(1));
					intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
					getActivity().startService(intent);
					invoicesLoadProgressDialog = ProgressDialog.show(getActivity(), getActivity().getResources().getString(R.string.dialog_title_invoices_load), getActivity().getResources().getString(R.string.dialog_body_invoices_load), true, true);
				}
			});
			ArrayAdapter<CharSequence> docTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.invoice_type_array, android.R.layout.simple_spinner_item);
			docTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			ArrayAdapter<CharSequence> openStatusAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.invoice_open_status_array, android.R.layout.simple_spinner_item);
			openStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			customerNo.addTextChangedListener(this);
			documentType.setOnItemSelectedListener(this);
			openStatus.setOnItemSelectedListener(this);
			
			openStatus.setAdapter(openStatusAdapter);
			documentType.setAdapter(docTypeAdapter);
		}
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.getContentResolver().registerContentObserver(
				MobileStoreContract.Invoices.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
        if (customer_no.equals("noCustomer") && isOpen == -1 && document_type == -1) {
        	baseUri = MobileStoreContract.Invoices.CONTENT_URI;
        } else {
        	baseUri = MobileStoreContract.Invoices.buildCustomSearchUri(Uri.encode(customer_no), String.valueOf(isOpen), String.valueOf(document_type));
        }
		
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				baseUri,
				InvoicesQuery.PROJECTION, null, null,
				MobileStoreContract.Invoices.DEFAULT_SORT);
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
		long postingDate;
		while (!cursor.isAfterLast()) {
			postingDate = UIUtils.getDateTime(
					cursor.getString(InvoicesQuery.POSTING_DATE)).getTime();
			if (!UIUtils.isSameDay(previouspostingDate, postingDate)) {
				String title = DateUtils.formatDateTime(getActivity(),
						postingDate, DateUtils.FORMAT_ABBREV_MONTH
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_SHOW_WEEKDAY);
				sections.add(new SimpleSelectionedListAdapter.Section(cursor
						.getPosition(), title));

			}
			previouspostingDate = postingDate;
			cursor.moveToNext();
		}
		invoicesAdapter.changeCursor(cursor);
		SimpleSelectionedListAdapter.Section[] dummy = new SimpleSelectionedListAdapter.Section[sections
				.size()];
		adapter.setSections(sections.toArray(dummy));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final Cursor cursor = (Cursor) invoicesAdapter.getItem(position);
		final String itemId = String.valueOf(cursor.getString(1));
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();
		alertDialog.setTitle("Faktura br:" + itemId);
		alertDialog.setMessage("Stavke fakture trenutno nisu dostupne.");
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
		IntentFilter salesDocumentsSyncObject = new IntentFilter(SalesDocumentsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, salesDocumentsSyncObject);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
	}
	
	public class InvoicesListAdapter extends CursorAdapter {
		/** Flags used with {@link DateUtils#formatDateRange}. */
//		private static final int TIME_FLAGS = DateUtils.FORMAT_SHOW_TIME
//				| DateUtils.FORMAT_SHOW_WEEKDAY
//				| DateUtils.FORMAT_ABBREV_WEEKDAY;

		public InvoicesListAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_report_document_block, parent, false);
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final Integer invoicesId = cursor.getInt(InvoicesQuery._ID);
			final String invoicesNo = cursor.getString(InvoicesQuery.NO);
			final String customerNo = cursor.getString(InvoicesQuery.CUSTOMER_NO);
			final String customerName = cursor.getString(InvoicesQuery.CUSTOMER_NAME);
			final Double totalAmount = cursor
					.getDouble(InvoicesQuery.ORIGINAL_AMOUNT);
			final Double remainingAmount = cursor
					.getDouble(InvoicesQuery.REMAINING_AMOUNT);
			final int statusOpen = cursor.getInt(InvoicesQuery.OPEN);
			
			final int doc_type = cursor.getInt(InvoicesQuery.DOCUMENT_TYPE);
			
			final String dueDate = cursor.getString(InvoicesQuery.DUE_DATE);
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

			String[] doc_types = getResources().getStringArray(R.array.invoice_type_array);
			
			primaryTouchTargetView.setOnLongClickListener(null);
			UIUtils.setActivatedCompat(primaryTouchTargetView, false);

			View.OnClickListener allSessionsListener = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					final Uri invoiceLineUri = MobileStoreContract.InvoiceLine
							.buildInvoiceLinesUri(invoicesId.toString());
					showDialog(invoiceLineUri, invoicesNo);
				}
			};

			titleView.setText(customerNo + " - " + customerName + "   " + getString(R.string.invoice_document_no) + " " + invoicesNo);
			titleView.setTextColor(res.getColorStateList(R.color.body_text_1_positive));
			
			statusView.setText(statusOpen != 0 ? getString(R.string.invoice_open) : "");
			if (statusOpen != 0) {
				statusView.setBackgroundResource(R.drawable.border_bad);
			} else {
				statusView.setBackgroundResource(0);
			}
			
			subtitleView.setText(getString(R.string.invoice_document_type) + " " + doc_types[doc_type]
					+ " - " + getString(R.string.invoice_due_date) + " " + rs.gopro.mobile_store.util.DateUtils.toUIfromDbDate(dueDate));
			subtitleView.setTextColor(res.getColorStateList(R.color.body_text_2));
			subtitleView2.setText(getString(R.string.invoice_amount_total)
							+ " " + UIUtils.formatDoubleForUI(totalAmount) + "  " + getString(R.string.invoice_amount_remaining)
							+ " " + UIUtils.formatDoubleForUI(remainingAmount));
			subtitleView2.setTextColor(res.getColorStateList(R.color.body_text_2));
			primaryTouchTargetView.setOnClickListener(allSessionsListener);
			primaryTouchTargetView.setEnabled(true);
		}

	}

	private void showDialog(Uri invoiceLineUri, String invoicesNo) {
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		InvoicesPreviewDialog fragment = new InvoicesPreviewDialog();
		Intent tempIntent = new Intent(Intent.ACTION_VIEW,
				invoiceLineUri);
		tempIntent.putExtra(InvoicesPreviewDialog.EXTRA_INVOICE_NO, invoicesNo);
		fragment.setArguments(BaseActivity
				.intentToFragmentArguments(tempIntent));
		fragment.show(ft, "invoice_dialog");
	}

	private interface InvoicesQuery {

		String[] PROJECTION = { Tables.INVOICES + "." + BaseColumns._ID,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.INVOICE_NO,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.CUSTOMER_ID,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.POSTING_DATE,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.SALES_PERSON_ID,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.DUE_DATE,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.ORIGINAL_AMOUNT,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.REMAINING_AMOUNT,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.DOCUMENT_TYPE,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.OPEN,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.PRICES_INCLUDE_VAT,

				Tables.INVOICES + "." + MobileStoreContract.Invoices.CREATED_DATE,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.CREATED_BY,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.UPDATED_DATE,
				Tables.INVOICES + "." + MobileStoreContract.Invoices.UPDATED_BY,
				Tables.CUSTOMERS + "." + MobileStoreContract.Customers.CUSTOMER_NO,
				Tables.CUSTOMERS + "." + MobileStoreContract.Customers.NAME
		};

		int _ID = 0;
		int NO = 1;
//		int CUSTOMER_ID = 2;
		int POSTING_DATE = 3;
//		int SALES_PERSON_ID = 4;
		int DUE_DATE = 5;
		int ORIGINAL_AMOUNT = 6;
		int REMAINING_AMOUNT = 7;
		int DOCUMENT_TYPE = 8;
		int OPEN = 9;
//		int PRICES_INCLUDE_VAT = 10;
//		int CREATED_DATE = 11;
//		int CREATED_BY = 12;
//		int UPDATED_DATE = 13;
//		int UPDATED_BY = 14;
		int CUSTOMER_NO = 15;
		int CUSTOMER_NAME = 16;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		int document_type_new = documentType.getSelectedItemPosition();
		int document_open_status_new = openStatus.getSelectedItemPosition();
		
		document_type = document_type_new == 0 ? -1 : document_type_new;
		isOpen = document_open_status_new == 0 ? -1 : document_open_status_new - 1; // it is boolean 0 and 1 because of that is -1
		
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
        String customer_no_new = !TextUtils.isEmpty(customerNo.getText().toString()) ? customerNo.getText().toString() : "noCustomer";
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
}