package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.SalesStatisticIndexDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.provider.BaseColumns;

public class UpdateSalesStatisticIndexSyncObject extends SyncObject {

	public static String TAG = "UpdateSalesStatisticIndexSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.UPDATE_SALE_STATISTICS_INDEX_SYNC_ACTION";
	
	private String pCSVString, pCustomerNo, pBusinessUnitCode;
	private int doUpdate;
	
	public static final Creator<UpdateSalesStatisticIndexSyncObject> CREATOR = new Creator<UpdateSalesStatisticIndexSyncObject>() {

		@Override
		public UpdateSalesStatisticIndexSyncObject createFromParcel(Parcel source) {
			return new UpdateSalesStatisticIndexSyncObject(source);
		}

		@Override
		public UpdateSalesStatisticIndexSyncObject[] newArray(int size) {
			return new UpdateSalesStatisticIndexSyncObject[size];
		}

	};
	
	public UpdateSalesStatisticIndexSyncObject() {
		super();
	}

	public UpdateSalesStatisticIndexSyncObject(String pCSVString, String pCustomerNo,
			String pBusinessUnitCode, int doUpdate) {
		super();
		this.pCSVString = pCSVString;
		this.pCustomerNo = pCustomerNo;
		this.pBusinessUnitCode = pBusinessUnitCode;
		this.doUpdate = doUpdate;
	}
	
	public UpdateSalesStatisticIndexSyncObject(Parcel source) {
		super(source);
		setpCSVString(source.readString());
		setpCustomerNo(source.readString());
		setpBusinessUnitCode(source.readString());
		setDoUpdate(source.readInt());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCSVString());
		dest.writeString(getpCustomerNo());
		dest.writeString(getpBusinessUnitCode());
		dest.writeInt(getDoUpdate());
	}

	@Override
	public String getWebMethodName() {
		return "UpdateSalesStatisticIndex";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();
		
		PropertyInfo pCSVStringInfo = new PropertyInfo();
		pCSVStringInfo.setName("pCSVString");
		pCSVStringInfo.setValue(pCSVString);
		pCSVStringInfo.setType(String.class);
		properies.add(pCSVStringInfo);
		
		PropertyInfo pCustomerNoInfo = new PropertyInfo();
		pCustomerNoInfo.setName("pCustomerNo");
		pCustomerNoInfo.setValue(pCustomerNo);
		pCustomerNoInfo.setType(String.class);
		properies.add(pCustomerNoInfo);
		
		PropertyInfo pBusinessUnitCodeInfo = new PropertyInfo();
		pBusinessUnitCodeInfo.setName("pBusinessUnitCode");
		pBusinessUnitCodeInfo.setValue(pBusinessUnitCode);
		pBusinessUnitCodeInfo.setType(String.class);
		properies.add(pBusinessUnitCodeInfo);
		
		PropertyInfo doUpdateInfo = new PropertyInfo();
		doUpdateInfo.setName("doUpdate");
		doUpdateInfo.setValue(false);
		doUpdateInfo.setType(Boolean.class);
		properies.add(doUpdateInfo);
		
		return properies;
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {

		// PROVERA DA LI INDEKS TREBA DA SE AZURIRA
		if (soapResponse.getPropertyAsString("doUpdate").equals("true")) {
			
			// PREPISIVANJE STAROG U NOVI
			Cursor cursor = contentResolver.query(ElectronicCardCustomer.CONTENT_URI, EKKQuery.PROJECTION, null, null, null);
			while (cursor.moveToNext()) {
				ContentValues cv = new ContentValues();
				cv.put(ElectronicCardCustomer.NEW_SORTING_INDEX, cursor.getInt(EKKQuery.SORTING_INDEX));
				contentResolver.update(ElectronicCardCustomer.CONTENT_URI, cv, Tables.ELECTRONIC_CARD_CUSTOMER + "." + ElectronicCardCustomer._ID + "=?", new String[] { String.valueOf(cursor.getInt(EKKQuery._ID)) });
			}
			cursor.close();
			
			// AZURIRANJE NOVOG NA OSNOVU POZICIJE STAROG INDEKSA
			List<SalesStatisticIndexDomain> parsedDomains = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVString")), SalesStatisticIndexDomain.class);
			ContentValues[] parsedContentValues = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedDomains);
			
			int count = 0;
			for (ContentValues parsedContentValue : parsedContentValues) {
				ContentValues cv = new ContentValues();
				cv.put(ElectronicCardCustomer.NEW_SORTING_INDEX, parsedContentValue.getAsInteger(SalesStatisticIndexDomain.newSortingIndexKey));
				contentResolver.update(ElectronicCardCustomer.CONTENT_URI, cv, Tables.ELECTRONIC_CARD_CUSTOMER + "." + ElectronicCardCustomer.SORTING_INDEX + "=?", new String[] { String.valueOf(parsedContentValue.getAsInteger(SalesStatisticIndexDomain.sortingIndexKey)) });
				count++;
			}
			
			return count;
		} else {
			return 0;
		}
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	public String getpCustomerNo() {
		return pCustomerNo;
	}

	public void setpCustomerNo(String pCustomerNo) {
		this.pCustomerNo = pCustomerNo;
	}

	public String getpBusinessUnitCode() {
		return pBusinessUnitCode;
	}

	public void setpBusinessUnitCode(String pBusinessUnitCode) {
		this.pBusinessUnitCode = pBusinessUnitCode;
	}

	public int getDoUpdate() {
		return doUpdate;
	}

	public void setDoUpdate(int doUpdate) {
		this.doUpdate = doUpdate;
	}
	
	private interface EKKQuery {
		String[] PROJECTION = new String[] {
				
				Tables.ELECTRONIC_CARD_CUSTOMER + "." + BaseColumns._ID,
				Tables.ELECTRONIC_CARD_CUSTOMER + "." + ElectronicCardCustomer.SORTING_INDEX
		};
		
		int _ID = 0;
		int SORTING_INDEX = 1;
	}

}
