package rs.gopro.mobile_store.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;

public class VisitListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(VisitListFragment.class);
	
	private CursorAdapter mAdapter;
	private String mSelectedVisitId;
	
	public interface Callbacks {
        /** Return true to select (activate) the vendor in the list, false otherwise. */
        public boolean onVisitSelected(String visitId);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean onVisitSelected(String visitId) {
            return true;
        }
    };

    private Callbacks mCallbacks = sDummyCallbacks;
	
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
	
	/**
	 * Sets current position in list from caller.
	 * @param id
	 */
	public void setSelectedVisitId(String id) {
		mSelectedVisitId = id;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

}
