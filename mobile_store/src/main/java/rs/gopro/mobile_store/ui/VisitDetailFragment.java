package rs.gopro.mobile_store.ui;

import android.support.v4.app.Fragment;

public class VisitDetailFragment extends Fragment {
	
	public interface Callbacks {
        public void onVisitIdAvailable(String visitId);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onVisitIdAvailable(String visitId) {}
    };

    private Callbacks mCallbacks = sDummyCallbacks;
}
