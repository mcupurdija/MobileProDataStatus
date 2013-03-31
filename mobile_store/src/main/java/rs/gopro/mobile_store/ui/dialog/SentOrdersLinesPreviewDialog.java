package rs.gopro.mobile_store.ui.dialog;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SentOrdersStatusLinesSyncObject;
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

public class SentOrdersLinesPreviewDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	public static final String EXTRA_SALE_ORDER_STATUS_NO= "rs.gopro.mobile_store.extra.SALE_ORDER_STATUS_NO";
	public static final String EXTRA_SALE_ORDER_STATUS_DOC_TYPE= "rs.gopro.mobile_store.extra.SALE_ORDER_STATUS_DOC_TYPE";
	
	private Uri mSentOrderStatusUri;
	private CursorAdapter mAdapter;
	private Button syncAllLinesForDoc;
	private String sentOrderNo;
	private String sentOrderDocType;
	private ProgressBar mDialogLoader;
//	private String salesPersonNo;
	
//	private ProgressDialog itemLoadProgressDialog;
	
	public SentOrdersLinesPreviewDialog() {
	}
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			//itemLoadProgressDialog.dismiss();
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
//		salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
		reloadFromArguments(getArguments());
	}
	
	public void reloadFromArguments(Bundle arguments) {
	// Load new arguments
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
		mSentOrderStatusUri = intent.getData();
		sentOrderNo = intent.getCharSequenceExtra(EXTRA_SALE_ORDER_STATUS_NO).toString();
		sentOrderDocType = intent.getCharSequenceExtra(EXTRA_SALE_ORDER_STATUS_DOC_TYPE).toString();
		if (mSentOrderStatusUri == null) {
			return;
		}
		mAdapter = new SaleOrdersSentLineAdaper(getActivity());
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_invoice, container);
		ListView listView=(ListView) view.findViewById(R.id.invoice_dialog_list);
