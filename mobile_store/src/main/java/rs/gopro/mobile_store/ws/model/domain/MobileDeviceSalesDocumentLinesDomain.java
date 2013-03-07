package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class MobileDeviceSalesDocumentLinesDomain extends Domain {

	public String document_type;
	public String sales_order_device_no;
	public String line_no;
	
	public String available_quantity;
	public String price;
	public String price_eur;
	public String min_discount;
	public String max_discount;
	public String discount;
	
	public String available_to_whole_ship;
	public String verify_status;
	public String price_and_discount_are_correct;
	
	public MobileDeviceSalesDocumentLinesDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type", "sales_order_device_no", "line_no", 
		"available_quantity", "price", "price_eur", "min_discount", "max_discount" , "discount",
		"available_to_whole_ship", "verify_status" , "price_and_discount_are_correct" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		//contentValues.put(MobileStoreContract.SaleOrders.DOCUMENT_TYPE, document_type);
		contentValues.put(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, sales_order_device_no);
		contentValues.put(MobileStoreContract.SaleOrderLines.LINE_NO, line_no);
		
		contentValues.put(MobileStoreContract.SaleOrderLines.QUANTITY_AVAILABLE, WsDataFormatEnUsLatin.toDoubleFromWs(available_quantity));
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE, WsDataFormatEnUsLatin.toDoubleFromWs(price));
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE_EUR, WsDataFormatEnUsLatin.toDoubleFromWs(price_eur));
		contentValues.put(MobileStoreContract.SaleOrderLines.MIN_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(min_discount));
		contentValues.put(MobileStoreContract.SaleOrderLines.MAX_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(max_discount));
		contentValues.put(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, WsDataFormatEnUsLatin.toDoubleFromWs(discount));
		
		contentValues.put(MobileStoreContract.SaleOrderLines.AVAILABLE_TO_WHOLE_SHIPMENT, available_to_whole_ship == "" ? "0" : available_to_whole_ship);
		contentValues.put(MobileStoreContract.SaleOrderLines.VERIFY_STATUS, verify_status == "" ? "0": verify_status);
		contentValues.put(MobileStoreContract.SaleOrderLines.PRICE_DISCOUNT_STATUS, price_and_discount_are_correct == "" ? "0" : price_and_discount_are_correct);
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.SALE_ORDERS, MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, sales_order_device_no, MobileStoreContract.SaleOrderLines.SALE_ORDER_ID));
		return dataHolders;
	}

}
