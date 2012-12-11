package rs.gopro.mobile_store.ui;

import java.util.Calendar;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.adapter.ActionsAdapter;
import rs.gopro.mobile_store.ui.customlayout.CustomLinearLayout;
import rs.gopro.mobile_store.ui.customlayout.CustomersLayout;
import rs.gopro.mobile_store.ui.customlayout.ItemsLayout;
import rs.gopro.mobile_store.ui.customlayout.PlanOfVisitsLayout;
import rs.gopro.mobile_store.ui.customlayout.ReportLayout;
import rs.gopro.mobile_store.ui.customlayout.SaleOrdersLayout;
import rs.gopro.mobile_store.util.ApplicationConstants;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Holder for main menu and first nested level (content)
 * 
 * @author aleksandar
 * 
 */

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	ActionsAdapter actionsAdapter;
	private Integer currentItemPosition = Integer.valueOf(1);
	public static final String CURRENT_POSITION_KEY = "current_position";

	CustomLinearLayout currentCustomLinearLayout;

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
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long flags) {
		setContentTitle(position);
		this.currentItemPosition = position;
		actionsAdapter.markSecletedItem(view);
		updateContent(position);

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
			view = new SaleOrdersLayout(getSupportFragmentManager(), this);
		} else if (PlanOfVisitsLayout.PLAN_OF_VISITS_URI.equals(uri)) {
			view = new PlanOfVisitsLayout(getSupportFragmentManager(), this);
		}else if(CustomersLayout.CUSTOMERS_URI.equals(uri)){
			view = new CustomersLayout(getSupportFragmentManager(), this);
		}else if(ItemsLayout.ITEMS_URI.equals(uri)){
			view = new ItemsLayout(getSupportFragmentManager(), this);
		}else if(ReportLayout.REPORTS_URI.equals(uri)){
			view = new ReportLayout(getSupportFragmentManager(), this);
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

}
