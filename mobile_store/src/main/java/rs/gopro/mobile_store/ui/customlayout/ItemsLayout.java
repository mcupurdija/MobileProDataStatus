package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.ItemsListFragment;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

public class ItemsLayout extends CustomLinearLayout {
	
	private static final String ITEMS_SCHEME = "settings";
	private static final String ITEMS_AUTHORITY = "items";
	public static final Uri ITEMS_URI = new Uri.Builder().scheme(ITEMS_SCHEME).authority(ITEMS_AUTHORITY).build();


	public ItemsLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_items, null);
		this.addView(view);
		FragmentTransaction tr = fragmentManager.beginTransaction();
		Fragment addCommentFragment = new ItemsListFragment();
		tr.add(R.id.item_content, addCommentFragment);
		tr.commit();
	}

}
