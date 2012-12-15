package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.provider.MobileStoreContract;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

public class VisitDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private Uri mVisitUri;
	
	public interface Callbacks {
		public void onVisitIdAvailable(String visitId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onVisitIdAvailable(String visitId) {
		}
	};

	private Callbacks mCallbacks = sDummyCallbacks;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = BaseActivity.fragmentArgumentsToIntent(getArguments());
        mVisitUri = intent.getData();
        if (mVisitUri == null) {
            return;
        }

        //mImageFetcher = UIUtils.getImageFetcher(getActivity());
        //mImageFetcher.setImageFadeIn(false);

        //setHasOptionsMenu(true);
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mVisitUri == null) {
            return;
        }

        // Start background query to load vendor details
        getLoaderManager().initLoader(VisitsQuery._TOKEN, null, this);
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}
	
	private interface VisitsQuery {
		int _TOKEN = 0x4;

        String[] PROJECTION = {
                BaseColumns._ID,
                MobileStoreContract.Visits.SALES_PERSON_ID,
                MobileStoreContract.Visits.CUSTOMER_ID,
                MobileStoreContract.Visits.CUSTOMER_NAME,
                MobileStoreContract.Visits.VISIT_DATE
        };

        int _ID = 0;
        int SALES_PERSON_ID = 1;
        int CUSTOMER_ID = 2;
        int CUSTOMER_NAME = 3;
        int VISIT_DATE = 4;
	}
}
