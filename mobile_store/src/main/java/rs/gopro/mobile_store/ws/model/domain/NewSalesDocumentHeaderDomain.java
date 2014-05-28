package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class NewSalesDocumentHeaderDomain extends Domain {

	public String document_type;
	public String document_no;
	public String customer_no;
	public String ship_to_address_code;
	public String document_date;
	public String location_code;
	public String shortcut_dimension_1_code;
	public String contact_name;
	public String external_document_no;
	public String payment_method_code;
	public String quote_no;
	public String contact_no;
	public String requested_delivery_date;
	public String backorder_shipment_status;
	public String order_condition_status;
	public String quote_realized_status;
	public String cust_uses_transit_cust;
	public String contact_phone;
	public String hide_discount_on_invoice;
	public String mandatory_declaration_trade;
	public String email_recepients_addresses;

	public NewSalesDocumentHeaderDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type",
			"document_no", "customer_no", "ship_to_address_code",
			"document_date", "location_code", "shortcut_dimension_1_code",
			"contact_name", "external_document_no", "payment_method_code",
			"quote_no", "contact_no", "requested_delivery_date",
			"backorder_shipment_status", "order_condition_status",
			"quote_realized_status", "cust_uses_transit_cust", "contact_phone",
			"hide_discount_on_invoice", "mandatory_declaration_trade",
			"email_recepients_addresses" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SaleOrders.DOCUMENT_TYPE, document_type);
		contentValues.put(SaleOrders.CUSTOMER_NO, customer_no);
		contentValues.put(SaleOrders.SALES_ORDER_DEVICE_NO, document_no);
		//contentValues.put(SaleOrders.SHIPP_TO_ADDRESS_ID, ship_to_address_code); // PROBLEM !!!
		contentValues.put(SaleOrders.ORDER_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(document_date));
		contentValues.put(SaleOrders.LOCATION_CODE, location_code == "" ? "001" : location_code);
		contentValues.put(SaleOrders.SHORTCUT_DIMENSION_1_CODE, shortcut_dimension_1_code);
		contentValues.put(SaleOrders.CONTACT_NAME, contact_name);
		contentValues.put(SaleOrders.EXTERNAL_DOCUMENT_NO, external_document_no);
		contentValues.put(SaleOrders.PAYMENT_OPTION, payment_method_code);
		contentValues.put(SaleOrders.QUOTE_NO, quote_no);
		contentValues.put(SaleOrders.REQUESTED_DELIVERY_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(requested_delivery_date));
		contentValues.put(SaleOrders.BACKORDER_SHIPMENT_STATUS, backorder_shipment_status  == "" ? "0" : backorder_shipment_status);
		contentValues.put(SaleOrders.ORDER_CONDITION_STATUS, order_condition_status == "" ? "0" : order_condition_status);
		contentValues.put(SaleOrders.QUOTE_REALIZED_STATUS, quote_realized_status == "" ? "0" : quote_realized_status);
		contentValues.put(SaleOrders.CUST_USES_TRANSIT_CUST, cust_uses_transit_cust);
		contentValues.put(SaleOrders.CONTACT_PHONE, contact_phone);
		contentValues.put(SaleOrders.HIDE_REBATE, hide_discount_on_invoice);
		contentValues.put(SaleOrders.FURTHER_SALE, mandatory_declaration_trade);
		contentValues.put(SaleOrders.CONTACT_EMAIL, email_recepients_addresses);
		contentValues.put(SaleOrders.SALES_PERSON_ID, "1");

		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Customers.CUSTOMER_NO, customer_no, SaleOrders.CUSTOMER_ID));
		dataHolders.add(new RowItemDataHolder(Tables.CONTACTS, MobileStoreContract.Contacts.CONTACT_NO, contact_no, SaleOrders.CONTACT_ID));
		// dataHolders.add(new RowItemDataHolder(Tables.USERS, Users.USERNAME, salesperson_code, SaleOrders.SALES_PERSON_ID));
		// if problem set 0
		return dataHolders;
	}

}
