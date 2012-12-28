package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.ContactsFragment;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.ActionMode.Callback;
import android.view.View;

public class ContactsLayout extends CustomLinearLayout {
	private static String TAG = "ContactsLayout";
	private static final String CONTACTS_SCHEME = "settings";
	private static final String CONTACTS_AUTHORITY = "contacts";
	public static final Uri CONTACTS_URI = new Uri.Builder().scheme(CONTACTS_SCHEME).authority(CONTACTS_AUTHORITY).build();
	private Fragment fragment;

	public ContactsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if(fragment == null){
			View view = layoutInflater.inflate(R.layout.content_holder_contacts, null);
			this.addView(view);
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			fragment = new ContactsFragment();
			transaction.replace(R.id.contacts_content, fragment);
			transaction.commit();
			LogUtils.LOGI(TAG, "inflateLayout"+fragment.getId());
		}
	}

	@Override
	public Callback getContextualActionBar(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
