package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.InvoicesFragment;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.SalesDocumentsSyncObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class InvoicesLayout extends CustomLinearLayout {

	private static String TAG = "InvoicesLayout";
	private static final String INVOICES_SCHEME = "settings";
	private static final String INVOICES_AUTHORITY = "invoices";
	public static final Uri INVOICES_URI = new Uri.Builder().scheme(INVOICES_SCHEME).authority(INVOICES_AUTHORITY).build();
	private Fragment fragment;

	public InvoicesLayout(Context context) {
		super(context);
	}
	
	public InvoicesLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		if (fragment == null) {
			View view = layoutInflater.inflate(R.layout.content_holder_invoices, null);
			this.addView(view);
			FragmentTransaction tr = fragmentManager.beginTransaction();
			fragment = new InvoicesFragment();
			tr.replace(R.id.invoices_content, fragment);
			tr.commit();
			LogUtils.LOGI(TAG, "inflateLayout" + fragment.getId());
		}
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier, String visitType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void doSynchronization() {
		Intent intent = new Intent(activity, NavisionSyncService.class);
		SalesDocumentsSyncObject syncObject = new SalesDocumentsSyncObject("", Integer.valueOf(0), "", "", DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), "",Integer.valueOf(-1));
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
		activity.startService(intent);
	}

}
