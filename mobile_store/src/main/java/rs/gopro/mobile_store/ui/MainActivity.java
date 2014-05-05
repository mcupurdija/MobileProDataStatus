package rs.gopro.mobile_store.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.adapter.ActionsAdapter;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
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
import rs.gopro.mobile_store.ui.dialog.ServiceOrderDialog;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
import rs.gopro.mobile_store.ws.model.ServiceOrderSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
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

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, MainContextualActionBarCallback, ServiceOrderDialog.ServiceOrderDialogListener {
	private static String TAG = "MainActivity";
	public static final String CURRENT_POSITION_KEY = "current_position";
	
	private ActionsAdapter actionsAdapter;
	private Integer currentItemPosition = Integer.valueOf(1);
	private CustomLinearLayout currentCustomLinearLayout;
	private Map<String, CustomLinearLayout> savedLayoutInstances = new HashMap<String, CustomLinearLayout>();
	
	private ProgressDialog serviceLoaderProgressDialog;
	
	/**
	 * Create menu as list view on left side of screen. Create content space
	 * right of menu.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.getDefaultSharedPreferences(this).edit().remove("ws_entry_point").remove("ws_schema").remove("ws_navisition_codeunit").remove("ws_server_address").remove("company_address").commit();
		PreferenceManager.setDefaultValues(this, R.xml.ws_settings, true);
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
		if (actionsAdapter.getItemTitle(position).equals("Kupci")) {
			Cursor syncLogCustomers = getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.CREATED_DATE }, SyncLogs.SYNC_OBJECT_ID+"=? and " + SyncLogs.SYNC_OBJECT_STATUS + "=?", new String[] { CustomerSyncObject.TAG, SyncStatus.SUCCESS.toString() }, SyncLogs._ID+" desc limit 1");
			if (syncLogCustomers.moveToFirst()) {
				String createdDateFromDb = syncLogCustomers.getString(0);
				contentTitle.setText("Datum poslednje sinhronizacije kupaca: " + DateUtils.formatDbDateForPresentation(createdDateFromDb));
				
				Date createdDate = DateUtils.getLocalDbDate(createdDateFromDb);
				long days = DateUtils.getDateDiff(createdDate, new Date(), TimeUnit.DAYS);
				if (days >= 1) {
					contentTitle.setTextColor(Color.RED);
				} else {
					contentTitle.setTextColor(Color.BLACK);
				}
			} else {
				contentTitle.setText("Kupci");
				contentTitle.setTextColor(Color.BLACK);
			}
			syncLogCustomers.close();
		} else if (actionsAdapter.getItemTitle(position).equals("Artikli")) {
			Cursor syncLogItemsAction = getContentResolver().query(SyncLogs.CONTENT_URI, new String[] { SyncLogs.CREATED_DATE }, SyncLogs.SYNC_OBJECT_ID+" like ? and " + SyncLogs.SYNC_OBJECT_STATUS + "=?", new String[] { "Items%", SyncStatus.SUCCESS.toString() }, SyncLogs._ID+" desc limit 1");
			if (syncLogItemsAction.moveToFirst()) {
				String createdDateFromDb = syncLogItemsAction.getString(0);
				contentTitle.setText("Datum poslednje sinhronizacije artikala: " + DateUtils.formatDbDateForPresentation(createdDateFromDb));
				
				Date createdDate = DateUtils.getLocalDbDate(createdDateFromDb);
				long days = DateUtils.getDateDiff(createdDate, new Date(), TimeUnit.DAYS);
				if (days >= 1) {
					contentTitle.setTextColor(Color.RED);
				} else {
					contentTitle.setTextColor(Color.BLACK);
				}
			} else {
				contentTitle.setText("Artikli");
				contentTitle.setTextColor(Color.BLACK);
			}
			syncLogItemsAction.close();
		} else {
			contentTitle.setText(actionsAdapter.getItemTitle(position));
			contentTitle.setTextColor(Color.BLACK);
		}

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
		} else if (ServiceOrderDialog.SERVICE_ORDER_URI.equals(uri)) {
			if (salesPersonId == null) {
				LogUtils.LOGE(TAG, "Big problem! Should be value here!");
				salesPersonId = "1";
			}
			ServiceOrderDialog serviceOrderDialog = new ServiceOrderDialog();
			Bundle sendInitValues = new Bundle();
			sendInitValues.putInt("DIALOG_ID", 0);
			sendInitValues.putString("DIALOG_TITLE", "Kreiraj servisni nalog");
			sendInitValues.putInt("SALES_PERSON_ID", Integer.valueOf(salesPersonId).intValue());
			serviceOrderDialog.setArguments(sendInitValues);
			
			serviceOrderDialog.show(getSupportFragmentManager(), "SERVICE_ORDER_DIALOG");
		}
		
		if (view != null) {
			linearLayout.removeAllViews();
			setContentTitle(position);
			this.currentItemPosition = position;
			actionsAdapter.markSecletedItem(view);
			currentCustomLinearLayout = view;
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
				menu.getItem(1).setVisible(true);menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (PlanOfVisitsLayout.PLAN_OF_VISITS_URI.equals(uri)) {
				menu.getItem(0).setVisible(true);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (CustomersLayout.CUSTOMERS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(true);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (ItemsLayout.ITEMS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (CustomerLedgerEntriesLayout.CUSTOMER_LEDGER_ENTRIES_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (SentOrdersStatusLayout.SENT_ORDERS_STATUS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (ContactsLayout.CONTACTS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(true);
			} else if (SentOrdersLayout.SENT_ORDERS_URI.equals(uri)) {
				menu.getItem(2).setVisible(true);menu.getItem(0).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
			} else if (RecordVisitsLayout.RECORD_VISITS_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(true);menu.getItem(5).setVisible(false);
			 }else if (PlanAndTurnoverLayout.PLAN_AND_TURNOVER_URI.equals(uri)) {
				menu.getItem(0).setVisible(false);menu.getItem(2).setVisible(false);menu.getItem(1).setVisible(false);menu.getItem(3).setVisible(false);menu.getItem(4).setVisible(false);menu.getItem(5).setVisible(false);
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
			/*final Uri recordVisitsUri = MobileStoreContract.Visits.CONTENT_URI;
			final Intent recordIntent = new Intent(RecordVisitsMultipaneActivity.RECORD_VISITS_INTENT, recordVisitsUri);
			startActivity(recordIntent);*/
			startActivity(new Intent(getApplicationContext(), NovaRealizacijaActivity.class));
			return true;
		case R.id.main_options_visits_details:
			/*final Uri visitsUri = MobileStoreContract.Visits.CONTENT_URI;
			final Intent intent = new Intent(Intent.ACTION_VIEW, visitsUri);
			startActivity(intent);*/
			startActivity(new Intent(getApplicationContext(), NoviPlanActivity.class));
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
		case R.id.main_shortcuts_sync_all:
			startActivity(new Intent(this, SinhonizacijaActivity.class));
			return true;
		case R.id.main_options_add_contact:
			Intent addContactIntent = new Intent(this, AddContactActivity.class);
			startActivity(addContactIntent);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter navSyncFilter = new IntentFilter(ServiceOrderSyncObject.BROADCAST_SYNC_ACTION);
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
			if (serviceLoaderProgressDialog != null)
				serviceLoaderProgressDialog.dismiss();
			SyncResult syncResult = intent
					.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
			// itemLoadProgressDialog.dismiss();
		}
	};
	
	protected void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ServiceOrderSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				ServiceOrderSyncObject serviceOrderSyncObject = (ServiceOrderSyncObject) syncResult.getComplexResult();
				DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_sync_info), "Broj servisnog naloga: "+serviceOrderSyncObject.getpServiceHeaderNoa46());
			}
		} else {
			DialogUtil.showInfoDialog(MainActivity.this,getResources().getString(R.string.dialog_title_error_in_sync),syncResult.getResult());
		}
	}

	@Override
	public void onFinishServiceOrderDialog(int service_order_id) {
		serviceLoaderProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.dialog_title_service_order_load), getResources().getString(R.string.dialog_body_service_order_load), true, true);
		ServiceOrderSyncObject serviceOrderSyncObject = new ServiceOrderSyncObject(service_order_id);
    	Intent intent = new Intent(this, NavisionSyncService.class);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, serviceOrderSyncObject);
		startService(intent);
		
	}

}
