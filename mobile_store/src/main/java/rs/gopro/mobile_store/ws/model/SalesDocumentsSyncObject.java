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
import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;
import rs.gopro.mobile_store.ws.model.domain.CustomerDomain;
import rs.gopro.mobile_store.ws.model.domain.SalesDocumentsDomain;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class SalesDocumentsSyncObject extends SyncObject {

	public static String TAG = "SalesDocumentsSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SALES_DOCUMENTS_SYNC_ACTION";

	private String cSVString;
	private Integer documentType;
	private String documentNoa46;
	private String customerNoa46;
	private Date postingDateFrom;
	private Date postingDateTo;
	private Date dueDate;
	private String salespersonCode;
	private Integer open;

	public static final Creator<SalesDocumentsSyncObject> CREATOR = new Creator<SalesDocumentsSyncObject>() {

		@Override
		public SalesDocumentsSyncObject createFromParcel(Parcel source) {
			return new SalesDocumentsSyncObject(source);
		}

		@Override
		public SalesDocumentsSyncObject[] newArray(int size) {
			return new SalesDocumentsSyncObject[size];
		}
	};

	public SalesDocumentsSyncObject() {
		super();
	}

	public SalesDocumentsSyncObject(Parcel parcel) {
		super(parcel);
		setcSVString(parcel.readString());
		setDocumentType(parcel.readInt());
		setDocumentNoa46(parcel.readString());
		setCustomerNoa46(parcel.readString());
		setPostingDateFrom(new Date(parcel.readLong()));
		setPostingDateTo(new Date(parcel.readLong()));
		setDueDate(new Date(parcel.readLong()));
		setSalespersonCode(parcel.readString());
		setOpen(parcel.readInt());
	}

	public SalesDocumentsSyncObject(String cSVString, Integer documentType, String documentNoa46, String customerNoa46, Date postingDateFrom, Date postingDateTo, Date dueDate, String salespersonCode, Integer open) {
		super();
		this.cSVString = cSVString;
		this.documentType = documentType;
		this.documentNoa46 = documentNoa46;
		this.customerNoa46 = customerNoa46;
		this.postingDateFrom = postingDateFrom;
		this.postingDateTo = postingDateTo;
		this.dueDate = dueDate;
		this.salespersonCode = salespersonCode;
		this.open = open;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getcSVString());
		dest.writeInt(getDocumentType());
		dest.writeString(getDocumentNoa46());
		dest.writeString(getCustomerNoa46());
		dest.writeLong(getPostingDateFrom().getTime());
		dest.writeLong(getPostingDateTo().getTime());
		dest.writeLong(getDueDate().getTime());
		dest.writeString(getSalespersonCode());
		dest.writeInt(getOpen());
	}

	@Override
	public String getWebMethodName() {
		return "GetSalesDocuments";
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
		postingDateFromInfo.setName("pPostingDateFrom");
		postingDateFromInfo.setValue(postingDateFrom);
		postingDateFromInfo.setType(Date.class);
		properties.add(postingDateFromInfo);

		PropertyInfo postingDateToInfo = new PropertyInfo();
		postingDateToInfo.setName("pPostingDateTo");
		postingDateToInfo.setValue(postingDateTo);
		postingDateToInfo.setType(Date.class);
		properties.add(postingDateToInfo);

		PropertyInfo dueDateInfo = new PropertyInfo();
		dueDateInfo.setName("pDueDate");
		dueDateInfo.setValue(dueDate);
		dueDateInfo.setType(Date.class);
		properties.add(dueDateInfo);

		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);

		PropertyInfo openInfo = new PropertyInfo();
		openInfo.setName("pOpen");
		openInfo.setValue(open);
		openInfo.setType(Integer.class);
		properties.add(openInfo);

		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		List<SalesDocumentsDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), SalesDocumentsDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.Invoices.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}

	@Override
	public String getTag() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		// TODO Auto-generated method stub
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

	public Date getPostingDateFrom() {
		return postingDateFrom;
	}

	public void setPostingDateFrom(Date postingDateFrom) {
		this.postingDateFrom = postingDateFrom;
	}

	public Date getPostingDateTo() {
		return postingDateTo;
	}

	public void setPostingDateTo(Date postingDateTo) {
		this.postingDateTo = postingDateTo;
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

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public Integer getOpen() {
		return open;
	}

	public void setOpen(Integer open) {
		this.open = open;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

}
