package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class NewSalesDocumentLinesDomain extends Domain {

	public String document_type;
	public String sales_order_device_no;
	public String line_no;
	public String item_no;
	public String location_code;
	public String quantity;
	public String unit_price;
	public String line_discount;
	public String backorder_shipment_status;
	public String item_campaign_status;
	public String available_to_whole_ship;
	public String quote_refused_reason;
	
	public NewSalesDocumentLinesDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type", "sales_order_device_no", "line_no", 
		"item_no", "location_code", "quantity", "unit_price", "line_discount" , "backorder_shipment_status",
		"item_campaign_status", "available_to_whole_ship" , "quote_refused_reason" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		
		Integer lineNo = Integer.valueOf(line_no);
		if (lineNo < 100000) {
			lineNo /= 1000;
		} else if (lineNo < 1000000) {
			lineNo /= 10000;
		} else if (lineNo < 10000000) {
			lineNo /= 100000;
		}
		contentValues.put(MobileStoreContract.SaleOrderLines.LINE_NO, lineNo.toString());
		contentValues.put(MobileStoreContract.SaleOrderLines.LOCATION_CODE, location_code == "" ? "001" : location_code);
		contentValues.put(MobileStoreContract.SaleOrderLines.QUANTITY, WsDataFormatEnUsLatin.toDoubleFromWs(quantity));
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE, WsDataFormatEnUsLatin.toDoubleFromWs(unit_price));
		contentValues.put(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(line_discount));
		contentValues.put(MobileStoreContract.SaleOrderLines.BACKORDER_STATUS, backorder_shipment_status == "" ? "0" : backorder_shipment_status);
		//contentValues.put(MobileStoreContract.SaleOrderLines.CAMPAIGN_STATUS, item_campaign_status == "" ? "0" : item_campaign_status);
		contentValues.put(MobileStoreContract.SaleOrderLines.AVAILABLE_TO_WHOLE_SHIPMENT, available_to_whole_ship == "" ? "0" : available_to_whole_ship);
		contentValues.put(MobileStoreContract.SaleOrderLines.QUOTE_REFUSED_STATUS, quote_refused_reason == "" ? "0" : quote_refused_reason);
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.ITEMS, MobileStoreContract.Items.ITEM_NO, item_no, MobileStoreContract.SaleOrderLines.ITEM_ID));
		dataHolders.add(new RowItemDataHolder(Tables.SALE_ORDERS, MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, sales_order_device_no, MobileStoreContract.SaleOrderLines.SALE_ORDER_ID));
		return dataHolders;
	}

}
