package rs.gopro.mobile_store.ui;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.adapter.ActionsAdapter;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.customlayout.ContactsLayout;
import rs.gopro.mobile_store.ui.customlayout.CustomLinearLayout;
import rs.gopro.mobile_store.ui.customlayout.CustomerLedgerEntriesLayout;
import rs.gopro.mobile_store.ui.customlayout.CustomersLayout;
import rs.gopro.mobile_store.ui.customlayout.ItemsLayout;
import rs.gopro.mobile_store.ui.customlayout.PlanAndTurnoverLayout;
import rs.gopro.mobile_store.ui.customlayout.PlanOfVisitsLayout;
import rs.gopro.mobile_store.ui.customlayout.RecordVisitsLayout;
import rs.gopro.mobile_store.ui.customlayout.SaleOrdersLayout;
import rs.gopro.mobile_store.ui.customlayout.SentOrdersLayout;
import rs.gopro.mobile_store.ui.customlayout.SentOrdersStatusLayout;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Holder for main menu and first nested level (content)
 * 
 * @author aleksandar
 * 
 * new comment vlada
 * 
 */

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, MainContextualActionBarCallback {
	private static String TAG = "MainActivity";
	public static final String CURRENT_POSITION_KEY = "current_position";
	
	private ActionsAdapter actionsAdapter;
	private Integer currentItemPosition = Integer.valueOf(1);
	private CustomLinearLayout currentCustomLinearLayout;
	private Map<String, CustomLinearLayout> savedLayoutInstances = new HashMap<String, CustomLinearLayout>();

	/**
	 * Create menu as list view on left side of screen. Create content space
	 * right of menu.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.ws_settings,false);
		boolean isUserLogged = SharedPreferencesUtil.isUserLoggedIn(getApplicationContext());
		if (!isUserLogged) {
			Intent loginScreen = new Intent(this, LoginActivity.class);
			startActivity(loginScreen);
		}
		
		if (savedInstanceState != null) {
			currentItemPosition = savedInstanceState.getInt(CURRENT_POSITION_KEY);
		}
	
		setContentView(R.layout.activity_main_screen);
		final ListView viewActionsList = (ListView) findViewById(R.id.main_menu_list);
		actionsAdapter = new ActionsAdapter(this);
		viewActionsList.setAdapter(actionsAdapter);
		viewActionsList.setOnItemClickListener(this);
		setContentTitle(currentItemPosition);
		updateContent(currentItemPosition);
		LogUtils.LOGI(TAG, "Main activity created!");
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long flags) {
		setContentTitle(position);
		this.currentItemPosition = position;
		actionsAdapter.markSecletedItem(view);
		updateContent(position);
		invalidateOptionsMenu();
		LogUtils.LOGI(TAG, "onItemClick");
	}

	/**
	 * Get title from arrays and setup in text field
	 * 
	 * @param position
	 */
	private void setContentTitle(int position) {
		TextView contentTitle = (TextView) findViewById(R.id.content_title);
		contentTitle.setText(actionsAdapter.getItemTitle(position));

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			outState.putInt(CURRENT_POSITION_KEY, currentItemPosition);
		}
	}

	public Integer getCurrentItemPosition() {
		return currentItemPosition;
	}

	public void updateContent(int position) {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content);
		linearLayout.removeAllViews();
		Uri uri = actionsAdapter.getItem(position);
		CustomLinearLayout view = null;
		if (SaleOrdersLayout.SALE_ORDER_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(SaleOrdersLayout.SALE_ORDER_URI.toString())) {
				view = savedLayoutInstances.get(SaleOrdersLayout.SALE_ORDER_URI.toString());
			} else {
				view = new SaleOrdersLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(SaleOrdersLayout.SALE_ORDER_URI.toString(), view);
			}
		} else if (RecordVisitsLayout.RECORD_VISITS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(RecordVisitsLayout.RECORD_VISITS_URI.toString())) {
				view = savedLayoutInstances.get(RecordVisitsLayout.RECORD_VISITS_URI.toString());
			} else {
				view = new RecordVisitsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(RecordVisitsLayout.RECORD_VISITS_URI.toString(), view);
			}
		} else if (PlanOfVisitsLayout.PLAN_OF_VISITS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(PlanOfVisitsLayout.PLAN_OF_VISITS_URI.toString())) {
				view = savedLayoutInstances.get(PlanOfVisitsLayout.PLAN_OF_VISITS_URI.toString());
			} else {
				view = new PlanOfVisitsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(PlanOfVisitsLayout.PLAN_OF_VISITS_URI.toString(), view);
			}
		} else if (CustomersLayout.CUSTOMERS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(CustomersLayout.CUSTOMERS_URI.toString())) {
				view = savedLayoutInstances.get(CustomersLayout.CUSTOMERS_URI.toString());
			} else {
				view = new CustomersLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(CustomersLayout.CUSTOMERS_URI.toString(), view);
			}
		} else if (ItemsLayout.ITEMS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(ItemsLayout.ITEMS_URI.toString())) {
				view = savedLayoutInstances.get(ItemsLayout.ITEMS_URI.toString());
			} else {
				view = new ItemsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(ItemsLayout.ITEMS_URI.toString(), view);
			}
		} else if (CustomerLedgerEntriesLayout.CUSTOMER_LEDGER_ENTRIES_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(CustomerLedgerEntriesLayout.CUSTOMER_LEDGER_ENTRIES_URI.toString())) {
				view = savedLayoutInstances.get(CustomerLedgerEntriesLayout.CUSTOMER_LEDGER_ENTRIES_URI.toString());
			} else {
				view = new CustomerLedgerEntriesLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(CustomerLedgerEntriesLayout.CUSTOMER_LEDGER_ENTRIES_URI.toString(), view);
			}
		} else if (SentOrdersStatusLayout.SENT_ORDERS_STATUS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(SentOrdersStatusLayout.SENT_ORDERS_STATUS_URI.toString())) {
				view = savedLayoutInstances.get(SentOrdersStatusLayout.SENT_ORDERS_STATUS_URI.toString());
			} else {
				view = new SentOrdersStatusLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(SentOrdersStatusLayout.SENT_ORDERS_STATUS_URI.toString(), view);
			}
		} else if (ContactsLayout.CONTACTS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(ContactsLayout.CONTACTS_URI.toString())) {
				view = savedLayoutInstances.get(ContactsLayout.CONTACTS_URI.toString());
			} else {
				view = new ContactsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(ContactsLayout.CONTACTS_URI.toString(), view);
			}
		} else if (SentOrdersLayout.SENT_ORDERS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(SentOrdersLayout.SENT_ORDERS_URI.toString())) {
				view = savedLayoutInstances.get(SentOrdersLayout.SENT_ORDERS_URI.toString());
			} else {
				view = new SentOrdersLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(SentOrdersLayout.SENT_ORDERS_URI.toString(), view);
			}
		} else if (PlanAndTurnoverLayout.PLAN_AND_TURNOVER_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(PlanAndTurnoverLayout.PLAN_AND_TURNOVER_URI.toString())) {
				view = savedLayoutInstances.get(PlanAndTurnoverLayout.PLAN_AND_TURNOVER_URI.toString());
			} else {
				view = new PlanAndTurnoverLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(PlanAndTurnoverLayout.PLAN_AND_TURNOVER_URI.toString(), view);
			}
		}
		currentCustomLinearLayout = view;
		if (view != null) {
			linearLayout.addView(view);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar calendar = Calendar.getInstance();
		switch (id) {
		case ApplicationConstants.DATE_PICKER_DIALOG:
			DatePickerDialog datePicker = new DatePickerDialog(this, currentCustomLinearLayout, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return datePicker;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * Callback method for contextual action bar initialization
	 */
	@Override
	public void onLongClickItem(String identifier, String visitType) {
		ActionMode.Callback callback = currentCustomLinearLayout.getContextualActionBar(identifier, visitType);
		if (callback != null) {
			startActionMode(callback);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// this check because if user is not logged in, it will break
		if (actionsAdapter != null) {
			Uri uri = actionsAdapter.getItem(currentItemPosition);
			
			if (SaleOrdersLayout.SALE_ORDER_URI.equals(uri)) {
				menu.getItem(1).setVisible(true);menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (PlanOfVisitsLayout.PLAN_OF_VISITS_URI.equals(uri)) {
				menu.getItem(0).setVisible(true);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (CustomersLayout.CUSTOMERS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(true);menu.getItem(4).setVisible(false);
			} else if (ItemsLayout.ITEMS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (CustomerLedgerEntriesLayout.CUSTOMER_LEDGER_ENTRIES_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (SentOrdersStatusLayout.SENT_ORDERS_STATUS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (ContactsLayout.CONTACTS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (SentOrdersLayout.SENT_ORDERS_URI.equals(uri)) {
				menu.getItem(2).setVisible(true);menu.getItem(0).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			} else if (RecordVisitsLayout.RECORD_VISITS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(true);
			 }else if (PlanAndTurnoverLayout.PLAN_AND_TURNOVER_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.synchronize_main_action:
//			// doSynchronization();
//			currentCustomLinearLayout.doSynchronization();
//			return true;
		case R.id.main_options_record_visits_details:
			final Uri recordVisitsUri = MobileStoreContract.Visits.CONTENT_URI;
			final Intent recordIntent = new Intent(RecordVisitsMultipaneActivity.RECORD_VISITS_INTENT, recordVisitsUri);
			startActivity(recordIntent);
			return true;
		case R.id.main_options_visits_details:
			final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
			final Intent intent = new Intent(Intent.ACTION_VIEW, visitsUri);
			startActivity(intent);
			return true;
		case R.id.main_options_sale_order_details:
			final Uri saleOrdersUri = MobileStoreContract.SaleOrders.CONTENT_URI;
            final Intent saleOrderIntent = new Intent(Intent.ACTION_VIEW, saleOrdersUri);
            startActivity(saleOrderIntent);
			return true;
		case R.id.main_options_sale_order_sent_details:
			final Uri sentOrdersUri = MobileStoreContract.SaleOrders.CONTENT_URI;
            final Intent sentOrderIntent = new Intent(Intent.ACTION_VIEW, sentOrdersUri);
            startActivity(sentOrderIntent);
			return true;
		case R.id.main_options_customers_details:
			final Uri customersuri = MobileStoreContract.Customers.CONTENT_URI;
            final Intent customersIntent = new Intent(Intent.ACTION_VIEW, customersuri);
            startActivity(customersIntent);
			return true;
		case R.id.main_shortcuts_new_sale_order:
			Intent newSaleOrderIntent = new Intent(Intent.ACTION_INSERT,
					MobileStoreContract.SaleOrders.CONTENT_URI);
//			startActivityForResult(newSaleOrderIntent, CALL_INSERT);
			startActivity(newSaleOrderIntent);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter navSyncFilter = new IntentFilter(ItemsSyncObject.BROADCAST_SYNC_ACTION);
		// registering broadcast receiver to listen BROADCAST_SYNC_ACTION
		// broadcast
		LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, navSyncFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}

	private BroadcastReceiver onNotice = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult.getStatus(), syncResult.getResult());
		}
	};

//	private void doSynchronization() {
//		Intent intent = new Intent(this, NavisionSyncService.class);
//		ItemsSyncObject itemsSyncObject = new ItemsSyncObject(null, null, Integer.valueOf(1), null, DateUtils.getWsDummyDate());
//		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
//		this.startService(intent);
//
//	}

	public void onSOAPResult(SyncStatus syncStatus, String result) {
		System.out.println("Status: " + syncStatus);
		return;
	}

}
