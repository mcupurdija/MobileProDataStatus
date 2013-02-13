package rs.gopro.mobile_store.ws.model;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.csv.CSVDomainWriter;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import au.com.bytecode.opencsv.CSVWriter;

public class MobileDeviceSalesDocumentSyncObject extends SyncObject {

	public static String TAG = "MobileDeviceSalesDocumentSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.MOBILE_DEVICE_SALES_DOCUMENT_SYNC_ACTION";
	
	private int documentId;
	private String pCSVStringHeader;
	private String pCSVStringLines;
	private String pNoteForCentralOffice;
	private String pDocumentNote;
	
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
		setpCSVStringHeader(source.readString());
		setpCSVStringLines(source.readString());
		setpNoteForCentralOffice(source.readString());
		setpDocumentNote(source.readString());
	}
	
	public MobileDeviceSalesDocumentSyncObject(int document_id) {
		this.documentId = document_id;
	}
	
	public MobileDeviceSalesDocumentSyncObject(String pCSVStringHeader,
			String pCSVStringLines, String pNoteForCentralOffice,
			String pDocumentNote) {
		super();
		this.pCSVStringHeader = pCSVStringHeader;
		this.pCSVStringLines = pCSVStringLines;
		this.pNoteForCentralOffice = pNoteForCentralOffice;
		this.pDocumentNote = pDocumentNote;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeInt(getDocumentId());
		dest.writeString(getpCSVStringHeader());
		dest.writeString(getpCSVStringLines());
		dest.writeString(getpNoteForCentralOffice());
		dest.writeString(getpDocumentNote());
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
		
		return properies;
	}

	private String createHeader() {
		// get header data
		Cursor cursorHeader = context.getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderExport(), SalesOrderHeaderQuery.PROJECTION, Tables.SALE_ORDERS+"._ID=?", new String[] { String.valueOf(documentId) }, null);
		List<String[]> header = CSVDomainWriter.parseCursor(cursorHeader, SalesOrderHeaderQuery.PROJECTION_TYPE);
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, ';', '"');
		writer.writeAll(header);
		try {
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
		return stringWriter.toString();
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
		return stringWriter.toString();
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		String headerResp = soapResponse.getPropertyAsString("pCSVStringHeader");
		String linesResp = soapResponse.getPropertyAsString("pCSVStringLines");
		return 0;
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
                MobileStoreContract.SaleOrders.CONTACT_NAME,
                MobileStoreContract.SaleOrders.CONTACT_PHONE,
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
				
				Integer.class,
				Integer.class,
				
				Integer.class,
				Integer.class,
				Integer.class,
				Integer.class
		};

		int _ID = 0;
		int DOCUMENT_TYPE = 1;
		int SALES_ORDER_NO = 2;
		int CUSTOMER_N = 3;
		int LOCATION_CODE = 4;
		int SHORTCUT_DIMENSION_1_CODE = 5;
		int EXTERNAL_DOCUMENT_NO = 6;
		int QUOTE_NO = 7;
		int SALES_PERSON_ID = 8;
		int CUSTOMER_BUSINESS_UNIT_CODE = 9;
		int SELL_TO_ADDRESS_ID = 10;
		int SHIPP_TO_ADDRESS_ID = 11;
		int CUST_USES_TRANSIT_CUST = 12;
		int CONTACT_NAME = 13;
		int CONTACT_PHONE = 14;
		int HIDE_REBATE = 15;
		int FURTHER_SALE = 16;
		int PAYMENT_OPTION = 17;
		int BACKORDER_SHIPMENT_STATUS = 18;
		int QUOTE_REALIZED_STATUS = 19;
		int ORDER_CONDITION_STATUS = 20; 
	}
	
	private interface SalesOrderLineQuery {
		String[] PROJECTION = {
                //BaseColumns._ID,
                MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
                MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO,
                MobileStoreContract.SaleOrderLines.LINE_NO,
                MobileStoreContract.Items.ITEM_NO,
                MobileStoreContract.SaleOrderLines.QUANTITY,
                MobileStoreContract.SaleOrderLines.UNIT_SALES_PRICE_DIN,
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
		int SALES_ORDER_NO = 0;
		int LINE_NO = 1;
		int ITEM_NO = 2;
		int QUANTITY = 3;
		int UNIT_SALES_PRICE_DIN = 4;
		int REAL_DISCOUNT = 5;
		int CAMPAIGN_STATUS = 6;
		int QUOTE_REFUSED_STATUS = 7;
		int BACKORDER_STATUS = 8;
		int AVAILABLE_TO_WHOLE_SHIPMENT = 9; 
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
}
