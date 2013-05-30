package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatus;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatusLines;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.SalesHeadersDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class SalesHeadersSyncObject extends SyncObject {

	public static String TAG = "SalesHeadersSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SALES_HEADERS_SYNC_ACTION";

	private String cSVString;
	private Integer documentType;
	private String documentNoa46;
	private String customerNoa46;
	private Date pDocumentDateFrom;
	private Date pDocumentDateTo;
	private Integer blocked;
	private String salespersonCode;
	

	public static final Creator<SalesHeadersSyncObject> CREATOR = new Creator<SalesHeadersSyncObject>() {

		@Override
		public SalesHeadersSyncObject createFromParcel(Parcel source) {
			return new SalesHeadersSyncObject(source);
		}

		@Override
		public SalesHeadersSyncObject[] newArray(int size) {
			return new SalesHeadersSyncObject[size];
		}
	};

	public SalesHeadersSyncObject() {
		super();
	}

	public SalesHeadersSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setDocumentType(parcel.readInt());
		setDocumentNoa46(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setDocumentDateFrom(new Date(parcel.readLong()));
		setDocumentDateTo(new Date(parcel.readLong()));
		setSalespersonCode(parcel.readString());
		setBlocked(parcel.readInt());
	}

	public SalesHeadersSyncObject(String cSVString, Integer documentType, String documentNoa46, String customerNoa46, Date postingDateFrom, Date postingDateTo, String salespersonCode, Integer open) {
		super();
		this.cSVString = cSVString;
		this.documentType = documentType;
		this.documentNoa46 = documentNoa46;
		this.customerNoa46 = customerNoa46;
		this.pDocumentDateFrom = postingDateFrom;
		this.pDocumentDateTo = postingDateTo;
		this.salespersonCode = salespersonCode;
		this.blocked = open;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeInt(getDocumentType());
		dest.writeString(getDocumentNoa46());
		dest.writeString(getCustomerNoa46());
		dest.writeLong(getDocumentDateFrom().getTime());
		dest.writeLong(getDocumentDateTo().getTime());
		dest.writeString(getSalespersonCode());
		dest.writeInt(getBlocked());
	}

	@Override
	public String getWebMethodName() {
		return "GetSalesHeaders";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		PropertyInfo documentTypeInfo = new PropertyInfo();
		documentTypeInfo.setName("pDocumentType");
		documentTypeInfo.setValue(documentType);
		documentTypeInfo.setType(Integer.class);
		properties.add(documentTypeInfo);

		PropertyInfo documentNoa46Info = new PropertyInfo();
		documentNoa46Info.setName("pDocumentNoa46");
		documentNoa46Info.setValue(documentNoa46);
		documentNoa46Info.setType(String.class);
		properties.add(documentNoa46Info);

		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(customerNoa46);
		customerNoa46Info.setType(String.class);
		properties.add(customerNoa46Info);

		PropertyInfo postingDateFromInfo = new PropertyInfo();
		postingDateFromInfo.setName("pDocumentDateFrom");
		postingDateFromInfo.setValue(pDocumentDateFrom);
		postingDateFromInfo.setType(Date.class);
		properties.add(postingDateFromInfo);

		PropertyInfo postingDateToInfo = new PropertyInfo();
		postingDateToInfo.setName("pDocumentDateTo");
		postingDateToInfo.setValue(pDocumentDateTo);
		postingDateToInfo.setType(Date.class);
		properties.add(postingDateToInfo);

		PropertyInfo openInfo = new PropertyInfo();
		openInfo.setName("pGetBlockedOnly");
		openInfo.setValue(blocked);
		openInfo.setType(Integer.class);
		properties.add(openInfo);
		
		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);

		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		List<SalesHeadersDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), SalesHeadersDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		//valuesForInsert.put(SalesPerson.SALE_PERSON_NO, salespersonCode);
		int del_res = 0;
		if (valuesForInsert.length > 0) {
			del_res = contentResolver.delete(SentOrdersStatus.CONTENT_URI, null, null);
			LogUtils.LOGD(TAG, "Deleted pre bulk insert rows:"+del_res);
		}
		int numOfInserted = contentResolver.bulkInsert(SentOrdersStatus.CONTENT_URI, valuesForInsert);
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

	public String getCustomerNoa46() {
		return customerNoa46;
	}

	public void setCustomerNoa46(String customerNoa46) {
		this.customerNoa46 = customerNoa46;
	}

	public Date getDocumentDateFrom() {
		return pDocumentDateFrom;
	}

	public void setDocumentDateFrom(Date postingDateFrom) {
		this.pDocumentDateFrom = postingDateFrom;
	}

	public Date getDocumentDateTo() {
		return pDocumentDateTo;
	}

	public void setDocumentDateTo(Date postingDateTo) {
		this.pDocumentDateTo = postingDateTo;
	}

	public Integer getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Integer documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNoa46() {
		return documentNoa46;
	}

	public void setDocumentNoa46(String documentNoa46) {
		this.documentNoa46 = documentNoa46;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public Integer getBlocked() {
		return blocked;
	}

	public void setBlocked(Integer blocked) {
		this.blocked = blocked;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

}
