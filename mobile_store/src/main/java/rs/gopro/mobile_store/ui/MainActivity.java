package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.adapter.ActionsAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Holder for main menu and first nested level (content)
 * 
 * @author aleksandar
 * 
 */

public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
	
	ActionsAdapter actionsAdapter;
	private Integer currentItemPosition = Integer.valueOf(1);
	public static final String CURRENT_POSITION_KEY = "current_position";

	/**
	 * Create menu as list view on left side of screen. Create content space
	 * right of menu.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			currentItemPosition = savedInstanceState.getInt(CURRENT_POSITION_KEY);
		}
		setContentView(R.layout.main_screen);
		final ListView viewActionsList = (ListView) findViewById(R.id.main_menu_list);
		actionsAdapter = new ActionsAdapter(this);
		viewActionsList.setAdapter(actionsAdapter);
		viewActionsList.setOnItemClickListener(this);
		setContentTitle(currentItemPosition);
	}
	

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long flags) {
		Uri uri = actionsAdapter.getItem(position);
		setContentTitle(position);
		this.currentItemPosition = position;
		actionsAdapter.markSecletedItem(view);
		

	}

	

	/**
	 * Get title from arrays and setup in text field
	 * 
	 * @param position
	 */
	private void setContentTitle(int position) {
		TextView	contentTitle = (TextView) findViewById(R.id.content_title);
		contentTitle.setText(actionsAdapter.getItemTitle(position));

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(outState != null){
		outState.putInt(CURRENT_POSITION_KEY, currentItemPosition);
		}
	}
	
	public Integer getCurrentItemPosition(){
	return  currentItemPosition;
}

}
