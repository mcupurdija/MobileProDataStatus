package rs.gopro.mobile_store.ui.widget;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.AddVisitActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 * @author Administrator
 * Create Callback methods for contextual action bar
 */
public class VisitContextualMenu implements  ActionMode.Callback{
	String visitId;
	Activity activity;
	public VisitContextualMenu(Activity activity, String visitId){
		this.visitId = visitId;
		this.activity = activity;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		
		 switch (item.getItemId()) {
		case R.id.deactivate_visit:
			activity.getContentResolver().delete(MobileStoreContract.Visits.buildVisitUri(visitId),null, null);
			mode.finish();
			return true;
		case R.id.edit_visit :
			Intent intent = new Intent(activity, AddVisitActivity.class);
			intent.putExtra(AddVisitActivity.VISIT_ID, visitId);
			activity.startActivity(intent);
			mode.finish();
			activity.finish();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		 // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.visit_multipane_contextual_menu, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}
	
}