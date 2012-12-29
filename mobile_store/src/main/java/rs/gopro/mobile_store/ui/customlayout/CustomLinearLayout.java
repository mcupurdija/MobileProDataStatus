package rs.gopro.mobile_store.ui.customlayout;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.LinearLayout;

public abstract class CustomLinearLayout extends LinearLayout implements OnDateSetListener {

	FragmentManager  fragmentManager;
	Activity activity;
	
	public CustomLinearLayout(Context context) {
		super(context);
	}
	
	public CustomLinearLayout(FragmentManager fragmentManager, Activity activity) {
		super(activity);
		this.fragmentManager = fragmentManager;
		this.activity = activity;
		inflateLayout(activity.getLayoutInflater());
		
	}
	
	
	protected abstract void inflateLayout(LayoutInflater layoutInflater);
	
	public abstract ActionMode.Callback getContextualActionBar(String identifier);
	
	
	/**
	 * On default implementation
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
	}

}
