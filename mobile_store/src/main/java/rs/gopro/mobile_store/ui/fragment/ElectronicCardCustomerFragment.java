package rs.gopro.mobile_store.ui.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ElectronicCardCustomerSyncObject;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ElectronicCardCustomerFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	
	public static final String EXTRA_MASTER_URI = "rs.gopro.mobile_store.extra.MASTER_URI";

	private CursorAdapter cursorAdapter;
	private Uri eccUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
		eccUri = intent.getData();
		if (eccUri == null) {
			return;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
//		int[] to = new int[] { android.R.id.empty, R.id.el_card_item_no, R.id.el_card_jan_qty, R.id.el_card_feb_qty, R.id.el_card_mart_qty, R.id.el_card_apr_qty, R.id.el_card_may_qty, R.id.el_card_june_qty, R.id.el_card_july_qty,
//				R.id.el_card_aug_qty, R.id.el_card_sep_qty, R.id.el_card_oct_qty, R.id.el_card_nov_qty, R.id.el_card_dec_qty, R.id.el_card_total_sale_curr_qty, R.id.el_card_total_sale_prior_qty, R.id.el_card_turnover_curr_qty,
//				R.id.el_card_turnover_prior_qty, R.id.el_card_sales_line_counts_curr_qty, R.id.el_card_sales_line_counts_prior_qty, R.id.el_card_sales_line_last_line_discount_value };
//		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.list_item_el_card_customer, null, ElectronicCardCustomerQuery.PROJECTION, to, 0);

		cursorAdapter = new EkkCursorAdapter(getActivity(), null, false);
		
		View headerView = getActivity().getLayoutInflater().inflate(R.layout.list_header_el_card_customer, null, false);
		
		List<Date> months = DateUtils.getMonthsBackwards();
		Iterator<Date> monthsBackwards = months.iterator();
		Calendar monthIteration = Calendar.getInstance();
		
		String[] monthsTitles = getResources().getStringArray(R.array.ekk_months);
		
		TextView january = (TextView) headerView.findViewById(R.id.el_card_jan_qty);
		monthIteration.setTime(monthsBackwards.next());
		january.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView february_qty = (TextView) headerView.findViewById(R.id.el_card_feb_qty);
		monthIteration.setTime(monthsBackwards.next());
		february_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView march_qty = (TextView) headerView.findViewById(R.id.el_card_mart_qty);
		monthIteration.setTime(monthsBackwards.next());
		march_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView april_qty = (TextView) headerView.findViewById(R.id.el_card_apr_qty);
		monthIteration.setTime(monthsBackwards.next());
		april_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView may_qty = (TextView) headerView.findViewById(R.id.el_card_may_qty);
		monthIteration.setTime(monthsBackwards.next());
		may_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView june_qty = (TextView) headerView.findViewById(R.id.el_card_june_qty);
		monthIteration.setTime(monthsBackwards.next());
		june_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView july_qty = (TextView) headerView.findViewById(R.id.el_card_july_qty);
		monthIteration.setTime(monthsBackwards.next());
		july_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView august_qty = (TextView) headerView.findViewById(R.id.el_card_aug_qty);
		monthIteration.setTime(monthsBackwards.next());
		august_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView september_qty = (TextView) headerView.findViewById(R.id.el_card_sep_qty);
		monthIteration.setTime(monthsBackwards.next());
		september_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView october_qty = (TextView) headerView.findViewById(R.id.el_card_oct_qty);
		monthIteration.setTime(monthsBackwards.next());
		october_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView november_qty = (TextView) headerView.findViewById(R.id.el_card_nov_qty);
		monthIteration.setTime(monthsBackwards.next());
		november_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		TextView december_qty = (TextView) headerView.findViewById(R.id.el_card_dec_qty);
		monthIteration.setTime(monthsBackwards.next());
		december_qty.setText(monthsTitles[monthIteration.get(Calendar.MONTH)]);
		
		getListView().addHeaderView(headerView);
		getListView().setDivider(null);
		view.setBackgroundColor(Color.WHITE);
		setListAdapter(cursorAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		doSync();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String customerId = ElectronicCardCustomer.getECCId(eccUri);
		return new CursorLoader(getActivity(), ElectronicCardCustomer.CONTENT_URI, ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=?", new String[] { customerId }, ElectronicCardCustomer.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if(data.moveToFirst()){
			cursorAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		cursorAdapter.swapCursor(null);

	}

	private void doSync() {
		String customerId = ElectronicCardCustomer.getECCId(eccUri);
		Cursor cursor = getActivity().getContentResolver().query(Customers.buildCustomersUri(customerId), new String[]{Customers.CUSTOMER_NO}, Customers._ID + "=?" ,new String[]{customerId}, null);
		String customerNO = null;
		if(cursor.moveToFirst()){
			customerNO = cursor.getString(0);
		}
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ElectronicCardCustomerSyncObject electronicCardCustomerSyncObject = new ElectronicCardCustomerSyncObject("", customerNO, "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, electronicCardCustomerSyncObject);
		getActivity().startService(intent);

	}

	private class EkkCursorAdapter extends CursorAdapter {


		public EkkCursorAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		public EkkCursorAdapter(Context context, Cursor c, boolean b) {
			super(context, c, b);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			List<Date> months = DateUtils.getMonthsBackwards();
			Iterator<Date> monthsBackwards = months.iterator();
			Calendar monthIteration = Calendar.getInstance();
			
			TextView january = (TextView) view.findViewById(R.id.el_card_jan_qty);
			monthIteration.setTime(monthsBackwards.next());
			january.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView february_qty = (TextView) view.findViewById(R.id.el_card_feb_qty);
			monthIteration.setTime(monthsBackwards.next());
			february_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView march_qty = (TextView) view.findViewById(R.id.el_card_mart_qty);
			monthIteration.setTime(monthsBackwards.next());
			march_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView april_qty = (TextView) view.findViewById(R.id.el_card_apr_qty);
			monthIteration.setTime(monthsBackwards.next());
			april_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView may_qty = (TextView) view.findViewById(R.id.el_card_may_qty);
			monthIteration.setTime(monthsBackwards.next());
			may_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView june_qty = (TextView) view.findViewById(R.id.el_card_june_qty);
			monthIteration.setTime(monthsBackwards.next());
			june_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView july_qty = (TextView) view.findViewById(R.id.el_card_july_qty);
			monthIteration.setTime(monthsBackwards.next());
			july_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView august_qty = (TextView) view.findViewById(R.id.el_card_aug_qty);
			monthIteration.setTime(monthsBackwards.next());
			august_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView september_qty = (TextView) view.findViewById(R.id.el_card_sep_qty);
			monthIteration.setTime(monthsBackwards.next());
			september_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView october_qty = (TextView) view.findViewById(R.id.el_card_oct_qty);
			monthIteration.setTime(monthsBackwards.next());
			october_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView november_qty = (TextView) view.findViewById(R.id.el_card_nov_qty);
			monthIteration.setTime(monthsBackwards.next());
			november_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView december_qty = (TextView) view.findViewById(R.id.el_card_dec_qty);
			monthIteration.setTime(monthsBackwards.next());
			december_qty.setText(UIUtils.formatDoubleForUI(cursor.getDouble(monthIteration.get(Calendar.MONTH))));
			
			TextView total_sale_qty_current_year = (TextView) view.findViewById(R.id.el_card_total_sale_curr_qty);
			total_sale_qty_current_year.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.TOTAL_SALE_QTY_CURRENT_YEAR)));
			TextView total_sale_qty_prior_year = (TextView) view.findViewById(R.id.el_card_total_sale_prior_qty);
			total_sale_qty_prior_year.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.TOTAL_SALE_QTY_PRIOR_YEAR)));
			
			TextView total_turnover_current_year = (TextView) view.findViewById(R.id.el_card_turnover_curr_qty);
			total_turnover_current_year.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.TOTAL_TURNOVER_CURRENT_YEAR)));
			TextView total_turnover_prior_year = (TextView) view.findViewById(R.id.el_card_turnover_prior_qty);
			total_turnover_prior_year.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.TOTAL_TURNOVER_PRIOR_YEAR)));
			
			TextView sales_line_counts_current_year = (TextView) view.findViewById(R.id.el_card_sales_line_counts_curr_qty);
			sales_line_counts_current_year.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.SALES_LINE_COUNTS_CURRENT_YEAR)));
			TextView sales_line_counts_prior_year = (TextView) view.findViewById(R.id.el_card_sales_line_counts_prior_qty);
			sales_line_counts_prior_year.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.SALES_LINE_COUNTS_PRIOR_YEAR)));
			
			TextView last_line_discount = (TextView) view.findViewById(R.id.el_card_sales_line_last_line_discount_value);
			last_line_discount.setText(UIUtils.formatDoubleForUI(cursor.getDouble(ElectronicCardCustomerQuery.LAST_LINE_DISCOUNT)));
			
			TextView item_no = (TextView) view.findViewById(R.id.el_card_item_no);
			item_no.setText(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_el_card_customer, parent, false);
		}
		
	}
	
	private interface ElectronicCardCustomerQuery {
		String[] PROJECTION = new String[] {
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.JANUARY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.FEBRUARY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.MARCH_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.APRIL_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.MAY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.JUNE_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.JULY_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.AUGUST_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SEPTEMBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.OCTOBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.NOVEMBER_QTY,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.DECEMBER_QTY,
				
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+BaseColumns._ID,
				Tables.CUSTOMERS+"."+ElectronicCardCustomer.CUSTOMER_NO,
				Tables.ITEMS+"."+ElectronicCardCustomer.ITEM_NO,
				
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_SALE_QTY_CURRENT_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_SALE_QTY_PRIOR_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_TURNOVER_CURRENT_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.TOTAL_TURNOVER_PRIOR_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SALES_LINE_COUNTS_CURRENT_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.SALES_LINE_COUNTS_PRIOR_YEAR,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.LAST_LINE_DISCOUNT
		};


//		int JANUARY_QTY = 0;
//		int FEBRUARY_QTY = 1;
//		int MARCH_QTY = 2;
//		int APRIL_QTY = 3;
//		int MAY_QTY = 4;
//		int JUNE_QTY = 5;
//		int JULY_QTY = 6;
//		int AUGUST_QTY = 7;
//		int SEPTEMBER_QTY = 8;
//		int OCTOBER_QTY = 9;
//		int NOVEMBER_QTY = 10;
//		int DECEMBER_QTY = 11;
		
//		int _ID = 12;
//		int CUSTOMER_ID = 13;
		int ITEM_NO = 14;
		
		int TOTAL_SALE_QTY_CURRENT_YEAR = 15;
		int TOTAL_SALE_QTY_PRIOR_YEAR = 16;
		int TOTAL_TURNOVER_CURRENT_YEAR = 17;
		int TOTAL_TURNOVER_PRIOR_YEAR = 18;
		int SALES_LINE_COUNTS_CURRENT_YEAR = 19;
		int SALES_LINE_COUNTS_PRIOR_YEAR = 20;
		int LAST_LINE_DISCOUNT = 21;
	}

}
