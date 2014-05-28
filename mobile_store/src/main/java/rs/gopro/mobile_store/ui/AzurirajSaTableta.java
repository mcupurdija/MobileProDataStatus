package rs.gopro.mobile_store.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.FileReaderUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.ws.model.domain.ItemsDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class AzurirajSaTableta extends BaseActivity {

	private static final String TAG = "AzurirajSaTableta";
	private static final int REQUEST_CODE = 6384;

	private Button bAzurirajSaTableta, bOdaberiDokument;
	private TextView tvAzurirajSaTableta;
	private ProgressBar pbAzurirajSaTableta;

	File latestFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_azuriraj_sa_tableta);

		bAzurirajSaTableta = (Button) findViewById(R.id.bAzurirajSaTableta);
		bOdaberiDokument = (Button) findViewById(R.id.bOdaberiDokument);
		tvAzurirajSaTableta = (TextView) findViewById(R.id.tvAzurirajSaTableta);
		pbAzurirajSaTableta = (ProgressBar) findViewById(R.id.pbAzurirajSaTableta);

		pbAzurirajSaTableta.setVisibility(View.GONE);

		bAzurirajSaTableta.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				pbAzurirajSaTableta.setVisibility(View.VISIBLE);
				new ParseFile().execute();
			}
		});
		
		bOdaberiDokument.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showChooser();
			}
		});

	}

	private void showChooser() {
		
		Intent target = FileUtils.createGetContentIntent();
		Intent intent = Intent.createChooser(target, getResources().getString(R.string.bOdaberiDokument));
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					final Uri uri = data.getData();
					LogUtils.LOGI(TAG, "Uri = " + uri.toString());
					try {
						latestFile = FileUtils.getFile(this, uri);
						pbAzurirajSaTableta.setVisibility(View.VISIBLE);
						showFileInfo();
						new UpdateDb().execute();
					} catch (Exception e) {
						LogUtils.LOGI(TAG, "File select error", e);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class ParseFile extends AsyncTask<Void, Void, Void> {

		long latestTimestamp = 0;

		@Override
		protected Void doInBackground(Void... params) {

			FileFilter fileFilter = new FileFilter() {
				@Override
				public boolean accept(File file) {
					if (file.isFile() && file.getName().contains("artikli")) {
						return true;
					}
					return false;
				}
			};

			File[] files = Environment.getExternalStorageDirectory().listFiles(fileFilter);
			if (files != null && files.length > 0) {
				for (File file : files) {
					if (file.lastModified() > latestTimestamp) {
						latestTimestamp = file.lastModified();
						latestFile = file;
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			showFileInfo();
			new UpdateDb().execute();
		}

	}

	private class UpdateDb extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {

			long startTime = System.currentTimeMillis();
			
			if (latestFile != null) {

				String resultString = FileReaderUtil.fileToString(latestFile.getPath());
				if (!resultString.contains("\"No.\"")) {
					return 0;
				} else {
					try {
						List<ItemsDomain> parsedItems = CSVDomainReader.parse(new StringReader(resultString), ItemsDomain.class);
						ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(getContentResolver(), parsedItems);
						getContentResolver().bulkInsert(
								MobileStoreContract.Items.CONTENT_URI,
								valuesForInsert);
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
						return 2;
					}
				}
			} else {
				return 3;
			}
			
			long stopTime = System.currentTimeMillis();
		      long elapsedTime = stopTime - startTime;
		      System.out.println("VREME " + elapsedTime);
			
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			switch (result) {
			case 0:
				Toast.makeText(
						AzurirajSaTableta.this,
						getResources().getString(R.string.azurirajSaTabletaError2),
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				ContentValues syncSuccess = new ContentValues();
				syncSuccess.put(MobileStoreContract.SyncLogs.SYNC_OBJECT_NAME, "ItemsNewSyncObject");
				syncSuccess.put(MobileStoreContract.SyncLogs.SYNC_OBJECT_ID, "ItemsNewSyncObject");
				syncSuccess.put(MobileStoreContract.SyncLogs.SYNC_OBJECT_STATUS, "SUCCESS");
				syncSuccess.put(MobileStoreContract.SyncLogs.SYNC_OBJECT_BATCH, 1);
				syncSuccess.put(MobileStoreContract.SyncLogs.UPDATED_DATE, DateUtils.toDbDate(new Date()));
				syncSuccess.put(MobileStoreContract.SyncLogs.CREATED_DATE, DateUtils.toDbDate(new Date()));
				getContentResolver().insert(MobileStoreContract.SyncLogs.CONTENT_URI, syncSuccess);
				pbAzurirajSaTableta.setVisibility(View.GONE);
				Toast.makeText(
						AzurirajSaTableta.this,
						getResources().getString(R.string.azurirajSaTabletaSuccess),
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(
						AzurirajSaTableta.this,
						getResources().getString(R.string.azurirajSaTabletaError2),
						Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(
						AzurirajSaTableta.this,
						getResources().getString(R.string.azurirajSaTabletaError),
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			finish();
		}
	}
	
	private void showFileInfo() {
		if (latestFile != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Dokument:\t\t\t" + latestFile.getName());
			sb.append("\n\n");
			sb.append("Ukupno artikala:\t" + FileReaderUtil.numberOfLines(latestFile.getPath()));
			tvAzurirajSaTableta.setText(sb.toString());
		}
	}

}
