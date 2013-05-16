package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class MobileDeviceSalesDocumentHeaderDomain extends Domain {

	public String document_type;
	public String sales_order_device_no;
	public String sales_order_no;
	
	public String order_condition_status;	
	public String financial_control_status;
	public String order_status_for_shipment;
	public String order_value_status;
	public String curr_max_discount_total_amount_difference;
	
	public MobileDeviceSalesDocumentHeaderDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type", "sales_order_device_no", "sales_order_no", 
		"order_condition_status", "financial_control_status", "order_status_for_shipment", "order_value_status", "curr_max_discount_total_amount_difference" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(SaleOrders.DOCUMENT_TYPE, document_type);
		contentValues.put(SaleOrders.SALES_ORDER_DEVICE_NO, sales_order_device_no);
		contentValues.put(SaleOrders.SALES_ORDER_NO, sales_order_no);
		contentValues.put(SaleOrders.ORDER_CONDITION_STATUS, order_condition_status == "" ? "0" : order_condition_status);
		contentValues.put(SaleOrders.FIN_CONTROL_STATUS, financial_control_status == "" ? "0" : financial_control_status);
		contentValues.put(SaleOrders.ORDER_STATUS_FOR_SHIPMENT, order_status_for_shipment == "" ? "0" : order_status_for_shipment);
		contentValues.put(SaleOrders.ORDER_VALUE_STATUS, order_value_status == "" ? "0" : order_value_status);
		contentValues.put(SaleOrders.CURR_MAX_DISCOUNT_TOTAL_AMOUNT_DIFFERENCE, curr_max_discount_total_amount_difference == "" ? Double.valueOf("0.0") : WsDataFormatEnUsLatin.toDoubleFromWs(curr_max_discount_total_amount_difference));
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		return dataHolders;
	}

}
