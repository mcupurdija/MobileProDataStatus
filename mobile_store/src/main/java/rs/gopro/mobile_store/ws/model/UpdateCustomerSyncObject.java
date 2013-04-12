package rs.gopro.mobile_store.ws.model;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
import au.com.bytecode.opencsv.CSVWriter;

public class UpdateCustomerSyncObject extends SyncObject {

	public static String TAG = "UpdateCustomerSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.UPDATE_CUSTOMER_SYNC_ACTION";

	private String pCSVString;
	private int customerId;
	
	public static final Creator<UpdateCustomerSyncObject> CREATOR = new Creator<UpdateCustomerSyncObject>() {

		@Override
		public UpdateCustomerSyncObject createFromParcel(Parcel source) {
			return new UpdateCustomerSyncObject(source);
		}

		@Override
		public UpdateCustomerSyncObject[] newArray(int size) {
			return new UpdateCustomerSyncObject[size];
		}

	};
	
	public UpdateCustomerSyncObject() {
		super();
	}

	public UpdateCustomerSyncObject(int customerId) {
		super();
		this.customerId = customerId;
	}
	
	public UpdateCustomerSyncObject(String pCSVString) {
		super();
		this.pCSVString = pCSVString;
	}

	public UpdateCustomerSyncObject(Parcel source) {
		super(source);
		setCustomerId(source.readInt());
		setcSVString(source.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(getStatusMessage());
		dest.writeInt(getCustomerId());
		dest.writeString(getcSVString());
	}

	@Override
	public String getWebMethodName() {
		return "UpdateCustomers";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		setcSVString(createHeader());
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(pCSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);
		
		return properties;
	}

	private String createHeader() {
		// get header data
		Cursor cursorHeader = context.getContentResolver().query(MobileStoreContract.Customers.buildUpdateCustomersExport(), PotentialCustomerQuery.PROJECTION, Tables.CUSTOMERS+"._id=?", new String[] { String.valueOf(customerId) }, null);
		List<String[]> header = CSVDomainWriter.parseCursor(cursorHeader, PotentialCustomerQuery.PROJECTION_TYPE);
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
//		List<SetShortPotentialCustomerDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), SetShortPotentialCustomerDomain.class);
//		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
//		valuesForInsert[0].put(MobileStoreContract.Customers._ID, customerId);
//		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.Customers.CONTENT_URI, valuesForInsert);
//		return numOfInserted;
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getcSVString() {
		return pCSVString;
	}

	public void setcSVString(String cSVString) {
		this.pCSVString = cSVString;
	}

	private interface PotentialCustomerQuery {
		String[] PROJECTION = {
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.CUSTOMER_NO,
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.NAME, 
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.NAME_2,
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.ADDRESS,
				//MobileStoreContract.Customers.CITY,

				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.PHONE, 
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.MOBILE, 

				Tables.SALES_PERSONS+"."+MobileStoreContract.Customers.SALE_PERSON_NO, 
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.VAT_REG_NO,
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.POST_CODE,
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.EMAIL, 
				Tables.CONTACTS+"."+MobileStoreContract.Contacts.CONTACT_NO, 
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.COMPANY_ID,
				
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT, 
				Tables.CUSTOMERS+"."+MobileStoreContract.Customers.NUMBER_OF_GREY_COAT
        };
		
		Type[] PROJECTION_TYPE = {
				String.class,
				String.class,
				String.class,
				String.class,
				//String.class,
				
				String.class,
				String.class,
				
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,			
				String.class,
				String.class,
		};
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
}
