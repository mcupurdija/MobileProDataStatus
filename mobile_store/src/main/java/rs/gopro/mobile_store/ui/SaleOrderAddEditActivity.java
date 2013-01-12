package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class SaleOrderAddEditActivity  extends BaseActivity implements LoaderCallbacks<Cursor> {

	public SaleOrderAddEditActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_visit);
		
		// routes data from intent that called this activity to business logic
		routeIntent(getIntent(), savedInstanceState != null);
	}
	
	private void routeIntent(Intent intent, boolean b) {
		// get action from intent
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		// get URI from intent
		Uri uri = intent.getData();
		if (uri == null) {
			return;
		}
		
		String mimeType = getContentResolver().getType(uri);
		
		if (Intent.ACTION_EDIT.equals(action)) {
			
		} else if (Intent.ACTION_INSERT.equals(action)) {
			
		} else {
			
		}
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
