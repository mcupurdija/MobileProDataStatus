package rs.gopro.mobile_store.ws.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.csv.CSVDomainWriter;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.model.domain.MobileDeviceSalesDocumentHeaderDomain;
import rs.gopro.mobile_store.ws.model.domain.MobileDeviceSalesDocumentLinesDomain;
import rs.gopro.mobile_store.ws.model.domain.TransformDomainObject;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import au.com.bytecode.opencsv.CSVWriter;

public class MobileDeviceSalesDocumentSyncObject extends SyncObject {

	public static String TAG = "MobileDeviceSalesDocumentSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.MOBILE_DEVICE_SALES_DOCUMENT_SYNC_ACTION";
	
	private int documentId;
	private int potentialCustomer = 0;
	private String pCSVStringHeader;
	private String pCSVStringLines;
	private String pNoteForCentralOffice;
	private String pDocumentNote;
	private Integer pVerifyOnly;
	
	// not on request/response just for status return
	private String order_condition_status;	
	private String financial_control_status;
	private String order_status_for_shipment;
	private String order_value_status;
	private String min_max_discount_total_amount_difference;
	
	public static final Creator<MobileDeviceSalesDocumentSyncObject> CREATOR = new Creator<MobileDeviceSalesDocumentSyncObject>() {

		@Override
		public MobileDeviceSalesDocumentSyncObject createFromParcel(Parcel source) {
			return new MobileDeviceSalesDocumentSyncObject(source);
		}

		@Override
		public MobileDeviceSalesDocumentSyncObject[] newArray(int size) {
			return new MobileDeviceSalesDocumentSyncObject[size];
		}

	};
	
	public MobileDeviceSalesDocumentSyncObject() {
		super();
	}

	public MobileDeviceSalesDocumentSyncObject(Parcel source) {
		super(source);
		setDocumentId(source.readInt());
		setPotentialCustomer(source.readInt());
		setpCSVStringHeader(source.readString());
		setpCSVStringLines(source.readString());
		setpNoteForCentralOffice(source.readString());
		setpDocumentNote(source.readString());
		setpVerifyOnly(source.readInt());
		
		setOrder_condition_status(source.readString());
		setFinancial_control_status(source.readString());
		setOrder_status_for_shipment(source.readString());
		setOrder_value_status(source.readString());
		setMin_max_discount_total_amount_difference(source.readString());
	}
	
	public MobileDeviceSalesDocumentSyncObject(int document_id, Integer pVerifyOnly) {
		this.documentId = document_id;
		this.pVerifyOnly = pVerifyOnly;
	}
	
	public MobileDeviceSalesDocumentSyncObject(String pCSVStringHeader,
			String pCSVStringLines, String pNoteForCentralOffice,
			String pDocumentNote, Integer pVerifyOnly) {
		super();
		this.pCSVStringHeader = pCSVStringHeader;
		this.pCSVStringLines = pCSVStringLines;
		this.pNoteForCentralOffice = pNoteForCentralOffice;
		this.pDocumentNote = pDocumentNote;
		this.pVerifyOnly = pVerifyOnly;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeInt(getDocumentId());
		dest.writeInt(isPotentialCustomer());
		dest.writeString(getpCSVStringHeader());
		dest.writeString(getpCSVStringLines());
		dest.writeString(getpNoteForCentralOffice());
		dest.writeString(getpDocumentNote());
		dest.writeInt(getpVerifyOnly());
		
		dest.writeString(getOrder_condition_status());
		dest.writeString(getFinancial_control_status());
		dest.writeString(getOrder_status_for_shipment());
		dest.writeString(getOrder_value_status());
		dest.writeString(getMin_max_discount_total_amount_difference());
	}

	@Override
	public String getWebMethodName() {
		return "SetMobileDeviceSalesDocument";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		setpCSVStringHeader(createHeader());
		PropertyInfo cSVStringHeader = new PropertyInfo();
		cSVStringHeader.setName("pCSVStringHeader");
		cSVStringHeader.setValue(pCSVStringHeader);
		cSVStringHeader.setType(String.class);
		properies.add(cSVStringHeader);
		
		setpCSVStringLines(createLines());
		PropertyInfo cSVStringLines = new PropertyInfo();
		cSVStringLines.setName("pCSVStringLines");
		cSVStringLines.setValue(pCSVStringLines);
		cSVStringLines.setType(String.class);
		properies.add(cSVStringLines);
		
		createDocumentNotes();
		
		PropertyInfo noteForCentralOffice = new PropertyInfo();
		noteForCentralOffice.setName("pNoteForCentralOffice");
		noteForCentralOffice.setValue(pNoteForCentralOffice);
		noteForCentralOffice.setType(String.class);
		properies.add(noteForCentralOffice);
		
		PropertyInfo documentNote = new PropertyInfo();
		documentNote.setName("pDocumentNote");
		documentNote.setValue(pDocumentNote);
		documentNote.setType(String.class);
		properies.add(documentNote);
		
		PropertyInfo verifyOnly = new PropertyInfo();
		verifyOnly.setName("pVerifyOnly");
		verifyOnly.setValue(pVerifyOnly);
		verifyOnly.setType(Integer.class);
		properies.add(verifyOnly);
		
		return properies;
	}

