package rs.gopro.mobile_store.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreDatabase;
import rs.gopro.mobile_store.util.SqlParserUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		
		new LoadViewTask().execute();
	}
	
	private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
		
    	private final String[] CREATE_FILES = {"create_mobile_store.sql", "create_mobile_store2.sql", "create_mobile_store3.sql", "create_mobile_store4.sql", "create_mobile_store5.sql", "create_mobile_store6.sql", "create_mobile_store7.sql"};
    	private InputStream inputStream;
    	private BufferedReader bufferedReader;
    	private int lineCount = 0, counter = 0;
    	
    	private ProgressDialog progressDialog;
    	
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(StartActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Initializing data for first use!");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);

            for (String s : CREATE_FILES) {
            	try {
					inputStream = getAssets().open("sql/" + s);
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					while (bufferedReader.readLine() != null) {
                    	lineCount++;
                    }
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
            }

            progressDialog.setMax(lineCount);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
        	
            MobileStoreDatabase mobileStoreDatabase = new MobileStoreDatabase(StartActivity.this);
            SQLiteDatabase db = mobileStoreDatabase.getWritableDatabase();

            db.beginTransaction();
            
            for (String s : CREATE_FILES) {
            	try {
					for (String sqlInstruction : SqlParserUtil.parseSqlFile("sql/" + s, getAssets())) {
						db.execSQL(sqlInstruction);
						counter++;
					    publishProgress(counter);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
            
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();

            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
	
}