//		ListView  listView2 = new ListView(getActivity());
		listView.setAdapter(mAdapter);
		
		mDialogLoader = (ProgressBar) view.findViewById(R.id.dialog_load_progress);
		
		syncAllLinesForDoc = (Button) view.findViewById(R.id.invoice_lines_sync_button);
		syncAllLinesForDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NavisionSyncService.class);
				SentOrdersStatusLinesSyncObject syncObject = new SentOrdersStatusLinesSyncObject("", Integer.valueOf(sentOrderDocType), sentOrderNo, "", "");
				intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
				getActivity().startService(intent);
				mDialogLoader.setVisibility(View.VISIBLE);
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
    	IntentFilter salesInvoiceLinesSyncObject = new IntentFilter(SentOrdersStatusLinesSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, salesInvoiceLinesSyncObject);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), mSentOrderStatusUri, SentOrdersStatusLineQuery.PROJECTION, null, null, MobileStoreContract.SentOrdersStatusLines.DEFAULT_SORT);
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {	
		mAdapter.changeCursor(null);
	}
	
	
	private class SaleOrdersSentLineAdaper extends CursorAdapter{
		
		public SaleOrdersSentLineAdaper(Context context) {
			super(context, null,false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
//			Integer  invoice_type =  cursor.getInt(SentOrdersStatusLineQuery.TYPE);
			Integer line_type = cursor.getInt(SentOrdersStatusLineQuery.LINE_TYPE);
			double quantity = cursor.getDouble(SentOrdersStatusLineQuery.QUANTITY);
			double price = cursor.getDouble(SentOrdersStatusLineQuery.UNIT_PRICE);
			double line_discount = cursor.getDouble(SentOrdersStatusLineQuery.LINE_DISCOUNT_AMOUNT);
			double line_discount_percent = cursor.getDouble(SentOrdersStatusLineQuery.LINE_DISCOUNT_PERCENT);
			double document_line_discount = cursor.getDouble(SentOrdersStatusLineQuery.INV_DISCOUNT_AMOUNT);
			String promised_date = "";
			if (!cursor.isNull(SentOrdersStatusLineQuery.PROMISED_DELIVERY_DATE)) {
				promised_date = cursor.getString(SentOrdersStatusLineQuery.PROMISED_DELIVERY_DATE);
			}
			
			int promised_date_confirmed = cursor.getInt(SentOrdersStatusLineQuery.CONFIRMED_PROMISED_DELIVERY_DATE);
			
			double line_amount = quantity*price - line_discount - document_line_discount;
			
			TextView title1 = (TextView) view.findViewById(R.id.invoice_line_title1);
			TextView title2 = (TextView) view.findViewById(R.id.invoice_line_title2);
			TextView subtitle1 = (TextView)view.findViewById(R.id.invoice_line_subtitle);
			TextView subtitle2 = (TextView)view.findViewById(R.id.invoice_line_subtitle2);
			TextView subtitle3 = (TextView)view.findViewById(R.id.invoice_line_subtitle3);
			
			String [] invoiceLineType = getResources().getStringArray(R.array.invoice_line_type_array);
			
			title1.setText(cursor.getString(SentOrdersStatusLineQuery.DESCRIPTION));
			title2.setText(invoiceLineType[line_type]);
			String 	quantityString = getString(R.string.invoice_line_quantity)+": "+ UIUtils.formatDoubleForUI(quantity);
			subtitle1.setText(quantityString + " Cena: "+ UIUtils.formatDoubleForUI(price) + " Popust: "+ UIUtils.formatDoubleForUI(line_discount_percent) + "%");
//			String unitPriceString = getString(R.string.invoice_line_unit_price) + ": " + cursor.getString(SentOrdersStatusLineQuery.UNIT_PRICE);
			subtitle2.setText("Iznos: "+UIUtils.formatDoubleForUI(line_amount));
//			String lineDiscountString = getString(R.string.invoice_line_discount_amount) + ": " + cursor.getString(InvoiceLineQuery.LINE_DISCOUNT_AMOUNT);
//			
			subtitle3.setText("Obećani datum isporuke: " + (promised_date == "" ? "-":DateUtils.formatDbDateForPresentation(promised_date)) + " Potvrdjen: " + (promised_date_confirmed == 0 ? "Ne":"Da"));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			SentOrdersLinesPreviewDialog.this.getDialog().setTitle(getString(R.string.invoice_line_dialog_title));
			return	getActivity().getLayoutInflater().inflate(R.layout.list_item_invoice_line, parent, false);
		}
		
			
		
	}
	
	private interface SentOrdersStatusLineQuery {

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.SentOrdersStatusLines.DOCUMENT_TYPE,
				MobileStoreContract.SentOrdersStatusLines.QUANTITY,
				MobileStoreContract.SentOrdersStatusLines.UNIT_PRICE,
				MobileStoreContract.SentOrdersStatusLines.LINE_DISCOUNT_AMOUNT,
				MobileStoreContract.SentOrdersStatusLines.DESCRIPTION,
				MobileStoreContract.SentOrdersStatusLines.LINE_DISCOUNT_PERCENT,
				MobileStoreContract.SentOrdersStatusLines.PROMISED_DELIVERY_DATE,
				MobileStoreContract.SentOrdersStatusLines.CONFIRMED_PROMISED_DELIVERY_DATE,
				MobileStoreContract.SentOrdersStatusLines.INV_DISCOUNT_AMOUNT,
				MobileStoreContract.SentOrdersStatusLines.LINE_TYPE};

//		int _ID = 0;
//		int TYPE = 1;
		int QUANTITY = 2;
		int UNIT_PRICE = 3;
		int LINE_DISCOUNT_AMOUNT = 4;
		int DESCRIPTION = 5;
		int LINE_DISCOUNT_PERCENT = 6;
		int PROMISED_DELIVERY_DATE = 7;
		int CONFIRMED_PROMISED_DELIVERY_DATE = 8;
		int INV_DISCOUNT_AMOUNT = 9;
		int LINE_TYPE = 10;
	}

}
