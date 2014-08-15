package rs.gopro.mobile_store.ws.model;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsOnPromotion;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerItemOnPromotionDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class CustomerItemOnPromotionSyncObject extends SyncObject {

	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.CUSTOMER_ITEM_ON_PROMOTION_SYNC_OBJECT";
	public static String TAG = "CustomerItemOnPromotionSyncObject";

	private String pCSVString;
	private String pCustomerNoa46;
	private String pSalespersonCode;

	public static final Creator<CustomerItemOnPromotionSyncObject> CREATOR = new Creator<CustomerItemOnPromotionSyncObject>() {

		@Override
		public CustomerItemOnPromotionSyncObject createFromParcel(Parcel source) {
			return new CustomerItemOnPromotionSyncObject(source);
		}

		@Override
		public CustomerItemOnPromotionSyncObject[] newArray(int size) {
			return new CustomerItemOnPromotionSyncObject[size];
		}

	};

	public CustomerItemOnPromotionSyncObject() {
		super();
	}

	public CustomerItemOnPromotionSyncObject(String pCSVString,
			String pCustomerNoa46, String pSalespersonCode) {
		super();
		this.pCSVString = pCSVString;
		this.pCustomerNoa46 = pCustomerNoa46;
		this.pSalespersonCode = pSalespersonCode;
	}

	public CustomerItemOnPromotionSyncObject(Parcel parcel) {
		super(parcel);
		setpCSVString(parcel.readString());
		setpCustomerNoa46(parcel.readString());
		setpSalespersonCode(parcel.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCSVString());
		dest.writeString(getpCustomerNoa46());
		dest.writeString(getpSalespersonCode());
	}

	@Override
	public String getWebMethodName() {
		return "GetCustomerItemOnPromotion";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo pCSVStringInfo = new PropertyInfo();
		pCSVStringInfo.setName("pCSVString");
		pCSVStringInfo.setValue(pCSVString);
		pCSVStringInfo.setType(String.class);
		properties.add(pCSVStringInfo);
		
		PropertyInfo pCustomerNoa46Info = new PropertyInfo();
		pCustomerNoa46Info.setName("pCustomerNoa46");
		pCustomerNoa46Info.setValue(pCustomerNoa46);
		pCustomerNoa46Info.setType(String.class);
		properties.add(pCustomerNoa46Info);
		
		PropertyInfo pSalespersonCodeInfo = new PropertyInfo();
		pSalespersonCodeInfo.setName("pSalespersonCode");
		pSalespersonCodeInfo.setValue(pSalespersonCode);
		pSalespersonCodeInfo.setType(String.class);
		properties.add(pSalespersonCodeInfo);
		
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
		
		contentResolver.delete(ItemsOnPromotion.CONTENT_URI, null, null);
		
		List<CustomerItemOnPromotionDomain> parsedDomains = CSVDomainReader.parse(new StringReader(soapResponse.toString()), CustomerItemOnPromotionDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedDomains);
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.ItemsOnPromotion.CONTENT_URI, valuesForInsert);
		return numOfInserted;
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

	public String getpCustomerNoa46() {
		return pCustomerNoa46;
	}

	public void setpCustomerNoa46(String pCustomerNoa46) {
		this.pCustomerNoa46 = pCustomerNoa46;
	}

	public String getpSalespersonCode() {
		return pSalespersonCode;
	}

	public void setpSalespersonCode(String pSalespersonCode) {
		this.pSalespersonCode = pSalespersonCode;
	}

}
