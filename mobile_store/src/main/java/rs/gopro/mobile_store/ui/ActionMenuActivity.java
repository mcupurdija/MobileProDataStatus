package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Class that handle action menu. Extend if want to access action menu.
 * 
 * @author Vladimir Makevic
 *
 */
public class ActionMenuActivity extends Activity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.upaction:
	            //newGame();
	            return true;
//	        case R.id.help:
	            //showHelp();
//	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
