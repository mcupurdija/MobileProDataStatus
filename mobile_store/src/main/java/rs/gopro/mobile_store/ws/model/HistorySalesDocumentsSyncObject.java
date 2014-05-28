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
import rs.gopro.mobile_store.ws.model.domain.HistoryMobileDeviceSalesDocumentHeaderDomain;
import rs.gopro.mobile_store.ws.model.domain.HistoryMobileDeviceSalesDocumentLinesDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class HistorySalesDocumentsSyncObject extends SyncObject {

	public static String TAG = "HistorySalesDocuSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.HISTORY_SALES_DOC_SYNC_ACTION";

	private String pCSVStringHeaders;
	private String pCSVStringLines;
	private Date pDocumentDateFrom;
	private Date pDocumentDateTo;
	private String salespersonCode;
	private String[] documentNumbers = new String[]{}; 
	

	public static final Creator<HistorySalesDocumentsSyncObject> CREATOR = new Creator<HistorySalesDocumentsSyncObject>() {

		@Override
		public HistorySalesDocumentsSyncObject createFromParcel(Parcel source) {
			return new HistorySalesDocumentsSyncObject(source);
		}

		@Override
		public HistorySalesDocumentsSyncObject[] newArray(int size) {
			return new HistorySalesDocumentsSyncObject[size];
		}
	};

	public HistorySalesDocumentsSyncObject() {
		super();
	}

	public HistorySalesDocumentsSyncObject(Parcel parcel) {
		super(parcel);
		setpCSVStringHeaders(parcel.readString());
		setpCSVStringLines(parcel.readString());
		setSalespersonCode(parcel.readString());
		setDocumentDateFrom(new Date(parcel.readLong()));
		setDocumentDateTo(new Date(parcel.readLong()));
		parcel.readStringArray(documentNumbers);
	}

	public HistorySalesDocumentsSyncObject(Date postingDateFrom, Date postingDateTo, String salespersonCode) {
		super();
		this.pCSVStringHeaders = "";
		this.pCSVStringLines = "";
		this.salespersonCode = salespersonCode;
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
		dest.writeLong(getDocumentDateFrom().getTime());
		dest.writeLong(getDocumentDateTo().getTime());
		dest.writeStringArray(getDocumentNumbers());
	}

	@Override
	public String getWebMethodName() {
		return "GetMobileDeviceSalesDocuments";
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
		List<HistoryMobileDeviceSalesDocumentHeaderDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.toString()), HistoryMobileDeviceSalesDocumentHeaderDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int numOfInserted = contentResolver.bulkInsert(SaleOrders.CONTENT_URI, valuesForInsert);
		return numOfInserted;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		List<HistoryMobileDeviceSalesDocumentHeaderDomain> parsedItems = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVStringHeaders")), HistoryMobileDeviceSalesDocumentHeaderDomain.class);
		ContentValues[] valuesForInsert = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedItems);
		int numOfInserted = contentResolver.bulkInsert(SaleOrders.CONTENT_URI, valuesForInsert);
		
		List<HistoryMobileDeviceSalesDocumentLinesDomain> parsedLines = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVStringLines")), HistoryMobileDeviceSalesDocumentLinesDomain.class);
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

}
