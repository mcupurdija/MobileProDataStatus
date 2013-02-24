package rs.gopro.mobile_store.ui.customlayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rs.gopro.mobile_store.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ReportLayout extends CustomLinearLayout {

	private static final String REPORTS_SCHEME = "settings";
	private static final String REPORTS_AUTHORITY = "reports";
	public static final Uri REPORTS_URI = new Uri.Builder().scheme(REPORTS_SCHEME).authority(REPORTS_AUTHORITY).build();

	public ReportLayout(FragmentManager fragmentManager, Activity activity) {
		super(fragmentManager, activity);
	}

	@Override
	protected void inflateLayout(LayoutInflater layoutInflater) {
		View view = layoutInflater.inflate(R.layout.content_holder_reports, null);
//		TextView textView = (TextView) view.findViewById(R.id.report_content);
		this.addView(view);
//		String string = loadText("lorem.txt",getResources().getAssets());
//		textView.setText(string);

	}

	private String loadText(String name, AssetManager assetManager) {
		StringBuilder text = new StringBuilder();
		try {
			InputStream inputStream = assetManager.open(name);
			InputStreamReader isReader = new InputStreamReader(inputStream);
			BufferedReader buffReader = new BufferedReader(isReader);
			String line = null;
			while ((line = buffReader.readLine()) != null) {
				text.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return text.toString();
	}

	@Override
	public ActionMode.Callback getContextualActionBar(String identifier, String visitType) {
		// TODO Auto-generated method stub
		return null;
	}

}
