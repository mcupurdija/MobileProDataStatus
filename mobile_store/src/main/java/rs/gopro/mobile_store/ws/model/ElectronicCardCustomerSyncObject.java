package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class ElectronicCardCustomerSyncObject extends SyncObject {

	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.ECECTRONIC_CARD_CUS_SYNC_ACTION";
	public static String TAG = "ElectronicCardCustomerSyncObject";
	
	
	private String cSVString;
	private String customerNoa46;
	private String mItemNoa46;
	private String salespersonCode;
	
	
	public static final Creator<ElectronicCardCustomerSyncObject> CREATOR = new Creator<ElectronicCardCustomerSyncObject>() {

		@Override
		public ElectronicCardCustomerSyncObject createFromParcel(Parcel source) {
			return new ElectronicCardCustomerSyncObject(source);
		}

		@Override
		public ElectronicCardCustomerSyncObject[] newArray(int size) {
			return new ElectronicCardCustomerSyncObject[size];
		}

	};
	
	
	public ElectronicCardCustomerSyncObject(){
	super();	
	}
	
	public ElectronicCardCustomerSyncObject(Parcel parcel){
		super(parcel);
		setcSVString(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setmItemNoa46(parcel.readString());
		setSalespersonCode(parcel.readString());
	}
	
	
	
	
	public ElectronicCardCustomerSyncObject(String cSVString, String customerNoa46, String mItemNoa46, String salespersonCode) {
		super();
		this.cSVString = cSVString;
		this.customerNoa46 = customerNoa46;
		this.mItemNoa46 = mItemNoa46;
		this.salespersonCode = salespersonCode;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeString(getCustomerNoa46());
		dest.writeString(getmItemNoa46());
		dest.writeString(getSalespersonCode());

	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerItemSaleStatistics";
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

		PropertyInfo pItemNoa46 = new PropertyInfo();
		pItemNoa46.setName("pItemNoa46");
		pItemNoa46.setValue(mItemNoa46);
		pItemNoa46.setType(String.class);
		properties.add(pItemNoa46);
		
		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);
		
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
		List<ElectronicCardCustomerDomain> parsedDomains = CSVDomainReader.parse(new StringReader(soapResponse.toString()), ElectronicCardCustomerDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedDomains);
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.ElectronicCardCustomer.CONTENT_URI, valuesForInsert);
		return numOfInserted;
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

	public String getmItemNoa46() {
		return mItemNoa46;
	}

	public void setmItemNoa46(String mItemNoa46) {
		this.mItemNoa46 = mItemNoa46;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
