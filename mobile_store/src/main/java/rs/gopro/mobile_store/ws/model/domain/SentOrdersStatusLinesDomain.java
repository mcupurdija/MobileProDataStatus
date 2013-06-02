package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatus;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatusLines;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SentOrdersStatusLinesDomain extends Domain {

	public String document_type;
	public String sent_order_status_no;
	public String line_no;
	public String customer_no;
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
	public String quantity_shipped;
	public String quantity_invoiced;
	public String inv_discount_amount;
	public String line_amount;
	public String unit_of_measure_code;
	
	
	public String promised_delivery_date;
	public String price_and_disc_are_correct;
	public String confirmed_promised_delivery_date;
	public String price_include_vat;
	public String reserved;

	private static final String[] COLUMNS = new String[] { "document_type", "sent_order_status_no", "line_no", "customer_no", "line_type",
		"item_no", "location_code", "description", 
		"quantity", "outstanding_quantity", "unit_price", "vat_percent", "line_discount_percent",
		"line_discount_amount", "quantity_shipped", "quantity_invoiced", "inv_discount_amount", "line_amount", "unit_of_measure_code",
		"promised_delivery_date", "confirmed_promised_delivery_date", "price_include_vat", "reserved"};

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
		contentValues.put(SentOrdersStatusLines.QUANTITY, WsDataFormatEnUsLatin.toDoubleFromWs(quantity));
		contentValues.put(SentOrdersStatusLines.OUTSTANDING_QUANTITY, WsDataFormatEnUsLatin.toDoubleFromWs(outstanding_quantity));
		contentValues.put(SentOrdersStatusLines.UNIT_PRICE, WsDataFormatEnUsLatin.toDoubleFromWs(unit_price));
		contentValues.put(SentOrdersStatusLines.VAT_PERCENT, WsDataFormatEnUsLatin.toDoubleFromWs(vat_percent));
		contentValues.put(SentOrdersStatusLines.LINE_DISCOUNT_PERCENT, WsDataFormatEnUsLatin.toDoubleFromWs(line_discount_percent));
		contentValues.put(SentOrdersStatusLines.LINE_DISCOUNT_AMOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(line_discount_amount));
		contentValues.put(SentOrdersStatusLines.INV_DISCOUNT_AMOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(inv_discount_amount));
		contentValues.put(SentOrdersStatusLines.LINE_AMOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(line_amount));
		contentValues.put(SentOrdersStatusLines.UNIT_OF_MEASURE_CODE, unit_of_measure_code);
		contentValues.put(SentOrdersStatusLines.PRICE_INCLUDE_VAT, price_include_vat);

		contentValues.put(SentOrdersStatusLines.PROMISED_DELIVERY_DATE, WsDataFormatEnUsLatin.toDbDateFromWsString(promised_delivery_date));
		contentValues.put(SentOrdersStatusLines.CONFIRMED_PROMISED_DELIVERY_DATE, confirmed_promised_delivery_date);
		
		contentValues.put(SentOrdersStatusLines.QUANTITY_SHIPPED, WsDataFormatEnUsLatin.toDoubleFromWs(quantity_shipped));
		contentValues.put(SentOrdersStatusLines.QUANTITY_INVOICED, WsDataFormatEnUsLatin.toDoubleFromWs(quantity_invoiced));
		contentValues.put(SentOrdersStatusLines.PRICE_AND_DISC_ARE_CORRECT, price_and_disc_are_correct);
		contentValues.put(SentOrdersStatusLines.RESERVED, WsDataFormatEnUsLatin.toDoubleFromWs(reserved));
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, Customers.CUSTOMER_NO, customer_no, SentOrdersStatusLines.CUSTOMER_ID));
		dataHolders.add(new RowItemDataHolder(Tables.SENT_ORDERS_STATUS, SentOrdersStatus.SENT_ORDER_NO, sent_order_status_no, SentOrdersStatusLines.SENT_ORDER_STATUS_ID));
		dataHolders.add(new RowItemDataHolder(Tables.ITEMS, Items.ITEM_NO, item_no, SentOrdersStatusLines.ITEM_ID));
		return dataHolders;
	}

}