	private String createHeader() {
		// get header data
		Cursor cursorHeader = context.getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderExport(), SalesOrderHeaderQuery.PROJECTION, Tables.SALE_ORDERS+"._ID=?", new String[] { String.valueOf(documentId) }, null);
		List<String[]> header = CSVDomainWriter.parseCursor(cursorHeader, SalesOrderHeaderQuery.PROJECTION_TYPE);
		if (header.size() > 0) {
			String[] headerLine = header.get(0);
			int document_type_pos = 0;
			int customer_no_pos = 2;
			int contact_no_pos = 11;
			// it is document type ponuda and customer is potential customer
			if (headerLine[document_type_pos].equals("1") && isPotentialCustomer(headerLine[customer_no_pos])) {
				headerLine[contact_no_pos] = headerLine[customer_no_pos];
				headerLine[customer_no_pos] = "";
			}
		} else {
			LogUtils.LOGE(TAG, "Sale document header is empty!!!");
		}
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, ';', '"');
		writer.writeAll(header);
		try {
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
		cursorHeader.close();
		return stringWriter.toString();
	}
	
	private boolean isPotentialCustomer(String customerNo) {
		Cursor potentialCustomerCursor = context.getContentResolver().query(MobileStoreContract.Customers.CONTENT_URI, new String[] {Customers.CONTACT_COMPANY_NO}, 
				"("+Customers.CONTACT_COMPANY_NO + " is null or " + Customers.CONTACT_COMPANY_NO + "='')" + " and " + Customers.CUSTOMER_NO + "=?" , new String[] { customerNo }, null);
		if (potentialCustomerCursor.moveToFirst()) {
			return true;
		}
		potentialCustomerCursor.close();
		return false;
	}
	
	private String createLines() {
		// get lines data
		Cursor cursorLines = context.getContentResolver().query(MobileStoreContract.SaleOrderLines.buildSaleOrderLineExportUri(), SalesOrderLineQuery.PROJECTION, Tables.SALE_ORDER_LINES+"."+MobileStoreContract.SaleOrderLines.SALE_ORDER_ID+"=?", new String[] { String.valueOf(documentId) }, Tables.SALE_ORDER_LINES + "." + MobileStoreContract.SaleOrderLines.LINE_NO + " ASC");
		List<String[]> lines = CSVDomainWriter.parseCursor(cursorLines, SalesOrderLineQuery.PROJECTION_TYPE);
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, ';', '"');
		writer.writeAll(lines);
		try {
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
		cursorLines.close();
		return stringWriter.toString();
	}

	private void createDocumentNotes() {
		Cursor cursorNotes = context.getContentResolver().query(MobileStoreContract.SaleOrders.CONTENT_URI, new String[] { SaleOrders.NOTE1, SaleOrders.NOTE2 }, Tables.SALE_ORDERS+"._ID=?", new String[] { String.valueOf(documentId) }, null);
		pDocumentNote = ""; pNoteForCentralOffice = "";
		if (cursorNotes != null && cursorNotes.moveToFirst()) {
			if (!cursorNotes.isNull(0)) {
				pDocumentNote = cursorNotes.getString(0);
			}
			if (!cursorNotes.isNull(1)) {
				pNoteForCentralOffice = cursorNotes.getString(1);
			}
		}
		cursorNotes.close();
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
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
//		String headerResp = soapResponse.getPropertyAsString("pCSVStringHeader");
//		String linesResp = soapResponse.getPropertyAsString("pCSVStringLines");
		
		if (soapResponse == null || soapResponse.toString().equals("") || soapResponse.getPropertyAsString("pCSVStringHeader") == null || soapResponse.getPropertyAsString("pCSVStringHeader").equals("") || soapResponse.getPropertyAsString("pCSVStringHeader").equals("anyType{}")) {
			// when status is ponuda or spec ponuda an response is empty everything is OK
			
			// now we have response
			return 1;
		}
		
		List<MobileDeviceSalesDocumentHeaderDomain> parsedSalesHeader = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVStringHeader")), MobileDeviceSalesDocumentHeaderDomain.class);
		ContentValues[] valuesForInsertHeader = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedSalesHeader);
		
		// verification, there is no returned sale order no
		if (pVerifyOnly == 1) {
			valuesForInsertHeader[0].putNull("sales_order_no");
		}
		
		List<MobileDeviceSalesDocumentLinesDomain> parsedSalesLines = CSVDomainReader.parse(new StringReader(soapResponse.getPropertyAsString("pCSVStringLines")), MobileDeviceSalesDocumentLinesDomain.class);
		ContentValues[] valuesForInsertLines = TransformDomainObject.newInstance().transformDomainToContentValues(contentResolver, parsedSalesLines);
		
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.SaleOrders.CONTENT_URI, valuesForInsertHeader);
		numOfInserted = contentResolver.bulkInsert(MobileStoreContract.SaleOrderLines.CONTENT_URI, valuesForInsertLines);
		
		// return statuses
		MobileDeviceSalesDocumentHeaderDomain deviceSalesDocumentHeaderDomain = parsedSalesHeader.get(0);
		setOrder_condition_status(deviceSalesDocumentHeaderDomain.order_condition_status);
		setFinancial_control_status(deviceSalesDocumentHeaderDomain.financial_control_status);
		setOrder_status_for_shipment(deviceSalesDocumentHeaderDomain.order_status_for_shipment);
		setOrder_value_status(deviceSalesDocumentHeaderDomain.order_value_status);
		setMin_max_discount_total_amount_difference(deviceSalesDocumentHeaderDomain.min_max_discount_total_amount_difference);
		
		return numOfInserted;
	}

	public String getpCSVStringHeader() {
		return pCSVStringHeader;
	}

	public void setpCSVStringHeader(String pCSVStringHeader) {
		this.pCSVStringHeader = pCSVStringHeader;
	}

	public String getpCSVStringLines() {
		return pCSVStringLines;
	}

	public void setpCSVStringLines(String pCSVStringLines) {
		this.pCSVStringLines = pCSVStringLines;
	}

	public String getpNoteForCentralOffice() {
		return pNoteForCentralOffice;
	}

	public void setpNoteForCentralOffice(String pNoteForCentralOffice) {
		this.pNoteForCentralOffice = pNoteForCentralOffice;
	}

	public String getpDocumentNote() {
		return pDocumentNote;
	}

	public void setpDocumentNote(String pDocumentNote) {
		this.pDocumentNote = pDocumentNote;
	}

	
	private interface SalesOrderHeaderQuery {
		String[] PROJECTION = {
                //BaseColumns._ID,
                MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
                MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO,
                MobileStoreContract.Customers.CUSTOMER_NO,
                MobileStoreContract.SaleOrders.LOCATION_CODE,
                MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE,
                MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO,
                MobileStoreContract.SaleOrders.QUOTE_NO,
                MobileStoreContract.SalesPerson.SALE_PERSON_NO,
                "sell_to_address_no",
                "shipp_to_address_no",
                MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST,
                MobileStoreContract.Contacts.CONTACT_NO,
                MobileStoreContract.SaleOrders.CONTACT_NAME,
                MobileStoreContract.SaleOrders.CONTACT_PHONE,
                MobileStoreContract.SaleOrders.CONTACT_EMAIL,
                
                MobileStoreContract.SaleOrders.HIDE_REBATE,
                MobileStoreContract.SaleOrders.FURTHER_SALE,
                
                MobileStoreContract.SaleOrders.PAYMENT_OPTION,
                MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS,
                MobileStoreContract.SaleOrders.QUOTE_REALIZED_STATUS,
                MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS
        };
		
		Type[] PROJECTION_TYPE = {
				Integer.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				
				String.class,
				String.class,
				
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				
				Integer.class,
				Integer.class,
				
				String.class,
				Integer.class,
				Integer.class,
				Integer.class
		};

//		int _ID = 0;
//		int DOCUMENT_TYPE = 1;
//		int SALES_ORDER_NO = 2;
//		int CUSTOMER_N = 3;
//		int LOCATION_CODE = 4;
//		int SHORTCUT_DIMENSION_1_CODE = 5;
//		int EXTERNAL_DOCUMENT_NO = 6;
//		int QUOTE_NO = 7;
//		int SALES_PERSON_ID = 8;
//		int CUSTOMER_BUSINESS_UNIT_CODE = 9;
//		int SELL_TO_ADDRESS_ID = 10;
//		int SHIPP_TO_ADDRESS_ID = 11;
//		int CUST_USES_TRANSIT_CUST = 12;
//		int CONTACT_NAME = 13;
//		int CONTACT_PHONE = 14;
//		int HIDE_REBATE = 15;
//		int FURTHER_SALE = 16;
//		int PAYMENT_OPTION = 17;
//		int BACKORDER_SHIPMENT_STATUS = 18;
//		int QUOTE_REALIZED_STATUS = 19;
//		int ORDER_CONDITION_STATUS = 20; 
	}
	
	private interface SalesOrderLineQuery {
		String[] PROJECTION = {
                //BaseColumns._ID,
                MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
                MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO,
                MobileStoreContract.SaleOrderLines.LINE_NO,
                MobileStoreContract.Items.ITEM_NO,
                MobileStoreContract.SaleOrderLines.QUANTITY,
                MobileStoreContract.SaleOrderLines.PRICE,
                MobileStoreContract.SaleOrderLines.REAL_DISCOUNT,
                MobileStoreContract.SaleOrderLines.CAMPAIGN_STATUS,
                MobileStoreContract.SaleOrderLines.QUOTE_REFUSED_STATUS,
                MobileStoreContract.SaleOrderLines.BACKORDER_STATUS,
                MobileStoreContract.SaleOrderLines.AVAILABLE_TO_WHOLE_SHIPMENT
        };

		Type[] PROJECTION_TYPE = {
				Integer.class,
				String.class,
				Integer.class,
				String.class,
				Double.class,
				Double.class,
				Double.class,
				Integer.class,
				Integer.class,
				Integer.class,
				Integer.class
		};
		
		//int _ID = 0;
		//int DOCUMENT_TYPE = 1;
//		int SALES_ORDER_NO = 0;
//		int LINE_NO = 1;
//		int ITEM_NO = 2;
//		int QUANTITY = 3;
//		int UNIT_SALES_PRICE_DIN = 4;
//		int REAL_DISCOUNT = 5;
//		int CAMPAIGN_STATUS = 6;
//		int QUOTE_REFUSED_STATUS = 7;
//		int BACKORDER_STATUS = 8;
//		int AVAILABLE_TO_WHOLE_SHIPMENT = 9; 
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public String getOrder_condition_status() {
		if (order_condition_status == null || order_condition_status.length() < 1) {
			return "0";
		}
		return order_condition_status;
	}

	public void setOrder_condition_status(String order_condition_status) {
		this.order_condition_status = order_condition_status;
	}

	public String getFinancial_control_status() {
		if (financial_control_status == null || financial_control_status.length() < 1) {
			return "0";
		}
		return financial_control_status;
	}

	public void setFinancial_control_status(String financial_control_status) {
		this.financial_control_status = financial_control_status;
	}

	public String getOrder_status_for_shipment() {
		if (order_status_for_shipment == null || order_status_for_shipment.length() < 1) {
			return "0";
		}
		return order_status_for_shipment;
	}

	public void setOrder_status_for_shipment(String order_status_for_shipment) {
		this.order_status_for_shipment = order_status_for_shipment;
	}

	public String getOrder_value_status() {
		if (order_value_status == null || order_value_status.length() < 1) {
			return "0";
		}
		return order_value_status;
	}

	public void setOrder_value_status(String order_value_status) {
		this.order_value_status = order_value_status;
	}

	public Integer getpVerifyOnly() {
		return pVerifyOnly;
	}

	public void setpVerifyOnly(Integer pVerifyOnly) {
		this.pVerifyOnly = pVerifyOnly;
	}

	public int isPotentialCustomer() {
		return potentialCustomer;
	}

	public void setPotentialCustomer(int potentialCustomer) {
		this.potentialCustomer = potentialCustomer;
	}

	public String getMin_max_discount_total_amount_difference() {
		if (min_max_discount_total_amount_difference == null || min_max_discount_total_amount_difference.length() < 1) {
			return "0";
		}
		return min_max_discount_total_amount_difference;
	}

	public void setMin_max_discount_total_amount_difference(
			String min_max_discount_total_amount_difference) {
		this.min_max_discount_total_amount_difference = min_max_discount_total_amount_difference;
	}
}
