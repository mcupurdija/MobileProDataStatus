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

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.csv.CSVDomainWriter;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import android.text.format.Time;
import au.com.bytecode.opencsv.CSVWriter;

public class SetPlannedVisitsToCustomersSyncObject extends SyncObject {

	public static String TAG = "SetPlannedVisitsToCustomersSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SET_PLANNED_VISITS_TO_CUSTOMERS_SYNC_ACTION";
	
	
	private int visitId;
	private String pCSVString;
	
	public static final Creator<SetPlannedVisitsToCustomersSyncObject> CREATOR = new Creator<SetPlannedVisitsToCustomersSyncObject>() {

		@Override
		public SetPlannedVisitsToCustomersSyncObject createFromParcel(Parcel source) {
			return new SetPlannedVisitsToCustomersSyncObject(source);
		}

		@Override
		public SetPlannedVisitsToCustomersSyncObject[] newArray(int size) {
			return new SetPlannedVisitsToCustomersSyncObject[size];
		}

	};
	
	public SetPlannedVisitsToCustomersSyncObject() {
		super();
	}

	public SetPlannedVisitsToCustomersSyncObject(int visitId) {
		super();
		this.visitId = visitId;
	}
	
	public SetPlannedVisitsToCustomersSyncObject(Parcel source) {
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
		return "SetPlannedVisitsToCustomers";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		setpCSVString(createVisitsData());
		PropertyInfo cSVString = new PropertyInfo();
		cSVString.setName("pCSVString");
		cSVString.setValue(pCSVString);
		cSVString.setType(String.class);
		properies.add(cSVString);
		
		return properies;
	}

	private String createVisitsData() {
		// get header data
		Cursor cursorHeader = context.getContentResolver().query(MobileStoreContract.Visits.buildVisitsPlannedExport(), VisitsQuery.PROJECTION, Tables.VISITS+"._ID=?", new String[] { String.valueOf(visitId) }, null);
		List<String[]> header = CSVDomainWriter.parseCursor(cursorHeader, VisitsQuery.PROJECTION_TYPE);
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, ';', '"');
		writer.writeAll(header);
		try {
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
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
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		if (soapResponse == null || soapResponse.toString().equals("")) {
			// ok servis no error response
			return 1;
		}
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getVisitId() {
		return visitId;
	}

	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	private interface VisitsQuery {

        String[] PROJECTION = {
        		MobileStoreContract.SalesPerson.SALE_PERSON_NO,
        		MobileStoreContract.Visits.VISIT_DATE,
        		MobileStoreContract.Visits.ARRIVAL_TIME,
        		MobileStoreContract.Visits.DEPARTURE_TIME,
        		MobileStoreContract.Visits.POTENTIAL_CUSTOMER,
        		MobileStoreContract.Customers.CUSTOMER_NO,
        		MobileStoreContract.Visits.NOTE,
        		MobileStoreContract.Visits.IS_DELETED
        };
        
        Type[] PROJECTION_TYPE = {
        		String.class,
        		Date.class,
        		Time.class,
        		Time.class,
        		Integer.class,
        		String.class,
        		String.class,
        		Integer.class
        };
	}
}
