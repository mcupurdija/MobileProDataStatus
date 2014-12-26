package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

public class CustomerSyncObject extends SyncObject {

	public static String TAG = "CustomerSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_SYNC_ACTION";

	private String cSVString;
	private String customerNoa46;
	private String salespersonCode;
	private Date dateModified;

	public static final Creator<CustomerSyncObject> CREATOR = new Creator<CustomerSyncObject>() {

		@Override
		public CustomerSyncObject createFromParcel(Parcel source) {
			return new CustomerSyncObject(source);
		}

		@Override
		public CustomerSyncObject[] newArray(int size) {
			return new CustomerSyncObject[size];
		}

	};

	public CustomerSyncObject() {
		super();
	}

	public CustomerSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setSalespersonCode(parcel.readString());
		setDateModified(new Date(parcel.readLong()));

	}

	public CustomerSyncObject(String cSVString, String customerNoa46, String salespersonCode, Date dateModified) {
		super();
		this.cSVString = cSVString;
		this.customerNoa46 = customerNoa46;
		this.salespersonCode = salespersonCode;
		this.dateModified = dateModified;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeString(getCustomerNoa46());
		dest.writeString(getSalespersonCode());
		dest.writeLong(getDateModified().getTime());

	}

	@Override
	public String getWebMethodName() {
		return "GetCustomers";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);

		PropertyInfo dateModifiedInfo = new PropertyInfo();
		dateModifiedInfo.setName("pDateModified");
		dateModifiedInfo.setValue(calculateDateModified());
		dateModifiedInfo.setType(Date.class);
		properties.add(dateModifiedInfo);
		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		
		List<CustomerDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), CustomerDomain.class);
		// every received should be active
		ContentValues defaultCV = new ContentValues();
		defaultCV.put(Customers.IS_ACTIVE, 1);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems, defaultCV);
		
		/*
		// there is customers, now to reset who is active first, all are inactive
		if (parsedItems != null && parsedItems.size() > 0) {
			ContentValues cv = new ContentValues();
			cv.put(Customers.IS_ACTIVE, 0);
			contentResolver.update(Customers.CONTENT_URI, cv, Customers.CUSTOMER_NO + " is not null or " + Customers.CUSTOMER_NO + " not like ''", null);
		}
		*/
		
		int numOfInserted = contentResolver.bulkInsert(Customers.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}
	
	private Date calculateDateModified() {
		
		Cursor cursor = context.getContentResolver().query(MobileStoreContract.SyncLogs.CONTENT_URI, new String[] { "MAX(DATE(" + SyncLogs.CREATED_DATE + "))" }, SyncLogs.SYNC_OBJECT_ID+"=? AND "+SyncLogs.SYNC_OBJECT_STATUS+"=?", new String[] { getTag(), SyncStatus.SUCCESS.toString() }, null);
		if (cursor.moveToFirst()) {
			return DateUtils.getLocalDbShortDate(cursor.getString(0));
		} else {
			return DateUtils.getWsDummyDate();
		}
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	public String getcSVString() {
		return cSVString;
	}

	public void setcSVString(String cSVString) {
		this.cSVString = cSVString;
	}

	public String getCustomerNoa46() {
		return customerNoa46;
	}

	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

}
