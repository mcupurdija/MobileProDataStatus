package rs.gopro.mobile_store.ui.fragment;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ui.NovaKarticaKupcaMasterActivity;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SharedPreferencesUtil;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.NavisionSyncService;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.model.ItemQtySalesPriceAndDiscSyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NovaKKFragment3 extends Fragment implements LoaderCallbacks<Cursor> {
	
	public static final String TAG = "NovaKKFragment3";
	
	private int saleOrderId, customerId, selectedLineId, minQty, campaignStatus, listPosition = -1;
	private String customerNo, selectedItemNo;
	
	private NovaKKFragment3Listener mCallback;
	
	private RelativeLayout rlKorpaDetaljiContainer;
	private ListView lvKorpa;
	private EditText etKolicina, etKolicinaNaStanju, etCenaBezPopusta, etRabat, etRabatMax, etCenaSaPopustom;
	private Spinner spSkladiste, spNacinObrade;
	private Button bSub, bAdd, bMin, bPreuzmiCenu, bObrisi, bSacuvaj;
	private TextView tvKorpaSuma, tvKorpaSumaPDV, tvNisteOdabraliArtikal;
	private ProgressBar pbBottom;
	
	private CursorAdapter korpaCursorAdapter;
	private ArrayAdapter<CharSequence> backorderAdapter;
	private ArrayAdapter<CharSequence> locationAdapter;
	
	public static NovaKKFragment3 newInstance(int saleOrderId, int customerId, String customerNo) {
		NovaKKFragment3 frag = new NovaKKFragment3();
        Bundle args = new Bundle();
        args.putInt("saleOrderId", saleOrderId);
        args.putInt("customerId", customerId);
        args.putString("customerNo", customerNo);
        frag.setArguments(args);
        return frag;
    }
	
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SyncResult syncResult = intent.getParcelableExtra(NavisionSyncService.SYNC_RESULT);
			pbBottom.setVisibility(View.GONE);
			onSOAPResult(syncResult, intent.getAction());
		}
	};
	
	public void onSOAPResult(SyncResult syncResult, String broadcastAction) {
		if (syncResult.getStatus().equals(SyncStatus.SUCCESS)) {
			if (ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION.equalsIgnoreCase(broadcastAction)) {

				ItemQtySalesPriceAndDiscSyncObject syncObject = (ItemQtySalesPriceAndDiscSyncObject) syncResult.getComplexResult();
				etKolicinaNaStanju.setText(syncObject.getpQuantityAsTxt());
				etRabatMax.setText(syncObject.getpMaximumDiscountPctAsTxt());
				etCenaBezPopusta.setText(syncObject.getpSalesPriceRSDAsTxt());
			}
		}
	}
	
	public interface NovaKKFragment3Listener {
		void onBasketUpdated();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_nova_kartica_kupca_3, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		saleOrderId = getArguments().getInt("saleOrderId", -1);
		customerId = getArguments().getInt("customerId", -1);
		customerNo = getArguments().getString("customerNo", null);
		
		rlKorpaDetaljiContainer = (RelativeLayout) view.findViewById(R.id.rlKorpaDetaljiContainer);
		etKolicina = (EditText) view.findViewById(R.id.etKolicina);
		etKolicinaNaStanju = (EditText) view.findViewById(R.id.etKolicinaNaStanju);
		etCenaBezPopusta = (EditText) view.findViewById(R.id.etCenaBezPopusta);
		etRabat = (EditText) view.findViewById(R.id.etRabat);
		etRabatMax = (EditText) view.findViewById(R.id.etRabatMax);
		etCenaSaPopustom = (EditText) view.findViewById(R.id.etCenaSaPopustom);
		
		pbBottom = (ProgressBar) view.findViewById(R.id.pbBottom);
		
		locationAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.location_line_type_array, android.R.layout.simple_spinner_item);
		locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSkladiste = (Spinner) view.findViewById(R.id.spSkladiste);
		spSkladiste.setAdapter(locationAdapter);
		
		backorderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.backorder_type_array, android.R.layout.simple_spinner_item);
		backorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spNacinObrade = (Spinner) view.findViewById(R.id.spNacinObrade);
		spNacinObrade.setAdapter(backorderAdapter);
		
		bSub = (Button) view.findViewById(R.id.bSub);
		bAdd = (Button) view.findViewById(R.id.bAdd);
		bMin = (Button) view.findViewById(R.id.bMin);
		bPreuzmiCenu = (Button) view.findViewById(R.id.bPreuzmiCenu);
		bObrisi = (Button) view.findViewById(R.id.bObrisi);
		bSacuvaj = (Button) view.findViewById(R.id.bSacuvaj);
		
		tvKorpaSuma = (TextView) view.findViewById(R.id.tvKorpaSuma);
		tvKorpaSumaPDV = (TextView) view.findViewById(R.id.tvKorpaSumaPDV);
		tvNisteOdabraliArtikal = (TextView) view.findViewById(R.id.tvNisteOdabraliArtikal);
		
		lvKorpa = (ListView) view.findViewById(R.id.lvKorpa);
		lvKorpa.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		korpaCursorAdapter = new ListKorpaAdapter(getActivity());
		lvKorpa.setAdapter(korpaCursorAdapter);
		
		lvKorpa.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				lvKorpa.setItemChecked(position, true);
				listPosition = position;
				ExtraData extraData = (ExtraData) ((TextView) view.findViewById(R.id.tvKorpaBrojLinije)).getTag();
				selectedLineId = extraData.getLineId();
				selectedItemNo = extraData.getItemNo();
				minQty = extraData.getItemMinQty();
				campaignStatus = extraData.getCampaignStatus();
				
				if (extraData.getQty() > 0) {
					etKolicina.setText(String.valueOf(extraData.getQty()));
				} else {
					etKolicina.setText("1");
				}
				etKolicinaNaStanju.setText(UIUtils.formatDoubleForUI(extraData.getQtyAvailable()));
				etCenaBezPopusta.setText(UIUtils.formatDoubleForUI(extraData.getPrice()));
				etRabat.setText(UIUtils.formatDoubleForUI(extraData.getDiscount()));
				etRabatMax.setText(UIUtils.formatDoubleForUI(extraData.getMaxDiscount()));
				etCenaSaPopustom.setText(UIUtils.formatDoubleForUI(extraData.getPriceWithDiscount()));
				spSkladiste.setSelection(locationAdapter.getPosition(extraData.getLocation()));
				spNacinObrade.setSelection(extraData.getBackorderType());
				
				hideInfoMessage();
			}
		});
		
		bSub.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int quantity = Integer.parseInt(etKolicina.getText().toString());
					quantity -= 1;
					if (quantity >= minQty) {
						etKolicina.setText(String.valueOf(quantity));
					}
				} catch (NumberFormatException nfe) {
					Toast.makeText(getActivity(), R.string.celobrojnaVrednostError, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.toString());
				}
			}
		});
		
		bAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int quantity = Integer.parseInt(etKolicina.getText().toString());
					quantity += 1;
					etKolicina.setText(String.valueOf(quantity));
				} catch (NumberFormatException nfe) {
					Toast.makeText(getActivity(), R.string.celobrojnaVrednostError, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.toString());
				}
			}
		});

		bMin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					int quantity = Integer.parseInt(etKolicina.getText().toString());
					if (quantity < minQty) {
						etKolicina.setText(String.valueOf(minQty));
					} else {
						quantity += minQty;
						etKolicina.setText(String.valueOf(quantity));
					}
				} catch (NumberFormatException nfe) {
					Toast.makeText(getActivity(), R.string.celobrojnaVrednostError, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.toString());
				}
			}
		});
		
		etRabat.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				calculatePriceWithDiscount();
			}
		});
		
		etCenaBezPopusta.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				calculatePriceWithDiscount();
			}
		});
		
		bPreuzmiCenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				pbBottom.setVisibility(View.VISIBLE);
				getItemQtySalesPriceAndDisc();
			}
		});
		
		bObrisi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog(selectedLineId, selectedItemNo);
			}
		});
		
		bSacuvaj.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ContentValues cv = new ContentValues();
				
				cv.put(SaleOrderLines.SALE_ORDER_ID, saleOrderId);
				cv.put(SaleOrderLines.LOCATION_CODE, getResources().getStringArray(R.array.location_line_type_array)[spSkladiste.getSelectedItemPosition()]);
				
				try {
					String kolicina = etKolicina.getText().toString().trim();
					if (kolicina != null && !kolicina.equals("")) {
						cv.put(MobileStoreContract.SaleOrderLines.QUANTITY, UIUtils.getDoubleFromUI(kolicina));
					} else {
						DialogUtil.showInfoErrorDialog(getActivity(), getString(R.string.kolicinaNijeUneta));
					}
					
					String kolicinaNaStanju = etKolicinaNaStanju.getText().toString().trim();
					if (kolicinaNaStanju != null && !kolicinaNaStanju.equals("")) {
						cv.put(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE, UIUtils.getDoubleFromUI(kolicinaNaStanju));
					} else {
						cv.putNull(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE);
					}
					
					String cena = etCenaBezPopusta.getText().toString().trim();
					if (cena != null && !cena.equals("")) {
						cv.put(MobileStoreContract.SaleOrderLines.PRICE, UIUtils.getDoubleFromUI(cena));
					} else {
						cv.putNull(MobileStoreContract.SaleOrderLines.PRICE);
					}
					
					String rabat = etRabat.getText().toString().trim();
					if (rabat != null && !rabat.equals("")) {
						cv.put(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, UIUtils.getDoubleFromUI(rabat.replace('.', ',')));
					} else {
						cv.putNull(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT);
					}
					
					cv.putNull(MobileStoreContract.SaleOrderLines.MIN_DISCOUNT);
					
					String rabatMax = etRabatMax.getText().toString().trim();
					if (rabatMax != null && !rabatMax.equals("")) {
						cv.put(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT, UIUtils.getDoubleFromUI(rabatMax));
					} else {
						cv.putNull(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT);
					}
				} catch (Exception e) {
					LogUtils.LOGE(TAG, e.getMessage());
					return;
				}

				cv.put(SaleOrderLines.BACKORDER_STATUS, spNacinObrade.getSelectedItemPosition());
				
				getActivity().getContentResolver().update(SaleOrderLines.CONTENT_URI, cv, Tables.SALE_ORDER_LINES + "." + SaleOrderLines._ID + "=?", new String[] { String.valueOf(selectedLineId) });
				korpaCursorAdapter.notifyDataSetChanged();
				restartLoader();
				updateBasketSum();
				mCallback.onBasketUpdated();
				
				((NovaKarticaKupcaMasterActivity) getActivity()).showToast("Stavka uspešno sačuvana");
			}
		});
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mCallback = (NovaKKFragment3Listener) activity;
	}
	
	protected void calculatePriceWithDiscount() {
		
		String price = etCenaBezPopusta.getText().toString();
		String discount = etRabat.getText().toString();
		if (price == null || price.length() < 1) {
			price = "0";
		}
		if (discount == null || discount.length() < 1) {
			discount = "0";
		}
		try {
			double price_d = WsDataFormatEnUsLatin.parseForUIDouble(price);
			double discount_d = WsDataFormatEnUsLatin.parseForUIDouble(discount.replace('.', ','));
			double discounted_price = price_d - (price_d * (discount_d/100));
			etCenaSaPopustom.setText(UIUtils.formatDoubleForUI(discounted_price));
		} catch (NumberFormatException e) {
			LogUtils.LOGE(TAG, e.getMessage());
		}
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	IntentFilter itemQtySalesPriceAndDiscSync = new IntentFilter(ItemQtySalesPriceAndDiscSyncObject.BROADCAST_SYNC_ACTION);
    	LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, itemQtySalesPriceAndDiscSync);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
        
        getArguments().putString("suma", tvKorpaSuma.getText().toString());
        getArguments().putString("sumaPdv", tvKorpaSumaPDV.getText().toString());
    }

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		
		lvKorpa.clearChoices();
		listPosition = -1;
		tvKorpaSuma.setText(getArguments().getString("suma", UIUtils.formatDoubleForUI(0d)));
		tvKorpaSumaPDV.setText(getArguments().getString("sumaPdv", UIUtils.formatDoubleForUI(0d)));
	}
	
	private void confirmDialog(final int lineId, final String sifra) {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
	    adb.setTitle(getString(R.string.potvrda_brisanja) + " [" + sifra + "]");

	    adb.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	deleteLine(lineId);
	        }
	    });

	    adb.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dialog.dismiss();
	        }
	    });
	    adb.show();
	}

	private class ListKorpaAdapter extends CursorAdapter {
		
		public ListKorpaAdapter(Context context) {
            super(context, null, false);
        }

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.nova_kk_korpa_stavka, parent, false);
			bindView(v, context, cursor);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			TextView tvKorpaBrojLinije = (TextView) view.findViewById(R.id.tvKorpaBrojLinije);
			TextView tvKorpaSifraOpis = (TextView) view.findViewById(R.id.tvKorpaSifraOpis);
			TextView tvKorpaInfo1 = (TextView) view.findViewById(R.id.tvKorpaInfo1);
			TextView tvKorpaInfo2 = (TextView) view.findViewById(R.id.tvKorpaInfo2);
			TextView tvKorpaStatus = (TextView) view.findViewById(R.id.tvKorpaStatus);

			try {
				int lineId = cursor.getInt(SaleOrderLinesQuery._ID);
				String brojLinije = cursor.getString(SaleOrderLinesQuery.LINE_NO);
				String sifraArtikla = cursor.getString(SaleOrderLinesQuery.ITEM_NO);
				String opisArtikla = cursor.getString(SaleOrderLinesQuery.DESCRIPTION);
				int itemMinQty = cursor.getInt(SaleOrderLinesQuery.MIN_QTY);
				int campaignStatus = cursor.getInt(SaleOrderLinesQuery.CAMPAIGN_STATUS);
				String location = cursor.getString(SaleOrderLinesQuery.LOCATION_CODE);
				int backorderType = cursor.getInt(SaleOrderLinesQuery.BACKORDER_STATUS);
				
				int qty = 0;
	            if (!cursor.isNull(SaleOrderLinesQuery.QUANTITY)) {
	            	qty = cursor.getInt(SaleOrderLinesQuery.QUANTITY);
	            }
	            double qtyAvailable = 0d;
	            if (!cursor.isNull(SaleOrderLinesQuery.QUANTITY_AVAILABLE)) {
	            	qtyAvailable = cursor.getDouble(SaleOrderLinesQuery.QUANTITY_AVAILABLE);
	            }
	            double price = 0d;
	            if (!cursor.isNull(SaleOrderLinesQuery.PRICE)) {
	            	price = cursor.getDouble(SaleOrderLinesQuery.PRICE);
	            }
	            double discount = 0d;
	            if (!cursor.isNull(SaleOrderLinesQuery.REAL_DISCOUNT)) {
	            	discount = cursor.getDouble(SaleOrderLinesQuery.REAL_DISCOUNT);
	            }
	            double maxDiscount = 0d;
	            if (!cursor.isNull(SaleOrderLinesQuery.MAX_DISCOUNT)) {
	            	maxDiscount = cursor.getDouble(SaleOrderLinesQuery.MAX_DISCOUNT);
	            }
	            double discountPrice = price - (price * (discount / 100));
	            double iznos = qty * discountPrice;
	            
	            int verify_status = -1;
	            if (!cursor.isNull(SaleOrderLinesQuery.VERIFY_STATUS)) {
	            	verify_status = cursor.getInt(SaleOrderLinesQuery.VERIFY_STATUS);
	            }
	            int price_discount_status = -1;
	            if (!cursor.isNull(SaleOrderLinesQuery.PRICE_DISCOUNT_STATUS)) {
	            	price_discount_status = cursor.getInt(SaleOrderLinesQuery.PRICE_DISCOUNT_STATUS);
	            }
	            int quote_refused_status = -1;
	            if (!cursor.isNull(SaleOrderLinesQuery.QUOTE_REFUSED_STATUS)) {
	            	quote_refused_status = cursor.getInt(SaleOrderLinesQuery.QUOTE_REFUSED_STATUS);
	            }
	            
	            String status = "-";
	            if ((price_discount_status == -1) && (verify_status == -1) && (quote_refused_status == -1)) {
	            	status = "-";
	            } else if ((price_discount_status == 0) && (verify_status == 0) && (quote_refused_status == 0)) {
	            	status = "-";
	            } else if ((price_discount_status == -1 || price_discount_status == 0 || price_discount_status == 3) && (verify_status == -1 || verify_status == 0 || verify_status == 5) && (quote_refused_status == -1 || quote_refused_status == 0 || quote_refused_status == 3)) {
	            	status = "OK";
	            } else {
	            	status = "Problem";
	            }
	            
	            ExtraData extraData = new ExtraData(lineId, itemMinQty, campaignStatus, sifraArtikla, qty, qtyAvailable, price, discount, maxDiscount, discountPrice, location, backorderType);
	            tvKorpaBrojLinije.setTag(extraData);
				
				tvKorpaBrojLinije.setText(brojLinije);
				tvKorpaSifraOpis.setText(String.format("%s - %s", sifraArtikla, opisArtikla));
				tvKorpaInfo1.setText(String.format("Naručeno: %d  Raspoloživo: %s", qty, UIUtils.formatDoubleForUI(qtyAvailable)));
				tvKorpaInfo2.setText(String.format("Cena: %s  Popust: %s%%  Iznos: %s", UIUtils.formatDoubleForUI(price), UIUtils.formatDoubleForUI(discount), UIUtils.formatDoubleForUI(iznos)));
				tvKorpaStatus.setText(status);
			
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
		}
	}
	
	private void getItemQtySalesPriceAndDisc() {

		String kolicina = etKolicina.getText().toString();
		if (kolicina == null || kolicina.equals("")) {
			DialogUtil.showInfoErrorDialog(getActivity(), getString(R.string.kolicinaNijeUneta));
			return;
		}
		
		String locationCode = getResources().getStringArray(R.array.location_line_type_array)[0];
		String salesPersonNo = SharedPreferencesUtil.getSalePersonNo(getActivity());
		int potentialCustomerSignal = isPotentialCustomer(customerId) == false ? 0 : 1;
		int documentType = getDocumentType();
		
		Intent intent = new Intent(getActivity(), NavisionSyncService.class);
		ItemQtySalesPriceAndDiscSyncObject itemQtySalesPriceAndDiscSyncObject = new ItemQtySalesPriceAndDiscSyncObject(selectedItemNo, locationCode, campaignStatus, Integer.valueOf(potentialCustomerSignal), customerNo, kolicina, salesPersonNo, documentType, "", 0, "", "", "", "", "", "", "", "");
		intent.putExtra(NavisionSyncService.EXTRA_WS_SYNC_OBJECT, itemQtySalesPriceAndDiscSyncObject);
		getActivity().startService(intent);
	}
	
	private boolean isPotentialCustomer(int customerId) {
		Cursor potentialCustomerCursor = getActivity().getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
				"(" + Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers._ID + "=?" , new String[] { String.valueOf(customerId) }, null);
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
	}
	
	public void updateBasketSum() {
		Cursor c = getActivity().getContentResolver().query(SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId)), SaleOrderLinesQuery.PROJECTION, null, null, null);
		double sum = 0d, itemQty = 0d, itemPrice = 0d, itemDiscount = 0d, itemDiscountPrice = 0d, itemTotal = 0d;
		while (c.moveToNext()) {
			try {
				itemQty = c.getDouble(SaleOrderLinesQuery.QUANTITY);
				itemPrice = c.getDouble(SaleOrderLinesQuery.PRICE);
				itemDiscount = c.getDouble(SaleOrderLinesQuery.REAL_DISCOUNT);
				itemDiscountPrice = itemPrice - (itemPrice * (itemDiscount / 100));
				itemTotal = itemQty * itemDiscountPrice;
				sum += itemTotal;
			} catch (Exception e) {
				LogUtils.LOGE(TAG, e.getMessage());
			}
		}
		c.close();
		
		tvKorpaSuma.setText(UIUtils.formatDoubleForUI(sum));
		tvKorpaSumaPDV.setText(UIUtils.formatDoubleForUI(sum * 1.2));
	}
	
	private int getDocumentType() {
		Cursor c = getActivity().getContentResolver().query(SaleOrders.buildSaleOrderUri(String.valueOf(saleOrderId)), new String[] { SaleOrders.DOCUMENT_TYPE }, null, null, null);
		c.moveToFirst();
		int documentType = c.getInt(0);
		c.close();
		return documentType;
	}
	
	private void hideInfoMessage() {
		if (tvNisteOdabraliArtikal.isShown()) {
			tvNisteOdabraliArtikal.setVisibility(View.GONE);
		}
		if (!rlKorpaDetaljiContainer.isShown()) {
			rlKorpaDetaljiContainer.setVisibility(View.VISIBLE);
		}
	}
	
	private void showInfoMessage() {
		rlKorpaDetaljiContainer.setVisibility(View.GONE);
		tvNisteOdabraliArtikal.setVisibility(View.VISIBLE);
	}
	
	private void deleteLine(int lineId) {
		getActivity().getContentResolver().delete(SaleOrderLines.CONTENT_URI, Tables.SALE_ORDER_LINES + "." + SaleOrderLines._ID + "=?", new String[] { String.valueOf(lineId) });
		listPosition = -1;
		restartLoader();
		showInfoMessage();
		updateBasketSum();
		mCallback.onBasketUpdated();
	}
	
	private class ExtraData {
		
		private int lineId, itemMinQty, campaignStatus, qty, backorderType;
		private String itemNo, location;
		private double qtyAvailable, price, discount, maxDiscount, priceWithDiscount;

		public ExtraData(int lineId, int itemMinQty, int campaignStatus, String itemNo, int qty, double qtyAvailable, double price, double discount, double maxDiscount, double priceWithDiscount, String location, int backorderType) {
			super();
			this.lineId = lineId;
			this.itemMinQty = itemMinQty;
			this.campaignStatus = campaignStatus;
			this.itemNo = itemNo;
			this.qty = qty;
			this.qtyAvailable = qtyAvailable;
			this.price = price;
			this.discount = discount;
			this.maxDiscount = maxDiscount;
			this.priceWithDiscount = priceWithDiscount;
			this.location = location;
			this.backorderType = backorderType;
		}

		public int getLineId() {
			return lineId;
		}

		public int getItemMinQty() {
			return itemMinQty;
		}

		public int getCampaignStatus() {
			return campaignStatus;
		}

		public String getItemNo() {
			return itemNo;
		}

		public int getQty() {
			return qty;
		}

		public double getQtyAvailable() {
			return qtyAvailable;
		}

		public double getPrice() {
			return price;
		}

		public double getDiscount() {
			return discount;
		}

		public double getMaxDiscount() {
			return maxDiscount;
		}

		public double getPriceWithDiscount() {
			return priceWithDiscount;
		}

		public int getBackorderType() {
			return backorderType;
		}

		public String getLocation() {
			return location;
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), SaleOrderLines.buildSaleOrderLinesUri(String.valueOf(saleOrderId)), SaleOrderLinesQuery.PROJECTION, null, null, SaleOrderLines.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (cursor != null) {
			korpaCursorAdapter.swapCursor(cursor);
			if (listPosition != -1) {
				lvKorpa.setItemChecked(listPosition, true);
			}
			if (lvKorpa.getCount() == 0) {
				tvNisteOdabraliArtikal.setText(getString(R.string.korpaJePrazna));
			} else {
				tvNisteOdabraliArtikal.setText(getString(R.string.nisteOdabraliArtikal));
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		korpaCursorAdapter.swapCursor(null);
	}
	
	public void restartLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	private interface SaleOrderLinesQuery {

        String[] PROJECTION = {
                BaseColumns._ID,
                SaleOrderLines.SALE_ORDER_ID, 
                SaleOrderLines.ITEM_NO, 
                SaleOrderLines.DESCRIPTION, 
                SaleOrderLines.DESCRIPTION2, 
                SaleOrderLines.LINE_NO, 
                SaleOrderLines.QUANTITY,
                SaleOrderLines.PRICE, 
                SaleOrderLines.MAX_DISCOUNT, 
                SaleOrderLines.REAL_DISCOUNT, 
                SaleOrderLines.PRICE_DISCOUNT_STATUS, 
                SaleOrderLines.QUOTE_REFUSED_STATUS, 
                SaleOrderLines.VERIFY_STATUS, 
                SaleOrderLines.QUANTITY_AVAILABLE, 
                SaleOrderLines.MIN_QTY, 
                SaleOrderLines.CAMPAIGN_STATUS, 
                SaleOrderLines.LOCATION_CODE, 
                SaleOrderLines.BACKORDER_STATUS
        };

        int _ID = 0;
//      int SALE_ORDER_ID = 1;
        int ITEM_NO = 2;
        int DESCRIPTION = 3;
//      int DESCRIPTION2 = 4;
        int LINE_NO = 5;
        int QUANTITY = 6;
        int PRICE = 7;
        int MAX_DISCOUNT = 8;
        int REAL_DISCOUNT = 9;
        int PRICE_DISCOUNT_STATUS = 10;
        int QUOTE_REFUSED_STATUS = 11;
        int VERIFY_STATUS = 12;
        int QUANTITY_AVAILABLE = 13;
        int MIN_QTY = 14;
        int CAMPAIGN_STATUS = 15;
        int LOCATION_CODE = 16;
        int BACKORDER_STATUS = 17;
	}

}
