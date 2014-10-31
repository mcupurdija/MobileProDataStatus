package rs.gopro.mobile_store.ui.widget;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.Methods;
import rs.gopro.mobile_store.ui.AddMethodActivity;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SetTeachingMethodSyncObject;
import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MethodContextualActionBar implements Callback {

	String methodId;
	Activity activity;
	
	public MethodContextualActionBar(Activity activity, String methodId) {
		this.activity = activity;
		this.methodId = methodId;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.new_method_by_pattern:
				Intent newByPatternMethodIntent = new Intent(activity, AddMethodActivity.class);
				newByPatternMethodIntent.setAction(Intent.ACTION_INSERT);
				newByPatternMethodIntent.putExtra("method_id", Integer.valueOf(methodId));
				activity.startActivity(newByPatternMethodIntent);
				mode.finish();
				return true;
			case R.id.edit_method:
				Intent editMethodIntent = new Intent(activity, AddMethodActivity.class);
				editMethodIntent.setAction(Intent.ACTION_EDIT);
				editMethodIntent.putExtra("method_id", Integer.valueOf(methodId));
				activity.startActivity(editMethodIntent);
				mode.finish();
				return true;
			case R.id.delete_method:
				activity.getContentResolver().delete(Methods.buildMethodsUri(Integer.valueOf(methodId)), null, null);
				mode.finish();
				return true;
			case R.id.sync_method:
				Intent serviceIntent = new Intent(activity, NavisionSyncService.class);
				SetTeachingMethodSyncObject setTeachingMethodSyncObject = new SetTeachingMethodSyncObject(Integer.valueOf(methodId));
				serviceIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, setTeachingMethodSyncObject);
				activity.startService(serviceIntent);
				mode.finish();
				return true;
			default:
				break;
			}
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.method_contextual_action_bar, menu);
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
