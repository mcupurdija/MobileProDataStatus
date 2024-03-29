package rs.gopro.mobile_store.ws.model;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.GiftItems;
import rs.gopro.mobile_store.provider.MobileStoreContract.GiftItemsColumns;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.csv.CSVDomainWriter;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.text.format.Time;
import au.com.bytecode.opencsv.CSVWriter;

public class SetGiftsForRealizedVisitsSyncObject extends SyncObject{

	private static String TAG = "SetGiftsForRealizedVisitsSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SET_GIFTS_FOR_REALIZED_VISITS_SYNC_ACTION";
	
	private String pCSVString;
	private int statusOK, visitId;
	
	public static final Creator<SetGiftsForRealizedVisitsSyncObject> CREATOR = new Creator<SetGiftsForRealizedVisitsSyncObject>() {

		@Override
		public SetGiftsForRealizedVisitsSyncObject createFromParcel(Parcel source) {
			return new SetGiftsForRealizedVisitsSyncObject(source);
		}

		@Override
		public SetGiftsForRealizedVisitsSyncObject[] newArray(int size) {
			return new SetGiftsForRealizedVisitsSyncObject[size];
		}

	};
	
	public SetGiftsForRealizedVisitsSyncObject() {
		super();
	}
	
	public SetGiftsForRealizedVisitsSyncObject(int visitId) {
		super();
		this.visitId = visitId;
	}

	public SetGiftsForRealizedVisitsSyncObject(Parcel source) {
		super(source);
		setVisitId(source.readInt());
		setpCSVString(source.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeInt(getVisitId());
		dest.writeString(getpCSVString());
	}

	@Override
	public String getWebMethodName() {
		return "SetGiftsForRealizedVisits";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();
		
		setpCSVString(createGiftData());
		PropertyInfo cSVString = new PropertyInfo();
		cSVString.setName("pCSVString");
		cSVString.setValue(pCSVString);
		cSVString.setType(String.class);
		properies.add(cSVString);
		
		System.out.println(">>> " + getpCSVString());
		
		PropertyInfo pStatusOK = new PropertyInfo();
		pStatusOK.setName("statusOK");
		pStatusOK.setValue(false);
		pStatusOK.setType(Boolean.class);
		properies.add(pStatusOK);
		
		return properies;
	}
	
	private String createGiftData() {
		
		Cursor cursor = context.getContentResolver().query(GiftItems.CONTENT_URI, GiftItemsQuery.PROJECTION, Tables.GIFT_ITEMS + "." + GiftItemsColumns.VISIT_ID + "=?", new String[] { String.valueOf(visitId) }, null);
		List<String[]> lines = CSVDomainWriter.parseCursor(cursor, GiftItemsQuery.PROJECTION_TYPE);
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, ';', '"');
		writer.writeAll(lines);
		try {
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
		cursor.close();
		return stringWriter.toString();		
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
		if (soapResponse.toString().equals("true")) {
			ContentValues cv = new ContentValues();
			cv.put(Visits.GIFT_STATUS, 2);
			return contentResolver.update(Visits.CONTENT_URI, cv, Tables.VISITS + "._id=?", new String[] { String.valueOf(visitId) });
		}
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	public int getStatusOK() {
		return statusOK;
	}

	public void setStatusOK(int statusOK) {
		this.statusOK = statusOK;
	}
	
	public int getVisitId() {
		return visitId;
	}

	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}

	private interface GiftItemsQuery {

        String[] PROJECTION = {
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.SALES_PERSON_NO,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.VISIT_DATE,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.VISIT_ARRIVAL_TIME,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.POTENTIAL_CUSTOMER,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.CUSTOMER_NO,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.ITEM_NO,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.ITEM_QUANTITY,
        		Tables.GIFT_ITEMS + "." + GiftItemsColumns.ITEM_NOTE
        };
        
        Type[] PROJECTION_TYPE = {
        		String.class,
        		Date.class,
        		Time.class,
        		Integer.class,
        		String.class,
        		String.class,
        		String.class,
        		String.class
        };
	}

}
