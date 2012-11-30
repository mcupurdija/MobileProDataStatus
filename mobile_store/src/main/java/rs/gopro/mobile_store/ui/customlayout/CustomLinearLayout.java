package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

public abstract class CustomLinearLayout extends LinearLayout implements OnDateSetListener {

	FragmentManager  fragmentManager;
	Activity activity;
	
	public CustomLinearLayout(FragmentManager fragmentManager, Activity activity) {
		super(activity);
		this.fragmentManager = fragmentManager;
		this.activity = activity;
		inflateLayout(activity.getLayoutInflater());
		
	}
	
	
	protected abstract void inflateLayout(LayoutInflater layoutInflater);
	
	
	/**
	 * On default implementation
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}

}
