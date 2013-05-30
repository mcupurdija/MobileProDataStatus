package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.fragment.InvoicesFragment;
import rs.gopro.mobile_store.util.LogUtils;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class CustomerLedgerEntriesLayout extends CustomLinearLayout {

	private static String TAG = "CustomerLedgerEntriesLayout";
	private static final String CUSTOMER_LEDGER_ENTRIES_SCHEME = "settings";
	private static final String CUSTOMER_LEDGER_ENTRIES_AUTHORITY = "customer_ledger_entries";
	public static final Uri CUSTOMER_LEDGER_ENTRIES_URI = new Uri.Builder().scheme(CUSTOMER_LEDGER_ENTRIES_SCHEME).authority(CUSTOMER_LEDGER_ENTRIES_AUTHORITY).build();
	private Fragment fragment;

	public CustomerLedgerEntriesLayout(Context context) {
		super(context);
	}
	
	public CustomerLedgerEntriesLayout(FragmentManager fragmentManager, Activity activity) {
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
		return null;
	}
	
//	@Override
//	public void doSynchronization() {
//		Intent intent = new Intent(activity, NavisionSyncService.class);
//		SalesDocumentsSyncObject syncObject = new SalesDocumentsSyncObject("", Integer.valueOf(0), "", "", DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), "",Integer.valueOf(-1));
//		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
//		activity.startService(intent);
//	}

}
