package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import android.provider.BaseColumns;

public class MobileDeviceSalesDocumentSyncObject extends SyncObject {

	public static String TAG = "MobileDeviceSalesDocumentSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.MOBILE_DEVICE_SALES_DOCUMENT_SYNC_ACTION";
	
	private int documentId;
	private String pCSVStringHeader;
	private String pCSVStringLines;
	private Integer pNoteForCentralOffice;
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
		setpNoteForCentralOffice(source.readInt());
		setpDocumentNote(source.readString());
	}
	
	public MobileDeviceSalesDocumentSyncObject(int document_id) {
		this.documentId = document_id;
	}
	
	public MobileDeviceSalesDocumentSyncObject(String pCSVStringHeader,
			String pCSVStringLines, Integer pNoteForCentralOffice,
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
		dest.writeInt(getpNoteForCentralOffice());
		dest.writeString(getpDocumentNote());
	}

	@Override
	public String getWebMethodName() {
		return "SetMobileDeviceSalesDocument";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		PropertyInfo cSVStringHeader = new PropertyInfo();
		cSVStringHeader.setName("pCSVStringHeader");
		cSVStringHeader.setValue(createHeader());
		cSVStringHeader.setType(String.class);
		properies.add(cSVStringHeader);
		
		PropertyInfo cSVStringLines = new PropertyInfo();
		cSVStringLines.setName("pCSVStringLines");
		cSVStringLines.setValue(createLines());
		cSVStringLines.setType(String.class);
		properies.add(cSVStringLines);
		
		PropertyInfo noteForCentralOffice = new PropertyInfo();
		noteForCentralOffice.setName("pNoteForCentralOffice");
		noteForCentralOffice.setValue(pNoteForCentralOffice);
		noteForCentralOffice.setType(Integer.class);
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
		//Cursor cursor = context.getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderExport(), SalesOrderHeaderQuery.PROJECTION, selection, selectionArgs, null);
		return null;
	}
	
	private String createLines() {
		// get lines data
		//Cursor cursor = context.getContentResolver().query(MobileStoreContract.SaleOrderLines.buildSaleOrderLineExportUri(), SalesOrderLineQuery.PROJECTION, selection, selectionArgs, sortOrder);
		return null;
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
		// TODO Auto-generated method stub
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

	public Integer getpNoteForCentralOffice() {
		return pNoteForCentralOffice;
	}

	public void setpNoteForCentralOffice(Integer pNoteForCentralOffice) {
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
                MobileStoreContract.SaleOrders.SALES_ORDER_NO,
                MobileStoreContract.Customers.CUSTOMER_NO,
                MobileStoreContract.SaleOrders.LOCATION_CODE,
                MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE,
                MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO,
                MobileStoreContract.SaleOrders.QUOTE_NO,
                MobileStoreContract.SalesPerson.SALE_PERSON_NO,
                MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE,
                MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID,
                MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID,
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
                //MobileStoreContract.SaleOrders.DOCUMENT_TYPE,
                //MobileStoreContract.SaleOrders.SALES_ORDER_NO,
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

		int _ID = 0;
		int DOCUMENT_TYPE = 1;
		int SALES_ORDER_NO = 2;
		int LINE_NO = 3;
		int ITEM_NO = 4;
		int QUANTITY = 5;
		int UNIT_SALES_PRICE_DIN = 6;
		int REAL_DISCOUNT = 7;
		int CAMPAIGN_STATUS = 8;
		int QUOTE_REFUSED_STATUS = 9;
		int BACKORDER_STATUS = 10;
		int AVAILABLE_TO_WHOLE_SHIPMENT = 11; 
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
}
