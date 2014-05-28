package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.NewSalesDocumentHeaderDomain;
import rs.gopro.mobile_store.ws.model.domain.NewSalesDocumentLinesDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class NewSalesDocumentsSyncObject extends SyncObject {

	public static String TAG = "NewSalesDocuSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.NEW_SALES_DOC_SYNC_ACTION";

	private String pCSVStringHeaders;
	private String pCSVStringLines;
	private Integer pDocumentType;
	private String pCustomerNo;
	private Date pDocumentDateFrom;
	private Date pDocumentDateTo;
	private String salespersonCode;
	private String[] documentNumbers = new String[]{}; 
	

	public static final Creator<NewSalesDocumentsSyncObject> CREATOR = new Creator<NewSalesDocumentsSyncObject>() {

		@Override
		public NewSalesDocumentsSyncObject createFromParcel(Parcel source) {
			return new NewSalesDocumentsSyncObject(source);
		}

		@Override
		public NewSalesDocumentsSyncObject[] newArray(int size) {
			return new NewSalesDocumentsSyncObject[size];
		}
	};

	public NewSalesDocumentsSyncObject() {
		super();
	}

	public NewSalesDocumentsSyncObject(Parcel parcel) {
		super(parcel);
		setpCSVStringHeaders(parcel.readString());
		setpCSVStringLines(parcel.readString());
		setSalespersonCode(parcel.readString());
		setpDocumentType(parcel.readInt());
		setpCustomerNo(parcel.readString());
		setDocumentDateFrom(new Date(parcel.readLong()));
		setDocumentDateTo(new Date(parcel.readLong()));
		//parcel.readStringArray(documentNumbers);
	}

	public NewSalesDocumentsSyncObject(Integer documentType, String customerNo, Date postingDateFrom, Date postingDateTo, String salespersonCode) {
		super();
		this.pCSVStringHeaders = "";
		this.pCSVStringLines = "";
		this.salespersonCode = salespersonCode;
		this.pDocumentType = documentType;
		this.pCustomerNo = customerNo;
		this.pDocumentDateFrom = postingDateFrom;
		this.pDocumentDateTo = postingDateTo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCSVStringHeaders());
		dest.writeString(getpCSVStringLines());
		dest.writeString(getSalespersonCode());
		dest.writeInt(getpDocumentType());
		dest.writeString(getpCustomerNo());
		dest.writeLong(getDocumentDateFrom().getTime());
		dest.writeLong(getDocumentDateTo().getTime());
		//dest.writeStringArray(getDocumentNumbers());
	}

	@Override
	public String getWebMethodName() {
		return "GetSalesDocuments";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVStringHeaders");
		cSVStringInfo.setValue(pCSVStringHeaders);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);
		
		PropertyInfo cSVStringLines = new PropertyInfo();
		cSVStringLines.setName("pCSVStringLines");
		cSVStringLines.setValue(pCSVStringLines);
		cSVStringLines.setType(String.class);
		properties.add(cSVStringLines);
		
		PropertyInfo salespersonCodeInfo = new PropertyInfo();
		salespersonCodeInfo.setName("pSalespersonCode");
		salespersonCodeInfo.setValue(salespersonCode);
		salespersonCodeInfo.setType(String.class);
		properties.add(salespersonCodeInfo);
		
		PropertyInfo documentTypeInfo = new PropertyInfo();
		documentTypeInfo.setName("pDocumentType");
		documentTypeInfo.setValue(pDocumentType);
		documentTypeInfo.setType(Integer.class);
		properties.add(documentTypeInfo);
		
		PropertyInfo customerNoInfo = new PropertyInfo();
		customerNoInfo.setName("pCustomerNo");
		customerNoInfo.setValue(pCustomerNo);
		customerNoInfo.setType(String.class);
		properties.add(customerNoInfo);
		
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

		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		List<NewSalesDocumentHeaderDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), NewSalesDocumentHeaderDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int numOfInserted = contentResolver.bulkInsert(SaleOrders.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		List<NewSalesDocumentHeaderDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVStringHeaders")), NewSalesDocumentHeaderDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int numOfInserted = contentResolver.bulkInsert(SaleOrders.CONTENT_URI, valuesForInsert);
		
		List<NewSalesDocumentLinesDomain> parsedLines = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVStringLines")), NewSalesDocumentLinesDomain.class);
		ContentValues[] valuesForInsertLines = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedLines);
		int numOfInsertedLines = contentResolver.bulkInsert(SaleOrderLines.CONTENT_URI, valuesForInsertLines);
		
		return numOfInserted + numOfInsertedLines;
	}
	
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	public String getpCSVStringHeaders() {
		return pCSVStringHeaders;
	}

	public void setpCSVStringHeaders(String cSVString) {
		this.pCSVStringHeaders = cSVString;
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

	public String getSalespersonCode() {
		return salespersonCode;
	}

	public void setSalespersonCode(String salespersonCode) {
		this.salespersonCode = salespersonCode;
	}

	public String[] getDocumentNumbers() {
		return documentNumbers;
	}

	public void setDocumentNumbers(String[] documentNumbers) {
		this.documentNumbers = documentNumbers;
	}

	public String getpCSVStringLines() {
		return pCSVStringLines;
	}

	public void setpCSVStringLines(String pCSVStringLines) {
		this.pCSVStringLines = pCSVStringLines;
	}

	public Integer getpDocumentType() {
		return pDocumentType;
	}

	public void setpDocumentType(Integer pDocumentType) {
		this.pDocumentType = pDocumentType;
	}

	public String getpCustomerNo() {
		return pCustomerNo;
	}

	public void setpCustomerNo(String pCustomerNo) {
		this.pCustomerNo = pCustomerNo;
	}

	public Date getpDocumentDateFrom() {
		return pDocumentDateFrom;
	}

	public void setpDocumentDateFrom(Date pDocumentDateFrom) {
		this.pDocumentDateFrom = pDocumentDateFrom;
	}

	public Date getpDocumentDateTo() {
		return pDocumentDateTo;
	}

	public void setpDocumentDateTo(Date pDocumentDateTo) {
		this.pDocumentDateTo = pDocumentDateTo;
	}

}
