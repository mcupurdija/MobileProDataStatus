package rs.gopro.mobile_store.ws.model;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.Methods;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.csv.CSVDomainWriter;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import au.com.bytecode.opencsv.CSVWriter;

public class SetTeachingMethodSyncObject extends SyncObject {

	public static String TAG = "SetTeachingMethodSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SET_TEACHING_METHOD_SYNC_ACTION";

	private String pCSVString;
	private int methodId;
	
	public static final Creator<SetTeachingMethodSyncObject> CREATOR = new Creator<SetTeachingMethodSyncObject>() {
		
		@Override
		public SetTeachingMethodSyncObject[] newArray(int size) {
			return new SetTeachingMethodSyncObject[size];
		}
		
		@Override
		public SetTeachingMethodSyncObject createFromParcel(Parcel source) {
			return new SetTeachingMethodSyncObject(source);
		}
	};
	
	public SetTeachingMethodSyncObject(Parcel source) {
		super(source);
		setMethodId(source.readInt());
		setpCSVString(source.readString());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeInt(getMethodId());
		dest.writeString(getpCSVString());
	}

	public SetTeachingMethodSyncObject() {
		super();
	}

	public SetTeachingMethodSyncObject(int methodId) {
		super();
		this.methodId = methodId;
	}

	@Override
	public String getWebMethodName() {
		return "SetTeachingMethod";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {

		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		setpCSVString(createHeader());
		
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(pCSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);
		
		return properties;
	}
	
	private String createHeader() {
		Cursor cursorHeader = context.getContentResolver().query(Methods.buildMethodsUri(methodId), MethodQuery.PROJECTION, null, null, null);
		List<String[]> header = CSVDomainWriter.parseCursor(cursorHeader, MethodQuery.PROJECTION_TYPE);
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
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}
	
	private interface MethodQuery {
		String[] PROJECTION = { 
			Tables.ITEMS + "." + Items.ITEM_NO,
			"SCH." + Customers.CUSTOMER_NO,
			Tables.METHODS + "." + Methods.SUBJECT,
			Tables.METHODS + "." + Methods.CLASS,
			"PR1." + Customers.CUSTOMER_NO,
			"PR2." + Customers.CUSTOMER_NO,
			Tables.METHODS + "." + Methods.SCHOOL_SIZE,
			Tables.METHODS + "." + Methods.SCHOOL_YEAR,
			Tables.METHODS + "." + Methods.COMMENT,
			Tables.METHODS + "." + Methods.SALESPERSON_CODE
		};
		
		Type[] PROJECTION_TYPE = {
				String.class,
				String.class,
				String.class,
				Integer.class,
				String.class,
				String.class,
				Integer.class,
				String.class,
				String.class,
				String.class
		};
	}

}
