package rs.gopro.mobile_store.ui;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.adapter.ActionsAdapter;
import rs.gopro.mobile_store.ui.customlayout.ContactsLayout;
import rs.gopro.mobile_store.ui.customlayout.CustomLinearLayout;
import rs.gopro.mobile_store.ui.customlayout.CustomersLayout;
import rs.gopro.mobile_store.ui.customlayout.InvoicesLayout;
import rs.gopro.mobile_store.ui.customlayout.ItemsLayout;
import rs.gopro.mobile_store.ui.customlayout.PlanOfVisitsLayout;
import rs.gopro.mobile_store.ui.customlayout.ReportLayout;
import rs.gopro.mobile_store.ui.customlayout.SaleOrdersLayout;
import rs.gopro.mobile_store.ui.widget.MainContextualActionBarCallback;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
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
 */

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, MainContextualActionBarCallback {
	private static String TAG = "MainActivity";
	
	ActionsAdapter actionsAdapter;
	private Integer currentItemPosition = Integer.valueOf(1);
	public static final String CURRENT_POSITION_KEY = "current_position";
	private ResultReceiver mReceiver;
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
		LogUtils.LOGI(TAG, "onCreate");
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long flags) {
		setContentTitle(position);
		this.currentItemPosition = position;
		actionsAdapter.markSecletedItem(view);
		updateContent(position);
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
				view =  new SaleOrdersLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(SaleOrdersLayout.SALE_ORDER_URI.toString(), view);
			}
		} else if (PlanOfVisitsLayout.PLAN_OF_VISITS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(PlanOfVisitsLayout.PLAN_OF_VISITS_URI.toString())) {
				view = savedLayoutInstances.get(PlanOfVisitsLayout.PLAN_OF_VISITS_URI.toString());
			} else {
				view = new PlanOfVisitsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(PlanOfVisitsLayout.PLAN_OF_VISITS_URI.toString(), view);
			}
		}else if(CustomersLayout.CUSTOMERS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(CustomersLayout.CUSTOMERS_URI.toString())) {
				view = savedLayoutInstances.get(CustomersLayout.CUSTOMERS_URI.toString());
			} else {
				view = new CustomersLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(CustomersLayout.CUSTOMERS_URI.toString(), view);
			}
		}else if(ItemsLayout.ITEMS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(ItemsLayout.ITEMS_URI.toString())) {
				view = savedLayoutInstances.get(ItemsLayout.ITEMS_URI.toString());
			} else {
				view = new ItemsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(ItemsLayout.ITEMS_URI.toString(), view);
			}
		} else if(InvoicesLayout.INVOICES_URI.equals(uri)){
			if(savedLayoutInstances.containsKey(InvoicesLayout.INVOICES_URI.toString())){
				view = savedLayoutInstances.get(InvoicesLayout.INVOICES_URI.toString());
				
			} else{
				view = new InvoicesLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(InvoicesLayout.INVOICES_URI.toString(), view);
			}
			
		} else if(ReportLayout.REPORTS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(ReportLayout.REPORTS_URI.toString())) {
				view = savedLayoutInstances.get(ReportLayout.REPORTS_URI.toString());
			} else {
				view = new ReportLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(ReportLayout.REPORTS_URI.toString(), view);
			}
		}else if(ContactsLayout.CONTACTS_URI.equals(uri)) {
			if (savedLayoutInstances.containsKey(ContactsLayout.CONTACTS_URI.toString())) {
				view = savedLayoutInstances.get(ContactsLayout.CONTACTS_URI.toString());
			} else {
				view = new ContactsLayout(getSupportFragmentManager(), this);
				savedLayoutInstances.put(ContactsLayout.CONTACTS_URI.toString(), view);
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
	public void onLongClickItem(String identifier) {
		ActionMode.Callback callback = currentCustomLinearLayout.getContextualActionBar(identifier);
		if(callback != null){
			startActionMode(callback);
		}
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
		case R.id.synchronize_main_action : 
			doSynchronization();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void doSynchronization() {
		mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
            	if (resultCode == ApplicationConstants.SUCCESS) {
					if (resultData != null) {
						onSOAPResult(resultCode, resultData.getString(NavisionSyncService.SOAP_RESULT));
					} else {
						onSOAPResult(resultCode, null);
					}
				} else if (resultCode == ApplicationConstants.FAILURE) {
					if (resultData != null) {
						onSOAPResult(resultCode, resultData.getString(NavisionSyncService.SOAP_FAULT));
					} else {
						onSOAPResult(resultCode, null);
					}
				}
            }
            
        };
		
		Intent intent = new Intent(this, NavisionSyncService.class);
		ItemsSyncObject itemsSyncObject = new ItemsSyncObject(null, null, Integer.valueOf(1), null, DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject );
		intent.putExtra(NavisionSyncService.EXTRA_RESULT_RECEIVER, getResultReceiver());
		this.startService(intent);
		
	}
	
	public ResultReceiver getResultReceiver() {
        return mReceiver;
    }
	
	public void onSOAPResult(int code, String result) {
		System.out.println(result);
    	return;
    }

}
