package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatus;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatusLines;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SentOrdersStatusLinesDomain extends Domain {

	public String document_type;
	public String customer_no;
	public String sent_order_status_no;
	public String line_no;
	public String line_type;
	
	public String item_no;
	public String location_code;
	public String description;
	
	public String quantity;
	public String outstanding_quantity;
	public String unit_price;
	public String vat_percent;
	public String line_discount_percent;
	
	public String line_discount_amount;
	public String inv_discount_amount;
	public String line_amount;
	public String unit_of_measure_code;
	public String price_include_vat;

	private static final String[] COLUMNS = new String[] { "document_type", "customer_no", "sent_order_status_no", "line_no", "line_type",
		"item_no", "location_code", "description", 
		"quantity", "outstanding_quantity", "unit_price", "vat_percent", "line_discount_percent",
		"line_discount_amount", "inv_discount_amount", "line_amount", "unit_of_measure_code", "price_include_vat"};

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(SentOrdersStatus.SENT_ORDER_NO, sent_order_status_no);
		contentValues.put(SentOrdersStatusLines.LINE_NO, line_no);
		contentValues.put(Customers.CUSTOMER_NO, customer_no);
		contentValues.put(SentOrdersStatusLines.DOCUMENT_TYPE, document_type);
		contentValues.put(SentOrdersStatusLines.LINE_TYPE, line_type);
		contentValues.put(Items.ITEM_NO, item_no);
		contentValues.put(SentOrdersStatusLines.LOCATION_CODE, location_code);
		contentValues.put(SentOrdersStatusLines.DESCRIPTION, description);
		contentValues.put(SentOrdersStatusLines.QUANTITY, quantity);
		contentValues.put(SentOrdersStatusLines.OUTSTANDING_QUANTITY, outstanding_quantity);
		contentValues.put(SentOrdersStatusLines.UNIT_PRICE, unit_price);
		contentValues.put(SentOrdersStatusLines.VAT_PERCENT, vat_percent);
		contentValues.put(SentOrdersStatusLines.LINE_DISCOUNT_PERCENT, line_discount_percent);
		contentValues.put(SentOrdersStatusLines.LINE_DISCOUNT_AMOUNT, line_discount_amount);
		contentValues.put(SentOrdersStatusLines.INV_DISCOUNT_AMOUNT, inv_discount_amount);
		contentValues.put(SentOrdersStatusLines.LINE_AMOUNT, line_amount);
		contentValues.put(SentOrdersStatusLines.UNIT_OF_MEASURE_CODE, unit_of_measure_code);
		contentValues.put(SentOrdersStatusLines.PRICE_INCLUDE_VAT, price_include_vat);

		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Customers.CUSTOMER_NO, customer_no, SentOrdersStatusLines.CUSTOMER_ID));
		dataHolders.add(new RowItemDataHolder(Tables.INVOICES, SentOrdersStatus.SENT_ORDER_NO, sent_order_status_no, SentOrdersStatusLines.SENT_ORDER_STATUS_ID));
		dataHolders.add(new RowItemDataHolder(Tables.ITEMS, Items.ITEM_NO, item_no, SentOrdersStatusLines.ITEM_ID));
		return dataHolders;
	}

}
