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
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerDomain;
import rs.gopro.mobile_store.ws.model.domain.PotentialCustomerDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class GetPotentialCustomerSyncObject extends SyncObject {

	public static String TAG = "GetPotentialCustomerSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.GET_POTENTIAL_CUSTOMER_SYNC_ACTION";

	private String cSVString;
	private String pPotentialCustomerNoa46;
	private String salespersonCode;
	private Date dateModified;

	public static final Creator<GetPotentialCustomerSyncObject> CREATOR = new Creator<GetPotentialCustomerSyncObject>() {

		@Override
		public GetPotentialCustomerSyncObject createFromParcel(Parcel source) {
			return new GetPotentialCustomerSyncObject(source);
		}

		@Override
		public GetPotentialCustomerSyncObject[] newArray(int size) {
			return new GetPotentialCustomerSyncObject[size];
		}

	};

	public GetPotentialCustomerSyncObject() {
		super();
	}

	public GetPotentialCustomerSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setpPotentialCustomerNoa46(parcel.readString());
		setSalespersonCode(parcel.readString());
		setDateModified(new Date(parcel.readLong()));

	}

	public GetPotentialCustomerSyncObject(String cSVString, String customerNoa46, String salespersonCode, Date dateModified) {
		super();
		this.cSVString = cSVString;
		this.pPotentialCustomerNoa46 = customerNoa46;
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
		dest.writeString(getpPotentialCustomerNoa46());
		dest.writeString(getSalespersonCode());
		dest.writeLong(getDateModified().getTime());

	}

	@Override
	public String getWebMethodName() {
		return "GetPotentialCustomers";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo potentialCustomerNoa46 = new PropertyInfo();
		potentialCustomerNoa46.setName("pPotentialCustomerNoa46");
		potentialCustomerNoa46.setValue(pPotentialCustomerNoa46);
		potentialCustomerNoa46.setType(String.class);
		properties.add(potentialCustomerNoa46);

		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);

		PropertyInfo dateModifiedInfo = new PropertyInfo();
		dateModifiedInfo.setName("pDateModified");
		dateModifiedInfo.setValue(dateModified);
		dateModifiedInfo.setType(Date.class);
		properties.add(dateModifiedInfo);

		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		
		
		List<PotentialCustomerDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), PotentialCustomerDomain.class);
		// every received should be active
		ContentValues defaultCV= new ContentValues();
		defaultCV.put(Customers.IS_ACTIVE, 1);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems, defaultCV);
		// there is customers, now to reset who is active first, all are inactive
		if (parsedItems != null && parsedItems.size() > 0) {
			ContentValues cv= new ContentValues();
			cv.put(Customers.IS_ACTIVE, 0);
			context.getContentResolver().update(Customers.CONTENT_URI, cv, Customers.CONTACT_COMPANY_NO+" is null or " + Customers.CONTACT_COMPANY_NO + " like ''", null);
		}
		int numOfInserted = contentResolver.bulkInsert(Customers.CONTENT_URI, valuesForInsert);
		return numOfInserted;
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

	public String getpPotentialCustomerNoa46() {
		return pPotentialCustomerNoa46;
	}

	public void setpPotentialCustomerNoa46(String customerNoa46) {
		this.pPotentialCustomerNoa46 = customerNoa46;
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
