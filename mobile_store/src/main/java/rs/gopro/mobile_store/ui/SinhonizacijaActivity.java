package rs.gopro.mobile_store.ui;

import java.util.Date;
import java.util.UUID;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
import rs.gopro.mobile_store.ws.model.GetPotentialCustomerSyncObject;
import rs.gopro.mobile_store.ws.model.HistorySalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsNewSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.SalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SinhonizacijaActivity extends BaseActivity {

	protected CheckBox cbKupci, cbArtikli, cbSpecifikacije, cbOtvoreneStavkeKupaca, cbPoslednjeStavkeKupaca;
	protected Button bAzuriraj;
	protected TextView tvStatus;
	protected ProgressBar pbSinhronizacija;
	protected StringBuilder sb;
	protected String webServiceRequestId;
	protected int checkCount;
	
	protected BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (GetPotentialCustomerSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				sb.append("Kupci i potencijalni kupci uspešno sinhronizovani");
				sb.append("\n");
				tvStatus.setText(sb.toString());
				checkCount--;
			} else if (ItemsNewSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				sb.append("Artikli uspešno sinhronizovani");
				sb.append("\n");
				tvStatus.setText(sb.toString());
				checkCount--;
			} else if (HistorySalesDocumentsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				sb.append("Porudžbenice uspešno sinhronizovane");
				sb.append("\n");
				tvStatus.setText(sb.toString());
				checkCount--;
			} else if (SalesDocumentsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				sb.append("Stavke kupaca uspešno sinhronizovane");
				sb.append("\n");
				tvStatus.setText(sb.toString());
				checkCount--;
			}
			webServiceRequestId = null;
		} else {
			DialogUtil.showInfoDialog(this, getResources().getString(R.string.dialog_title_error_in_sync), syncResult.getResult());
			checkCount = 0;
		}
		if (checkCount == 0) {
			pbSinhronizacija.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sinhronizacija);
		
		cbKupci = (CheckBox) findViewById(R.id.cbKupci);
		cbArtikli = (CheckBox) findViewById(R.id.cbArtikli);
		cbSpecifikacije = (CheckBox) findViewById(R.id.cbSpecifikacije);
		cbOtvoreneStavkeKupaca = (CheckBox) findViewById(R.id.cbOtvoreneStavkeKupaca);
		cbPoslednjeStavkeKupaca = (CheckBox) findViewById(R.id.cbPoslednjeStavkeKupaca);
		bAzuriraj = (Button) findViewById(R.id.bAzuriraj);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		pbSinhronizacija = (ProgressBar) findViewById(R.id.pbSinhronizacija);
		
		if (savedInstanceState != null) {
			webServiceRequestId = savedInstanceState.getString("WEB_SERVICE_REQUEST_ID");
		}
		
		bAzuriraj.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				boolean kupciChecked = cbKupci.isChecked();
				boolean artikliChecked = cbArtikli.isChecked();
				boolean specifikacijeChecked = cbSpecifikacije.isChecked();
				boolean otvoreneStavkeKupacaChecked = cbOtvoreneStavkeKupaca.isChecked();
				boolean poslednjStavkeKupacaChecked = cbPoslednjeStavkeKupaca.isChecked();
				checkCount = 0;
				
				if (kupciChecked || artikliChecked || specifikacijeChecked || otvoreneStavkeKupacaChecked || poslednjStavkeKupacaChecked) {
					pbSinhronizacija.setVisibility(View.VISIBLE);
					sb = new StringBuilder();
					if (kupciChecked) {
						checkCount++;
						sinhronizujKupce();
					}
					if (artikliChecked) {
						checkCount++;
						sinhronizujArtikle();
					}
					if (specifikacijeChecked) {
						checkCount++;
						sinhronizujPorudzbenice();
					}
					if (otvoreneStavkeKupacaChecked) {
						checkCount++;
						sinhronizujOtvoreneStavkeKupaca();
					}
					if (poslednjStavkeKupacaChecked) {
						checkCount++;
						sinhronizujPoslednjeStavkeKupaca();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Potrebno je odabrati makar jednu stavku za sinhronizaciju", Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter getPotentialCustomersSyncObject = new IntentFilter(GetPotentialCustomerSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, getPotentialCustomersSyncObject);
		IntentFilter itemsSyncObject = new IntentFilter(ItemsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, itemsSyncObject);
    	IntentFilter historySalesDocumentsSyncObject = new IntentFilter(HistorySalesDocumentsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, historySalesDocumentsSyncObject);
    	IntentFilter salesDocumentsSyncObject = new IntentFilter(SalesDocumentsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, salesDocumentsSyncObject);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("WEB_SERVICE_REQUEST_ID", webServiceRequestId);
	}

	protected void sinhronizujKupce() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		CustomerSyncObject syncObject = new CustomerSyncObject("", "", salesPersonNo, DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT,syncObject);
		startService(intent);
		Intent intentPotentialCust = new Intent(this, NavisionSyncService.class);
		GetPotentialCustomerSyncObject potentialCustSyncObject = new GetPotentialCustomerSyncObject("", "", salesPersonNo, DateUtils.getWsDummyDate());
		intentPotentialCust.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT,potentialCustSyncObject);
		startService(intentPotentialCust);
	}
	
	protected void sinhronizujArtikle() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		ItemsNewSyncObject itemsSyncObject = new ItemsNewSyncObject(null, null, Integer.valueOf(0), salesPersonNo, null);
		itemsSyncObject.setResetTypeSignal(1);
		itemsSyncObject.setSessionId(webServiceRequestId = UUID.randomUUID().toString());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
		startService(intent);
	}
	
	protected void sinhronizujPorudzbenice() {
		Intent serviceIntent = new Intent(this, NavisionSyncService.class);
		HistorySalesDocumentsSyncObject historySalesDocumentsSyncObject = new HistorySalesDocumentsSyncObject(DateUtils.getFirstDayInMonth(0, 2014), DateUtils.getLastDayInMonth(11, 2014), salesPersonNo);
		serviceIntent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, historySalesDocumentsSyncObject);
		startService(serviceIntent);
	}
	
	protected void sinhronizujOtvoreneStavkeKupaca() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		Date today = new Date();
		SalesDocumentsSyncObject salesDocumentsSyncObject = new SalesDocumentsSyncObject("", Integer.valueOf(-1), "", "", DateUtils.getPreviousDateIgnoringWeekend(today), DateUtils.getTodayDateIgnoringWeekend(today), DateUtils.getWsDummyDate(), salesPersonNo, Integer.valueOf(-1));
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, salesDocumentsSyncObject);
		startService(intent);
	}
	
	protected void sinhronizujPoslednjeStavkeKupaca() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		SalesDocumentsSyncObject salesDocumentsSyncObject = new SalesDocumentsSyncObject("", Integer.valueOf(-1), "", "", DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), DateUtils.getWsDummyDate(), salesPersonNo, Integer.valueOf(1));
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, salesDocumentsSyncObject);
		startService(intent);
	}
	
}
