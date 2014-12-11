package rs.gopro.mobile_store.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.model.CustomerSyncObject;
import rs.gopro.mobile_store.ws.model.GetPotentialCustomerSyncObject;
import rs.gopro.mobile_store.ws.model.HistorySalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsNewSyncObject;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.NewSalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.SalesDocumentsSyncObject;
import rs.gopro.mobile_store.ws.model.SalesHeadersSyncObject;
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

	protected CheckBox cbKupci, cbArtikli, cbDokumenti, cbSpecifikacije, cbOtvoreneStavkeKupaca, cbPoslednjeStavkeKupaca, cbStatusPoslatihPorudzbina;
	protected Button bAzuriraj;
	protected TextView tvStatus;
	protected ProgressBar pbSinhronizacija;
	protected StringBuilder sb;
	protected String webServiceRequestId;
	protected int checkCount;
	protected int tekucaGodina;
	
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
			} else if (NewSalesDocumentsSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				sb.append("Dokumenti uspešno sihnronizovani");
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
			} else if (SalesHeadersSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {
				sb.append("Statusi poslatih porudžbina uspešno sinhronizovani");
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
		
		tekucaGodina = Calendar.getInstance().get(Calendar.YEAR);
		
		cbKupci = (CheckBox) findViewById(R.id.cbKupci);
		cbArtikli = (CheckBox) findViewById(R.id.cbArtikli);
		cbDokumenti = (CheckBox) findViewById(R.id.cbDokumenti);
		cbSpecifikacije = (CheckBox) findViewById(R.id.cbSpecifikacije);
		cbOtvoreneStavkeKupaca = (CheckBox) findViewById(R.id.cbOtvoreneStavkeKupaca);
		cbPoslednjeStavkeKupaca = (CheckBox) findViewById(R.id.cbPoslednjeStavkeKupaca);
		cbStatusPoslatihPorudzbina = (CheckBox) findViewById(R.id.cbStatusPoslatihPorudzbina);
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
				boolean dokumentiChecked = cbDokumenti.isChecked();
				boolean specifikacijeChecked = cbSpecifikacije.isChecked();
				boolean otvoreneStavkeKupacaChecked = cbOtvoreneStavkeKupaca.isChecked();
				boolean poslednjStavkeKupacaChecked = cbPoslednjeStavkeKupaca.isChecked();
				boolean statusPoslatihPorudzbinaChecked = cbStatusPoslatihPorudzbina.isChecked();
				checkCount = 0;
				
				if (kupciChecked || artikliChecked || dokumentiChecked || specifikacijeChecked || otvoreneStavkeKupacaChecked || poslednjStavkeKupacaChecked || statusPoslatihPorudzbinaChecked) {
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
					if (dokumentiChecked) {
						checkCount++;
						sinhronizujDokumente();
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
					if (statusPoslatihPorudzbinaChecked) {
						checkCount++;
						sinhronizujStatusePoslatihPorudzbina();
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
    	IntentFilter newSalesDocumentsSyncObject = new IntentFilter(NewSalesDocumentsSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, newSalesDocumentsSyncObject);
    	IntentFilter salesHeaderSyncObject = new IntentFilter(SalesHeadersSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, salesHeaderSyncObject);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("WEB_SERVICE_REQUEST_ID", webServiceRequestId);
		outState.putInt("COUNT", checkCount);
		outState.putBoolean("PROGRESS", pbSinhronizacija.isShown());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		sb = new StringBuilder();
		checkCount = savedInstanceState.getInt("COUNT");
		
		if (savedInstanceState.getBoolean("PROGRESS", false)) {
			pbSinhronizacija.setVisibility(View.VISIBLE);
		}
	}

	protected void sinhronizujKupce() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		CustomerSyncObject syncObject = new CustomerSyncObject("", "", salesPersonNo, DateUtils.getWsDummyDate());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
		startService(intent);
		Intent intentPotentialCust = new Intent(this, NavisionSyncService.class);
		GetPotentialCustomerSyncObject potentialCustSyncObject = new GetPotentialCustomerSyncObject("", "", salesPersonNo, DateUtils.getWsDummyDate());
		intentPotentialCust.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, potentialCustSyncObject);
		startService(intentPotentialCust);
	}
	
	protected void sinhronizujArtikle() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		ItemsNewSyncObject itemsSyncObject = new ItemsNewSyncObject(null, null, Integer.valueOf(-1), salesPersonNo, null);
		itemsSyncObject.setResetTypeSignal(1);
		itemsSyncObject.setSessionId(webServiceRequestId = UUID.randomUUID().toString());
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemsSyncObject);
		startService(intent);
	}
	
	protected void sinhronizujPorudzbenice() {
		Intent serviceIntent = new Intent(this, NavisionSyncService.class);
		HistorySalesDocumentsSyncObject historySalesDocumentsSyncObject = new HistorySalesDocumentsSyncObject(DateUtils.getFirstDayInMonth(0, tekucaGodina), DateUtils.getLastDayInMonth(11, tekucaGodina), salesPersonNo);
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
	
	protected void sinhronizujDokumente() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		NewSalesDocumentsSyncObject newSalesDocumentsSyncObject = new NewSalesDocumentsSyncObject(2, "", DateUtils.getWsDummyDate(), DateUtils.getLastDayInMonth(11, tekucaGodina), salesPersonNo);
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, newSalesDocumentsSyncObject);
		startService(intent);
	}
	
	protected void sinhronizujStatusePoslatihPorudzbina() {
		Intent intent = new Intent(this, NavisionSyncService.class);
		SalesHeadersSyncObject syncObject = new SalesHeadersSyncObject("", Integer.valueOf(ApplicationConstants.SENT_DOCUMENTS_STATUS_HEADER_DOC_TYPE_ORDERS), "", "", DateUtils.getWsDummyDate(), rs.gopro.mobile_store.util.DateUtils.getWsDummyDate(), salesPersonNo, Integer.valueOf(-1));
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, syncObject);
		startService(intent);
	}
	
}
