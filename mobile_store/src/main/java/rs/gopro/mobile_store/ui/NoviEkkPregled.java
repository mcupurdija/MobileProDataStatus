package rs.gopro.mobile_store.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsColumns;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ElectronicCardCustomerSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import rs.gopro.mobile_store.ws.model.domain.ElectronicCardCustomerDomain;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NoviEkkPregled extends Activity {
	
	private String customerId, customerNo;
	private String last_ecc_sync_date;
	
	private static int sirinaAC, visinaAB, sirinaB, sirinaBopis, minusMargina;
	
	DisplayMetrics metrics;

	protected BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ElectronicCardCustomerSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				
				ContentValues cv = new ContentValues();
				cv.put(Customers.LAST_ECC_SYNC_DATE, DateUtils.toDbDate(new Date()));
				getContentResolver().update(Customers.buildCustomersUri(customerId), cv, null, null);
				
				new GenerateLayout().execute();
				//contentView = new DoubleScrollingLayout(this, null);
				//setContentView(contentView);
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		switch (metrics.densityDpi) {
			case 160:
				sirinaAC = 140;
				visinaAB = 80;
				sirinaB = 100;
				sirinaBopis = 400;
				minusMargina = -10;
				break;
			case 213:
				sirinaAC = 180;
				visinaAB = 100;
				sirinaB = 120;
				sirinaBopis = 500;
				minusMargina = -12;
				break;
			default:
				sirinaAC = 280;
				visinaAB = 160;
				sirinaB = 200;
				sirinaBopis = 800;
				minusMargina = -19;
				break;
		}
		
		customerId = getIntent().getStringExtra("customerId");
		Cursor cursor = getContentResolver().query(Customers.buildCustomersUri(customerId), new String[]{ Customers.CUSTOMER_NO }, Customers._ID + "=?" ,new String[]{ customerId }, null);
		if(cursor.moveToFirst()){
			customerNo = cursor.getString(0);
		}
		new GenerateLayout().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE, "Preuzmi sa servera za prethodni mesec");
		item.setIcon(R.drawable.ic_action_download);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0 :
			setContentView(R.layout.loading);
			doSync();
			return true;
        case android.R.id.home : // home back, not do it on main
            finish();
            return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}

	@Override
	protected void onResume() {
		super.onResume();
    	IntentFilter eccSyncObject = new IntentFilter(ElectronicCardCustomerSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, eccSyncObject);
	}
	
	private void doSync() {
		Intent intent = new Intent(getApplicationContext(), NavisionSyncService.class);
		ElectronicCardCustomerSyncObject electronicCardCustomerSyncObject = new ElectronicCardCustomerSyncObject("", customerNo, "", "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, electronicCardCustomerSyncObject);
		startService(intent);
	}
	
	private Date firstDayOfCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	private Date sixthDayOfCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH) + 5);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	private boolean NeedSync() {
		
		Cursor cursor = getContentResolver().query(Customers.buildCustomersUri(customerId), new String[]{ Customers.LAST_ECC_SYNC_DATE }, Customers._ID + "=?" ,new String[]{ customerId }, null);
		if(cursor.moveToFirst()){
			last_ecc_sync_date = cursor.getString(0);
			
			try {
				Date lastSyncDate = DateUtils.getLocalDbDate(last_ecc_sync_date);
				Date currentDate = new Date();
				
				System.out.println("DATUM POSLEDNJEG AZURIRANJA: " + lastSyncDate.toString());
				System.out.println("TRENUTNI DATUM: " + currentDate.toString());
				System.out.println("PRVI DAN U MESECU: " + firstDayOfCurrentMonth().toString());
				System.out.println("SESTI DAN U MESECU: " + sixthDayOfCurrentMonth().toString());
				
				if (lastSyncDate.before(firstDayOfCurrentMonth()) || 
						(firstDayOfCurrentMonth().before(lastSyncDate) && 
								lastSyncDate.before(sixthDayOfCurrentMonth()) && 
									currentDate.after(sixthDayOfCurrentMonth()))) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	
	private class GenerateLayout extends AsyncTask<Void, Void, Void> {

		View contentView;
		 
		@Override
		protected Void doInBackground(Void... params) {
			contentView = new DoubleScrollingLayout(NoviEkkPregled.this, null);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setContentView(contentView);
			
			if (NeedSync()) {
				DialogUtil.showInfoDialog(NoviEkkPregled.this, "Obaveštenje", "Ukoliko niste u ovom mesecu preuzeli podatke sa servera potrebno je da ih preuzmete.");
			}
		}
	     
	}
	
	private class DoubleScrollingLayout extends RelativeLayout {

		String[] headers = new String[21];
		int headerCellsWidth[] = new int[headers.length];
		
		TableLayout tableA;
		TableLayout tableB;
		TableLayout tableC;
		TableLayout tableD;

		HorizontalScrollView horizontalScrollViewB;
		HorizontalScrollView horizontalScrollViewD;

		ScrollView scrollViewC;
		ScrollView scrollViewD;

		Context context;
		Cursor cursor;
		
		ArrayList<ElectronicCardCustomerDomain> ekkObjects = new ArrayList<ElectronicCardCustomerDomain>();
		
		public DoubleScrollingLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
			this.context = context;
			
			List<Date> months = DateUtils.getMonthsBackwards();
			Iterator<Date> monthsBackwards = months.iterator();
			Calendar monthIteration = Calendar.getInstance();

			String[] monthsTitles = getResources().getStringArray(R.array.ekk_months);
			
			headers[0] = getString(R.string.el_card_item_no_label);
			for (int i = 1; i < monthsTitles.length + 1; i++) {
				monthIteration.setTime(monthsBackwards.next());
				headers[i] = monthsTitles[monthIteration.get(Calendar.MONTH)];
			}
			headers[13] = getString(R.string.el_card_total_sale_curr_qty_label);
			headers[14] = getString(R.string.el_card_total_sale_prior_qty_label);
			headers[15] = getString(R.string.el_card_turnover_curr_qty_label);
			headers[16] = getString(R.string.el_card_turnover_prior_qty_label);
			headers[17] = getString(R.string.el_card_sales_line_counts_curr_qty_label);
			headers[18] = getString(R.string.el_card_sales_line_counts_prior_qty_label);
			headers[19] = getString(R.string.el_card_sales_line_last_line_discount_label);
			headers[20] = getString(R.string.item_desc_label);
			
			cursor = context.getContentResolver().query(ElectronicCardCustomer.CONTENT_URI, ElectronicCardCustomerQuery.PROJECTION, ElectronicCardCustomer.CUSTOMER_ID + "=?", new String[] { customerId }, ElectronicCardCustomer.DEFAULT_SORT);
			
			if (cursor.moveToFirst()) {
				for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					
					monthsBackwards = months.iterator();
					
					ElectronicCardCustomerDomain eccd = new ElectronicCardCustomerDomain();
					eccd.setItem_no(cursor.getString(ElectronicCardCustomerQuery.ITEM_NO));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setJanuary_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setFebruary_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setMarch_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setApril_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setMay_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setJune_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setJuly_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setAugust_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setSeptember_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setOctober_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setNovember_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					monthIteration.setTime(monthsBackwards.next());
					eccd.setDecember_qty(cursor.getString(monthIteration.get(Calendar.MONTH)));
					eccd.setTotal_sale_qty_current_year(cursor.getString(ElectronicCardCustomerQuery.TOTAL_SALE_QTY_CURRENT_YEAR));
					eccd.setTotal_sale_qty_prior_year(cursor.getString(ElectronicCardCustomerQuery.TOTAL_SALE_QTY_PRIOR_YEAR));
					eccd.setTotal_turnover_current_year(cursor.getString(ElectronicCardCustomerQuery.TOTAL_TURNOVER_CURRENT_YEAR));
					eccd.setTotal_turnover_prior_year(cursor.getString(ElectronicCardCustomerQuery.TOTAL_TURNOVER_PRIOR_YEAR));
					eccd.setSales_line_counts_current_year(cursor.getString(ElectronicCardCustomerQuery.SALES_LINE_COUNTS_CURRENT_YEAR));
					eccd.setSales_line_counts_prior_year(cursor.getString(ElectronicCardCustomerQuery.SALES_LINE_COUNTS_PRIOR_YEAR));
					eccd.setLast_line_discount(cursor.getString(ElectronicCardCustomerQuery.LAST_LINE_DISCOUNT));
					eccd.setColor(cursor.getString(ElectronicCardCustomerQuery.COLOR));
					eccd.setItem_description(cursor.getString(ElectronicCardCustomerQuery.DESCRIPTION));
					ekkObjects.add(eccd);
				}
				
				// initialize the main components (TableLayouts, HorizontalScrollView,
				// ScrollView)
				this.initComponents();
				this.setComponentsId();
				this.setScrollViewAndHorizontalScrollViewTag();

				// no need to assemble component A, since it is just a table
				this.horizontalScrollViewB.addView(this.tableB);

				this.scrollViewC.addView(this.tableC);

				this.scrollViewD.addView(this.horizontalScrollViewD);
				this.horizontalScrollViewD.addView(this.tableD);

				// add the components to be part of the main layout
				this.addComponentToMainLayout();
				this.setBackgroundColor(Color.WHITE);

				// add some table rows
				this.addTableRowToTableA();
				this.addTableRowToTableB();
				this.resizeHeaderHeight();
				this.getTableRowHeaderCellWidth();
				this.generateTableC_AndTable_D();
				this.resizeBodyTableRowHeight();
				
			} else {
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				TextView textView = new TextView(context);
				textView.setLayoutParams(params);
				textView.setTextSize(24);
				textView.setText("Nema podataka za odabranog kupca");
				this.addView(textView);
				
			}
			
		}

		// initalized components
		private void initComponents() {

			this.tableA = new TableLayout(this.context);
			this.tableB = new TableLayout(this.context);
			this.tableC = new TableLayout(this.context);
			this.tableD = new TableLayout(this.context);

			this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
			this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);

			this.scrollViewC = new MyScrollView(this.context);
			this.scrollViewD = new MyScrollView(this.context);

			this.tableA.setBackgroundColor(Color.BLACK);
			this.horizontalScrollViewB.setBackgroundColor(Color.BLACK);

		}

		// set essential component IDs
		private void setComponentsId() {
			this.tableA.setId(1);
			this.horizontalScrollViewB.setId(2);
			this.scrollViewC.setId(3);
			this.scrollViewC.setScrollbarFadingEnabled(false);
			this.scrollViewD.setId(4);
		}

		// set tags for some horizontal and vertical scroll view
		private void setScrollViewAndHorizontalScrollViewTag() {

			this.horizontalScrollViewB.setTag("horizontal scroll view b");
			this.horizontalScrollViewD.setTag("horizontal scroll view d");

			this.scrollViewC.setTag("scroll view c");
			this.scrollViewD.setTag("scroll view d");
		}

		// we add the components here in our TableMainLayout
		private void addComponentToMainLayout() {

			// RelativeLayout params were very useful here
			// the addRule method is the key to arrange the components properly
			RelativeLayout.LayoutParams componentB_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableA.getId());

			RelativeLayout.LayoutParams componentC_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			componentC_Params.addRule(RelativeLayout.BELOW, this.tableA.getId());

			RelativeLayout.LayoutParams componentD_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewC.getId());
			componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewB.getId());

			// 'this' is a relative layout,
			// we extend this table layout as relative layout as seen during the
			// creation of this class
			this.addView(this.tableA);
			this.addView(this.horizontalScrollViewB, componentB_Params);
			this.addView(this.scrollViewC, componentC_Params);
			this.addView(this.scrollViewD, componentD_Params);

		}

		private void addTableRowToTableA() {
			this.tableA.addView(this.componentATableRow());
		}

		private void addTableRowToTableB() {
			this.tableB.addView(this.componentBTableRow());
		}

		// generate table row of table A
		TableRow componentATableRow() {

			TableRow componentATableRow = new TableRow(this.context);
			TableRow.LayoutParams params = new TableRow.LayoutParams(sirinaAC, visinaAB);
			
			TextView textView = this.headerTextView(this.headers[0]);
			textView.setLayoutParams(params);
			textView.setTextSize(16);
			componentATableRow.addView(textView);

			return componentATableRow;
		}

		// generate table row of table B
		TableRow componentBTableRow() {

			TableRow componentBTableRow = new TableRow(this.context);
			int headerFieldCount = this.headers.length;

			TableRow.LayoutParams params = new TableRow.LayoutParams(sirinaB, visinaAB);
			params.setMargins(2, 0, 0, 0);
			
			TableRow.LayoutParams params2 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, visinaAB);
			params2.setMargins(2, minusMargina, 0, 0);
			
			TableRow.LayoutParams params3 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, visinaAB);
			params3.setMargins(2, 0, 0, 0);
			
			TableRow.LayoutParams params4 = new TableRow.LayoutParams(sirinaBopis, visinaAB);
			params4.setMargins(2, 0, 0, 0);
			
			for (int x = 0; x < 12; x++) {
				TextView textView = this.headerTextView(this.headers[x + 1]);
				textView.setLayoutParams(params);
				textView.setTextSize(16);
				componentBTableRow.addView(textView);
			}

			for (int x = 12; x < (headerFieldCount - 3); x++) {
				TextView textView2 = this.headerTextView(this.headers[x + 1]);
				textView2.setLayoutParams(params2);
				textView2.setTextSize(16);
				componentBTableRow.addView(textView2);
			}
			
			TextView textView = this.headerTextView(this.headers[headerFieldCount - 2]);
			textView.setLayoutParams(params3);
			textView.setTextSize(16);
			componentBTableRow.addView(textView);
			
			TextView textView2 = this.headerTextView(this.headers[headerFieldCount - 1]);
			textView2.setLayoutParams(params4);
			textView2.setTextSize(16);
			componentBTableRow.addView(textView2);

			return componentBTableRow;
		}

		// generate table row of table C and table D
		private void generateTableC_AndTable_D() {

			// just seeing some header cell width
			headerCellsWidth[0] = sirinaAC;
			headerCellsWidth[1] = sirinaB;
			headerCellsWidth[2] = sirinaB;
			headerCellsWidth[3] = sirinaB;
			headerCellsWidth[4] = sirinaB;
			headerCellsWidth[5] = sirinaB;
			headerCellsWidth[6] = sirinaB;
			headerCellsWidth[7] = sirinaB;
			headerCellsWidth[8] = sirinaB;
			headerCellsWidth[9] = sirinaB;
			headerCellsWidth[10] = sirinaB;
			headerCellsWidth[11] = sirinaB;
			headerCellsWidth[12] = sirinaB;
			headerCellsWidth[20] = sirinaBopis;

			for (int x = 0; x < this.headerCellsWidth.length; x++) {
				Log.v("TableMainLayout.java", this.headerCellsWidth[x] + "");
			}

			for (ElectronicCardCustomerDomain sampleObject : ekkObjects) {

				TableRow tableRowForTableC = this.tableRowForTableC(sampleObject);
				TableRow taleRowForTableD = this.taleRowForTableD(sampleObject);

				tableRowForTableC.setBackgroundColor(Color.BLACK);
				taleRowForTableD.setBackgroundColor(Color.BLACK);

				this.tableC.addView(tableRowForTableC);
				this.tableD.addView(taleRowForTableD);

			}
		}

		// a TableRow for table C
		TableRow tableRowForTableC(ElectronicCardCustomerDomain sampleObject) {

			TableRow.LayoutParams params = new TableRow.LayoutParams(this.headerCellsWidth[0], LayoutParams.MATCH_PARENT);
			params.setMargins(0, 2, 0, 0);

			TableRow tableRowForTableC = new TableRow(this.context);
			TextView textView = this.bodyTextView(sampleObject.item_no);
			
			try {
				switch (Integer.valueOf(sampleObject.color)) {
				case 0:
					textView.setBackgroundColor(Color.parseColor("#FFCCCB"));
					break;
				case 1:
					textView.setBackgroundColor(Color.parseColor("#FFA500"));
					break;
				case 2:
					textView.setBackgroundColor(Color.YELLOW);
					break;
				case 3:
					textView.setBackgroundColor(Color.parseColor("#7FE817"));
					break;
				case 4:
					textView.setBackgroundColor(Color.GRAY);
					break;
				case 5:
					textView.setBackgroundColor(Color.CYAN);
					break;
				default:
					textView.setBackgroundColor(Color.WHITE);
					break;
				}
			} catch (Exception e) {
				textView.setBackgroundColor(Color.parseColor("#FFCCCB"));
			}
			
			
			tableRowForTableC.addView(textView, params);

			return tableRowForTableC;
		}

		TableRow taleRowForTableD(ElectronicCardCustomerDomain sampleObject) {

			TableRow taleRowForTableD = new TableRow(this.context);

			int loopCount = ((TableRow) this.tableB.getChildAt(0)).getChildCount();
			String info[] = { sampleObject.january_qty, sampleObject.february_qty,
					sampleObject.march_qty, sampleObject.april_qty,
					sampleObject.may_qty, sampleObject.june_qty,
					sampleObject.july_qty, sampleObject.august_qty,
					sampleObject.september_qty, sampleObject.october_qty,
					sampleObject.november_qty, sampleObject.december_qty,
					sampleObject.total_sale_qty_current_year, sampleObject.total_sale_qty_prior_year,
					sampleObject.total_turnover_current_year, sampleObject.total_turnover_prior_year,
					sampleObject.sales_line_counts_current_year, sampleObject.sales_line_counts_prior_year,
					sampleObject.last_line_discount, sampleObject.item_description };

			for (int x = 0; x < loopCount; x++) {
				TableRow.LayoutParams params = new TableRow.LayoutParams(headerCellsWidth[x + 1], LayoutParams.MATCH_PARENT);
				params.setMargins(2, 2, 0, 0);

				TextView textViewB = this.bodyTextView(info[x]);
				
				try {
					switch (Integer.valueOf(sampleObject.color)) {
					case 0:
						textViewB.setBackgroundColor(Color.parseColor("#FFCCCB"));
						break;
					case 1:
						textViewB.setBackgroundColor(Color.parseColor("#FFA500"));
						break;
					case 2:
						textViewB.setBackgroundColor(Color.YELLOW);
						break;
					case 3:
						textViewB.setBackgroundColor(Color.parseColor("#7FE817"));
						break;
					case 4:
						textViewB.setBackgroundColor(Color.GRAY);
						break;
					case 5:
						textViewB.setBackgroundColor(Color.CYAN);
						break;
					default:
						textViewB.setBackgroundColor(Color.WHITE);
						break;
					}
				} catch (Exception e) {
					textViewB.setBackgroundColor(Color.parseColor("#FFCCCB"));
				}
				
				taleRowForTableD.addView(textViewB, params);
				
			}
			return taleRowForTableD;
		}

		// table cell standard TextView
		TextView bodyTextView(String label) {

			TextView bodyTextView = new TextView(this.context);
			//bodyTextView.setBackgroundColor(Color.WHITE);
			bodyTextView.setText(label);
			bodyTextView.setGravity(Gravity.CENTER);
			bodyTextView.setPadding(5, 5, 5, 5);

			return bodyTextView;
		}

		// header standard TextView
		TextView headerTextView(String label) {

			TextView headerTextView = new TextView(this.context);
			headerTextView.setBackgroundColor(Color.WHITE);
			headerTextView.setText(label);
			headerTextView.setGravity(Gravity.CENTER);
			headerTextView.setPadding(5, 5, 5, 5);

			return headerTextView;
		}

		// resizing TableRow height starts here
		void resizeHeaderHeight() {

			TableRow productNameHeaderTableRow = (TableRow) this.tableA
					.getChildAt(0);
			TableRow productInfoTableRow = (TableRow) this.tableB.getChildAt(0);

			int rowAHeight = this.viewHeight(productNameHeaderTableRow);
			int rowBHeight = this.viewHeight(productInfoTableRow);

			TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow
					: productInfoTableRow;
			int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

			this.matchLayoutHeight(tableRow, finalHeight);
		}

		void getTableRowHeaderCellWidth() {

			int tableAChildCount = ((TableRow) this.tableA.getChildAt(0))
					.getChildCount();
			int tableBChildCount = ((TableRow) this.tableB.getChildAt(0))
					.getChildCount();
			;

			for (int x = 0; x < (tableAChildCount + tableBChildCount); x++) {

				if (x == 0) {
					this.headerCellsWidth[x] = this
							.viewWidth(((TableRow) this.tableA.getChildAt(0))
									.getChildAt(x));
				} else {
					this.headerCellsWidth[x] = this
							.viewWidth(((TableRow) this.tableB.getChildAt(0))
									.getChildAt(x - 1));
				}

			}
		}

		// resize body table row height
		void resizeBodyTableRowHeight() {

			int tableC_ChildCount = this.tableC.getChildCount();

			for (int x = 0; x < tableC_ChildCount; x++) {

				TableRow productNameHeaderTableRow = (TableRow) this.tableC.getChildAt(x);
				TableRow productInfoTableRow = (TableRow) this.tableD.getChildAt(x);

				int rowAHeight = this.viewHeight(productNameHeaderTableRow);
				int rowBHeight = this.viewHeight(productInfoTableRow);

				TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
				int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

				this.matchLayoutHeight(tableRow, finalHeight);
			}

		}

		// match all height in a table row
		// to make a standard TableRow height
		private void matchLayoutHeight(TableRow tableRow, int height) {

			int tableRowChildCount = tableRow.getChildCount();

			// if a TableRow has only 1 child
			if (tableRow.getChildCount() == 1) {

				View view = tableRow.getChildAt(0);
				TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
				params.height = height - (params.bottomMargin + params.topMargin);

				return;
			}

			// if a TableRow has more than 1 child
			for (int x = 0; x < tableRowChildCount; x++) {

				View view = tableRow.getChildAt(x);

				TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();

				if (!isTheHeighestLayout(tableRow, x)) {
					params.height = height
							- (params.bottomMargin + params.topMargin);
					return;
				}
			}

		}

		// check if the view has the highest height in a TableRow
		private boolean isTheHeighestLayout(TableRow tableRow, int layoutPosition) {

			int tableRowChildCount = tableRow.getChildCount();
			int heighestViewPosition = -1;
			int viewHeight = 0;

			for (int x = 0; x < tableRowChildCount; x++) {
				View view = tableRow.getChildAt(x);
				int height = this.viewHeight(view);

				if (viewHeight < height) {
					heighestViewPosition = x;
					viewHeight = height;
				}
			}

			return heighestViewPosition == layoutPosition;
		}

		// read a view's height
		private int viewHeight(View view) {
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			return view.getMeasuredHeight();
		}

		// read a view's width
		private int viewWidth(View view) {
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			return view.getMeasuredWidth();
		}

		// horizontal scroll view custom class
		class MyHorizontalScrollView extends HorizontalScrollView {

			public MyHorizontalScrollView(Context context) {
				super(context);
			}

			@Override
			protected void onScrollChanged(int l, int t, int oldl, int oldt) {
				String tag = (String) this.getTag();

				if (tag.equalsIgnoreCase("horizontal scroll view b")) {
					horizontalScrollViewD.scrollTo(l, 0);
				} else {
					horizontalScrollViewB.scrollTo(l, 0);
				}
			}

		}

		// scroll view custom class
		class MyScrollView extends ScrollView {

			public MyScrollView(Context context) {
				super(context);
			}

			@Override
			protected void onScrollChanged(int l, int t, int oldl, int oldt) {

				String tag = (String) this.getTag();

				if (tag.equalsIgnoreCase("scroll view c")) {
					scrollViewD.scrollTo(0, t);
				} else {
					scrollViewC.scrollTo(0, t);
				}
			}
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
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.LAST_LINE_DISCOUNT,
				Tables.ELECTRONIC_CARD_CUSTOMER+"."+ElectronicCardCustomer.COLOR,
				Tables.ITEMS+"."+ItemsColumns.DESCRIPTION
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
		int COLOR = 22;
		int DESCRIPTION = 23;
	}
}
