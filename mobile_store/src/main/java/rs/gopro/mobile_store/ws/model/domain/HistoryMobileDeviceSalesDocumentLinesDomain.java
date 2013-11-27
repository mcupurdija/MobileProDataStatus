package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class HistoryMobileDeviceSalesDocumentLinesDomain extends Domain {

	public String document_type;
	public String document_no;
	public String line_no;
	public String item_no;
	public String item_name;
	public String location_code;
	public String quantity;
	public String available_quantity;
	public String unit_of_measure_code;
	public String unit_price;
	public String unit_price_eur;
	public String minimum_line_discount_percent;
	public String maximum_line_discount_percent;
	public String line_discount_percent;
	public String line_amount;
	public String item_campaign_status;
	public String quote_refused_reason;
	public String backorder_shipment_status;
	public String available_to_whole_ship;
	public String verify_status;
	public String price_and_disc_are_correct;
	public String sales_header_no;
	
	public HistoryMobileDeviceSalesDocumentLinesDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type","document_no","line_no","item_no","item_name",
		"location_code","quantity","available_quantity","unit_of_measure_code","unit_price","unit_price_eur",
		"minimum_line_discount_percent","maximum_line_discount_percent","line_discount_percent","line_amount",
		"item_campaign_status","quote_refused_reason","backorder_shipment_status","available_to_whole_ship","verify_status",
		"price_and_disc_are_correct","sales_header_no" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();

		contentValues.put(MobileStoreContract.SaleOrderLines.LOCATION_CODE, location_code == "" ? "001" : location_code);
		contentValues.put(MobileStoreContract.SaleOrderLines.QUANTITY, WsDataFormatEnUsLatin.toDoubleFromWs(quantity));
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE, WsDataFormatEnUsLatin.toDoubleFromWs(unit_price));
		contentValues.put(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(line_discount_percent));
		contentValues.put(MobileStoreContract.SaleOrderLines.CAMPAIGN_STATUS, item_campaign_status == "" ? "0" : item_campaign_status);
		contentValues.put(MobileStoreContract.SaleOrderLines.QUOTE_REFUSED_STATUS, quote_refused_reason == "" ? "0" : quote_refused_reason);
		contentValues.put(MobileStoreContract.SaleOrderLines.BACKORDER_STATUS, backorder_shipment_status == "" ? "0" : backorder_shipment_status);
		contentValues.put(MobileStoreContract.SaleOrderLines.AVAILABLE_TO_WHOLE_SHIPMENT, available_to_whole_ship == "" ? "0" : available_to_whole_ship);
		
		contentValues.put(MobileStoreContract.SaleOrderLines.LINE_NO, line_no);
		
		contentValues.put(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE, WsDataFormatEnUsLatin.toDoubleFromWs(available_quantity));
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE_EUR, WsDataFormatEnUsLatin.toDoubleFromWs(unit_price_eur));
		contentValues.put(MobileStoreContract.SaleOrderLines.MIN_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(minimum_line_discount_percent));
		contentValues.put(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(maximum_line_discount_percent));
		
		contentValues.put(MobileStoreContract.SaleOrderLines.VERIFY_STATUS, verify_status == "" ? "0": verify_status);
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE_DISCOUNT_STATUS, price_and_disc_are_correct == "" ? "0" : price_and_disc_are_correct);
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.ITEMS, MobileStoreContract.Items.ITEM_NO, item_no, MobileStoreContract.SaleOrderLines.ITEM_ID));
		dataHolders.add(new RowItemDataHolder(Tables.SALE_ORDERS, MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, document_no, MobileStoreContract.SaleOrderLines.SALE_ORDER_ID));
		return dataHolders;
	}

}
