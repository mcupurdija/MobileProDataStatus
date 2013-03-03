package rs.gopro.mobile_store.ui.customlayout;

import rs.gopro.mobile_store.R;
import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

public class SentOrdersStatusLayout extends CustomLinearLayout {

	private static final String TAG = "SentOrdersStatusLayout";
	
	private static final String SENT_ORDERS_STATUS_SCHEME = "settings";
	private static final String SENT_ORDERS_STATUS_AUTHORITY = "sent_orders_status";
	public static final Uri SENT_ORDERS_STATUS_URI = new Uri.Builder().scheme(SENT_ORDERS_STATUS_SCHEME).authority(SENT_ORDERS_STATUS_AUTHORITY).build();

	public SentOrdersStatusLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_sale_orders_status, null);
//		TextView textView = (TextView) view.findViewById(R.id.report_content);
		this.addView(view);
//		String string = loadText("lorem.txt",getResources().getAssets());
//		textView.setText(string);

	}

//	private String loadText(String name, AssetManager assetManager) {
//		StringBuilder text = new StringBuilder();
//		try {
//			InputStream inputStream = assetManager.open(name);
//			InputStreamReader isReader = new InputStreamReader(inputStream);
//			BufferedReader buffReader = new BufferedReader(isReader);
//			String line = null;
//			while ((line = buffReader.readLine()) != null) {
//				text.append(line);
//			}
//		} catch (IOException e) {
//			LogUtils.LOGE(TAG, "", e);
//			e.printStackTrace();
//		}
//
//		return text.toString();
//	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier, String visitType) {
		return null;
	}

}
