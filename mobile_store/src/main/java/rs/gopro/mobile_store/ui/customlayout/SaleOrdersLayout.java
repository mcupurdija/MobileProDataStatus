package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.SaleOrderListFragment;
import rs.gopro.mobile_store.ui.fragment.InvoicesFragment;
import rs.gopro.mobile_store.ui.fragment.SaleOrderFragment;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;

import android.app.Activity;
import android.app.ListFragment;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class SaleOrdersLayout extends CustomLinearLayout implements  OnLongClickListener {
	private static String TAG = "CustomersLayout";
	private static final String SALE_ORDER_SCHEME = "settings";
	private static final String SALE_ORDER_AUTHORITY = "sale_orders";
	public static final Uri SALE_ORDER_URI = new Uri.Builder().scheme(SALE_ORDER_SCHEME).authority(SALE_ORDER_AUTHORITY).build();

	private Fragment fragment;

	public SaleOrdersLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
		this.activity = activity;
	}

	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_sale_order, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new SaleOrderFragment();
			tr.replace(R.id.sale_order_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout" + fragment.getId());
		}

	}

	
	@Override
	public boolean onLongClick(View arg0) {
		// activity.showDialog(ApplicationConstants.DATE_PICKER_DIALOG);
		return false;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		/*
		 * EditText editText = (EditText) findViewById(R.id.second_search);
		 * editText.setText(year + "/" + (++monthOfYear) + "/" + dayOfMonth);
		 */

	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
