package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class HistoryMobileDeviceSalesDocumentHeaderDomain extends Domain {

	public String document_type;
	public String document_no;
	public String sales_header_no;
	public String potential_customer;
	public String customer_no;
	public String customer_name;
	public String document_date;
	public String location_code;
	public String currency_code;
	public String shortcut_dimension_1_code;
	public String external_document_no;
	public String quote_no;
	public String salesperson_code;
	public String sales_manager_code;
	public String invoice_to_address_code;
	public String ship_to_address_code;
	public String requested_delivery_date;
	public String cust_uses_transit_cust;
	public String contact_no;
	public String contact_name;
	public String contact_phone;
	public String email_recepients_addresses;
	public String hide_discount_on_invoice;
	public String mandatory_declaration_trade;
	public String payment_method_code;
	public String backorder_shipment_status;
	public String quote_realized_status;
	public String order_condition_status;
	public String financial_control_status;
	public String order_status_for_shipment;
	public String order_value_status;
	public String document_amount;
	public String number_of_lines;
	
	public HistoryMobileDeviceSalesDocumentHeaderDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type", "document_no", "sales_header_no", "potential_customer", "customer_no", 
		"customer_name", "document_date", "location_code", "currency_code", "shortcut_dimension_1_code", "external_document_no", "quote_no", 
		"salesperson_code", "sales_manager_code", "invoice_to_address_code", "ship_to_address_code", "requested_delivery_date", 
		"cust_uses_transit_cust", "contact_no", "contact_name", "contact_phone", "email_recepients_addresses", "hide_discount_on_invoice", 
		"mandatory_declaration_trade", "payment_method_code", "backorder_shipment_status", "quote_realized_status", "order_condition_status", 
		"financial_control_status", "order_status_for_shipment", "order_value_status", "document_amount", "number_of_lines" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SaleOrders.DOCUMENT_TYPE, document_type);
		contentValues.put(SaleOrders.SALES_ORDER_DEVICE_NO, document_no);
		contentValues.put(SaleOrders.SALES_ORDER_NO, sales_header_no);
		contentValues.put(SaleOrders.ORDER_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(document_date));

        contentValues.put(SaleOrders.LOCATION_CODE, location_code);
		contentValues.put(SaleOrders.SHORTCUT_DIMENSION_1_CODE, shortcut_dimension_1_code);
		contentValues.put(SaleOrders.EXTERNAL_DOCUMENT_NO, external_document_no);
		contentValues.put(SaleOrders.QUOTE_NO, quote_no);
		//contentValues.put(SaleOrders.SALE_PERSON_NO, salesperson_code);
		
		contentValues.put(SaleOrders.REQUESTED_DELIVERY_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(requested_delivery_date));
        
		//adrese bi trebalo da idu ovde ali komplikovano je
		contentValues.putNull(SaleOrders.SHIPP_TO_ADDRESS_ID);
		contentValues.putNull(SaleOrders.SELL_TO_ADDRESS_ID);
		
		// TODO this should be fixed I do not send correct transit customer
		contentValues.put(SaleOrders.CUST_USES_TRANSIT_CUST, cust_uses_transit_cust);
        
        contentValues.put(SaleOrders.CONTACT_NAME, contact_name);
        contentValues.put(SaleOrders.CONTACT_PHONE, contact_phone);
        contentValues.put(SaleOrders.CONTACT_EMAIL, email_recepients_addresses);
		
		contentValues.put(SaleOrders.HIDE_REBATE, hide_discount_on_invoice);
		contentValues.put(SaleOrders.FURTHER_SALE, mandatory_declaration_trade);

		contentValues.put(SaleOrders.PAYMENT_OPTION, payment_method_code);
		contentValues.put(SaleOrders.BACKORDER_SHIPMENT_STATUS, backorder_shipment_status == "" ? "0" : backorder_shipment_status);
		contentValues.put(SaleOrders.QUOTE_REALIZED_STATUS, quote_realized_status == "" ? "0" : quote_realized_status);

		contentValues.put(SaleOrders.ORDER_CONDITION_STATUS, order_condition_status == "" ? "0" : order_condition_status);
		contentValues.put(SaleOrders.FIN_CONTROL_STATUS, financial_control_status == "" ? "0" : financial_control_status);
		contentValues.put(SaleOrders.ORDER_STATUS_FOR_SHIPMENT, order_status_for_shipment == "" ? "0" : order_status_for_shipment);
		contentValues.put(SaleOrders.ORDER_VALUE_STATUS, order_value_status == "" ? "0" : order_value_status);
		
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Customers.CUSTOMER_NO, customer_no, SaleOrders.CUSTOMER_ID));
		dataHolders.add(new RowItemDataHolder(Tables.CONTACTS, rs.gopro.mobile_store.provider.MobileStoreContract.Contacts.CONTACT_NO, contact_no, SaleOrders.CONTACT_ID));
		dataHolders.add(new RowItemDataHolder(Tables.USERS, Users.USERNAME, salesperson_code, SaleOrders.SALES_PERSON_ID));
		// if problem set 0
		return dataHolders;
	}

}
