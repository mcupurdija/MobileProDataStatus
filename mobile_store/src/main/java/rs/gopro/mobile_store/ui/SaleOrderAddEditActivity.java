package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.LogUtils;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor> {

	private static final String TAG = "SaleOrderAddEditActivity";
	
	private static String DEFAULT_VIEW_TYPE = MobileStoreContract.SaleOrders.CONTENT_ITEM_TYPE;
	
	private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private String mViewType;
	
	public SaleOrderAddEditActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_sale_order);
		
		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);
	}
	
	/**
	 * Routes activity on passed data through intent.
	 * It is ran only in onCreate().
	 * @param intent
	 * @param b
	 */
	private void routeIntent(Intent intent, boolean b) {
		// get action from intent
		String action = intent.getAction();
		if (action == null) {
			LogUtils.LOGE(TAG, "Activity called without action!");
			return;
		}
		// get URI from intent
		Uri uri = intent.getData();
		if (uri == null) {
			LogUtils.LOGE(TAG, "Activity called without URI!");
			return;
		}
		// check uri mime type
		mViewType = getContentResolver().getType(uri);
		// wrong uri, only working with single item
		if (!mViewType.equals(DEFAULT_VIEW_TYPE)) {
			LogUtils.LOGE(TAG, "Activity called with wrong URI! URI:"+uri.toString());
			return;
		}
		
		// check action and route from there
		if (Intent.ACTION_EDIT.equals(action)) {
			initComponents(action);
		} else if (Intent.ACTION_INSERT.equals(action)) {
			initComponents(action);
		} else if (Intent.ACTION_VIEW.equals(action))  {
			initComponents(action);
		}
	}

	private void initComponents(String action) {
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}

}
