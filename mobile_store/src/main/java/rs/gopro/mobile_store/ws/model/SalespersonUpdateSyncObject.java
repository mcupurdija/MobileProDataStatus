package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SalesPerson;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.SalespersonSetupDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class SalespersonUpdateSyncObject extends SyncObject {

	public static String TAG = "SalespersonSetupSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SALESPERSON_SETUP_SYNC_ACTION";
	
	public String pCSVString;
	public String pSalespersonCode;
	
	public static final Creator<SalespersonUpdateSyncObject> CREATOR = new Creator<SalespersonUpdateSyncObject>() {

		@Override
		public SalespersonUpdateSyncObject createFromParcel(Parcel source) {
			return new SalespersonUpdateSyncObject(source);
		}

		@Override
		public SalespersonUpdateSyncObject[] newArray(int size) {
			return new SalespersonUpdateSyncObject[size];
		}
	};
	
	
	
	public SalespersonUpdateSyncObject() {
		super();
	}
	
	public SalespersonUpdateSyncObject(String salesPerson) {
		super();
		this.setpSalespersonCode(salesPerson);
	}
	
	public SalespersonUpdateSyncObject(Parcel parcel) {
		super(parcel);
		setpCSVString(parcel.readString());
		setpSalespersonCode(parcel.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCSVString());
		dest.writeString(getpSalespersonCode());
	}

	@Override
	public String getWebMethodName() {
		return "GetSalespersonSetup";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		PropertyInfo cSVString = new PropertyInfo();
		cSVString.setName("pCSVString");
		cSVString.setValue(pCSVString);
		cSVString.setType(String.class);
		properies.add(cSVString);
		
		PropertyInfo salespersonCode = new PropertyInfo();
		salespersonCode.setName("pSalespersonCode");
		salespersonCode.setValue(pSalespersonCode);
		salespersonCode.setType(String.class);
		properies.add(salespersonCode);
		
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
		List<SalespersonSetupDomain> parsedSalesLines = CSVDomainReader.parse(new StringReader(soapResponse.toString()), SalespersonSetupDomain.class);
		//parsedSalesLines.get(0).getContentValues().put(SalesPerson.SALE_PERSON_NO, pSalespersonCode);
		ContentValues[] valuesForInsertLines = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedSalesLines);
		valuesForInsertLines[0].put(SalesPerson.SALE_PERSON_NO, pSalespersonCode.toUpperCase());
		
		int status = contentResolver.update(MobileStoreContract.SalesPerson.CONTENT_URI, valuesForInsertLines[0], SalesPerson.SALE_PERSON_NO + "=?", new String[] { valuesForInsertLines[0].getAsString(SalesPerson.SALE_PERSON_NO) });
		return status;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		
		List<SalespersonSetupDomain> parsedSalesLines = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVString")), SalespersonSetupDomain.class);
		ContentValues[] valuesForInsertLines = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedSalesLines);
		valuesForInsertLines[0].put(SalesPerson.SALE_PERSON_NO, pSalespersonCode.toUpperCase());
		
		int status = contentResolver.update(MobileStoreContract.SalesPerson.CONTENT_URI, valuesForInsertLines[0], SalesPerson.SALE_PERSON_NO + "=?", new String[] { valuesForInsertLines[0].getAsString(SalesPerson.SALE_PERSON_NO) });
		return status;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	public String getpSalespersonCode() {
		return pSalespersonCode;
	}

	public void setpSalespersonCode(String pSalespersonCode) {
		this.pSalespersonCode = pSalespersonCode;
	}

}
