package rs.gopro.mobile_store.ui.widget;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.AddContactActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ActionMode.Callback;

public class ContactContextualActionBar implements Callback {

	String contactId;
	Activity activity;
	
	public ContactContextualActionBar(Activity activity, String contactId) {
	this.activity = activity;
	this.contactId = contactId;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deactivate_contact:
			activity.getContentResolver().delete(MobileStoreContract.Contacts.buildContactsUri(contactId), null, null);
			mode.finish();
			return true;
		case R.id.edit_contact:
			Intent intent = new Intent(activity, AddContactActivity.class);
			intent.putExtra(AddContactActivity.CONTACT_ID, contactId);
			activity.startActivity(intent);
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
		inflater.inflate(R.menu.contact_contextual_action_bar, menu);
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
