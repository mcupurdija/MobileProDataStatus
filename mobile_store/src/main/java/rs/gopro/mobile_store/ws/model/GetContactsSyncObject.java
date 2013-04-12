package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.ContactsDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class GetContactsSyncObject extends SyncObject {

	public static String TAG = "GetContactsSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.GET_CONTACTS_SYNC_ACTION";

	private String cSVString;
	private String pContactNo;
	private String pPotentialCustomerNoa46;
	private String salespersonCode;
	private Date dateModified;
	
	public static final Creator<GetContactsSyncObject> CREATOR = new Creator<GetContactsSyncObject>() {

		@Override
		public GetContactsSyncObject createFromParcel(Parcel source) {
			return new GetContactsSyncObject(source);
		}

		@Override
		public GetContactsSyncObject[] newArray(int size) {
			return new GetContactsSyncObject[size];
		}

	};
	
	public GetContactsSyncObject() {
		super();
	}

	public GetContactsSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setpContactNo(parcel.readString());
		setpPotentialCustomerNoa46(parcel.readString());
		setSalespersonCode(parcel.readString());
		setDateModified(new Date(parcel.readLong()));
	}
	
	public GetContactsSyncObject(String cSVString, String pContactNo, String customerNoa46,
			String salespersonCode, Date dateModified) {
		super();
		this.cSVString = cSVString;
		this.pContactNo = pContactNo;
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
		dest.writeString(getpContactNo());
		dest.writeString(getpPotentialCustomerNoa46());
		dest.writeString(getSalespersonCode());
		dest.writeLong(getDateModified().getTime());
	}

	@Override
	public String getWebMethodName() {
		return "GetContacts";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo contactNo = new PropertyInfo();
		contactNo.setName("pContactNoa46");
		contactNo.setValue(pContactNo);
		contactNo.setType(String.class);
		properties.add(contactNo);
		
		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pPotentialCustomerNoa46");
		customerNoa46Info.setValue(pPotentialCustomerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

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
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		List<ContactsDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), ContactsDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.Contacts.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
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

	public void setpPotentialCustomerNoa46(String pPotentialCustomerNoa46) {
		this.pPotentialCustomerNoa46 = pPotentialCustomerNoa46;
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

	public String getpContactNo() {
		return pContactNo;
	}

	public void setpContactNo(String pContactNo) {
		this.pContactNo = pContactNo;
	}
	
}
