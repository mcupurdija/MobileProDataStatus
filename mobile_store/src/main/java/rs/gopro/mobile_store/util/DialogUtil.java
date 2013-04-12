package rs.gopro.mobile_store.util;

import rs.gopro.mobile_store.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {

	public DialogUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static void showInfoErrorDialog(Context context, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(
				context).create();

	    // Setting Dialog Title
	    alertDialog.setTitle(context.getResources().getString(R.string.dialog_title_error_in_sync));
	    // Setting Dialog Message
	    alertDialog.setMessage(message);
	    // Setting Icon to Dialog
	    alertDialog.setIcon(R.drawable.ic_launcher);
	    // Setting OK Button
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// Write your code here to execute after dialog closed
            }
	    });
	
	    // Showing Alert Message
	    alertDialog.show();
	}
	
	public static Dialog showInfoDialog(Context context, String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(
				context).create();
	    // Setting Dialog Title
	    alertDialog.setTitle(title);
	    // Setting Dialog Message
	    alertDialog.setMessage(message);
	    // Setting Icon to Dialog
	    alertDialog.setIcon(R.drawable.ic_launcher);
	    // Setting OK Button
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	// Write your code here to execute after dialog closed
            }
	    });
	    // Showing Alert Message
	    alertDialog.show();
	    
	    return alertDialog;
	}
	
}
