package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class InvoicesPreviewDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private Uri mInvoiceLineUri;
	private CursorAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reloadFromArguments(getArguments());
	}
	
	public void reloadFromArguments(Bundle arguments) {
	// Load new arguments
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
		mInvoiceLineUri = intent.getData();
		if (mInvoiceLineUri == null) {
			return;
		}
		mAdapter = new InvoiceLineAdaper(getActivity());
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_invoice, container);
		ListView listView=(ListView) view.findViewById(R.id.invoice_dialog_list);
		ListView  listView2 = new ListView(getActivity());
		listView.setAdapter(mAdapter);
		
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
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), mInvoiceLineUri, InvoiceLineQuery.PROJECTION, null, null, MobileStoreContract.InvoiceLine.DEFAULT_SORT);
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
		
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class InvoiceLineAdaper extends CursorAdapter{
		
		public InvoiceLineAdaper(Context context) {
			super(context, null,false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Integer  integer =  cursor.getInt(InvoiceLineQuery.TYPE);
			String [] invoiceLineType = getResources().getStringArray(R.array.invoice_line_type_array);
			TextView title = (TextView) view.findViewById(R.id.invoice_line_title);
			title.setText(invoiceLineType[integer]);
			TextView  quantity = (TextView)view.findViewById(R.id.invoice_line_subtitle);
			String 	quantityString = getString(R.string.invoice_line_quantity)+": "+cursor.getString(InvoiceLineQuery.QUANTITY);
			quantity.setText(quantityString);
			String unitPriceString = getString(R.string.invoice_line_unit_price) + ": " + cursor.getString(InvoiceLineQuery.UNIT_PRICE);
			TextView  unitPrice = (TextView)view.findViewById(R.id.invoice_line_subtitle2);
			unitPrice.setText(unitPriceString);
			String lineDiscountString = getString(R.string.invoice_line_discount_amount) + ": " + cursor.getString(InvoiceLineQuery.LINE_DISCOUNT_AMOUNT);
			TextView lineDiscount = (TextView)view.findViewById(R.id.invoice_line_subtitle3);
			lineDiscount.setText(lineDiscountString);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			InvoicesPreviewDialog.this.getDialog().setTitle(getString(R.string.invoice_line_dialog_title));
			return	getActivity().getLayoutInflater().inflate(R.layout.list_item_invoice_line, parent, false);
		}
		
			
		
	}
	
	private interface InvoiceLineQuery {
		
		String[] PROJECTION = {
				BaseColumns._ID, 
				MobileStoreContract.InvoiceLine.TYPE,
				MobileStoreContract.InvoiceLine.QUANTITY,
				MobileStoreContract.InvoiceLine.UNIT_PRICE,
				MobileStoreContract.InvoiceLine.LINE_DISCOUNT_AMOUNT
				};
		
		int _ID = 0;
		int TYPE = 1;
		int QUANTITY = 2;
		int UNIT_PRICE= 3;
		int LINE_DISCOUNT_AMOUNT= 4;
		}

}
