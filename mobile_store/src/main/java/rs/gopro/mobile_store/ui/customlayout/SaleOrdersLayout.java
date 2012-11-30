package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.SaleOrderListFragment;
import rs.gopro.mobile_store.util.ApplicationConstants;

import android.app.Activity;
import android.app.ListFragment;
import android.app.DatePickerDialog.OnDateSetListener;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class SaleOrdersLayout extends CustomLinearLayout implements OnClickListener, OnLongClickListener {

	private static final String SALE_ORDER_SCHEME = "settings";
	private static final String SALE_ORDER_AUTHORITY = "sale_orders";
	public static final Uri SALE_ORDER_URI = new Uri.Builder().scheme(SALE_ORDER_SCHEME).authority(SALE_ORDER_AUTHORITY).build();

	Button actionButton;
	EditText dateField;

	public SaleOrdersLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
		
	}

	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_sale_order, null);
		actionButton = (Button) view.findViewById(R.id.sale_order_search);
		actionButton.setOnClickListener(this);
		dateField = (EditText) view.findViewById(R.id.second_search);
		dateField.setOnLongClickListener(this);
		this.addView(view);

	}

	@Override
	public void onClick(View v) {
		FragmentTransaction tr = fragmentManager.beginTransaction();
		Fragment addCommentFragment = new SaleOrderListFragment();
		tr.add(R.id.sale_order_content, addCommentFragment);
		tr.commit();

	}

	@Override
	public boolean onLongClick(View arg0) {
		activity.showDialog(ApplicationConstants.DATE_PICKER_DIALOG);
		return false;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		 EditText editText = (EditText) findViewById(R.id.second_search);
		 editText.setText(year + "/" + (++monthOfYear) + "/" + dayOfMonth);
		
	}
	
}
