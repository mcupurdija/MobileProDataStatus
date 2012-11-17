package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.adapter.ActionsAdapter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Holder for main menu and first nested level (content)
 * @author aleksandar
 *
 */
public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
	TextView contentTitle = null;
	ActionsAdapter actionsAdapter;

	/**
	 * Create menu as list view on left side of screen.
	 * Create content space right of menu.
	 *  
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
	    setDefaultContentTitle();
		final ListView viewActionsList = (ListView) findViewById(R.id.actions);
		actionsAdapter = new ActionsAdapter(this);
		viewActionsList.setAdapter(actionsAdapter);
		viewActionsList.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long flags) {
		setContentTitle(position);
		System.out.println("POsition: "+position);
		Uri uri = actionsAdapter.getItem(position);
		System.out.println(uri);
		System.out.println(uri.getAuthority());
	}

	
	
	
	/**
	 * Set default title
	 * Get first position value from titles array
	 */
	private void setDefaultContentTitle(){
		setContentTitle(1);
	}
	
	/**
	 * Get title from arrays and setup in text field
	 * @param position
	 */
	private void setContentTitle(int position) {
		final Resources res = getApplicationContext().getResources();
		String[] titles = res.getStringArray(R.array.actions_names);
		contentTitle = (TextView) findViewById(R.id.content_title);
		contentTitle.setText(titles[position]);
	}

}
