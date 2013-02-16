package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
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
	
	public MobileDeviceSalesDocumentHeaderDomain() {
	}

	private static final String[] COLUMNS = new String[] { "document_type", "sales_order_device_no", "sales_order_no", 
		"order_condition_status", "financial_control_status", "order_status_for_shipment", "order_value_status" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MobileStoreContract.SaleOrders.DOCUMENT_TYPE, document_type);
		contentValues.put(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, sales_order_device_no);
		contentValues.put(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS, order_condition_status == "" ? "0" : order_condition_status);
		contentValues.put(MobileStoreContract.SaleOrders.FIN_CONTROL_STATUS, financial_control_status == "" ? "0" : financial_control_status);
		contentValues.put(MobileStoreContract.SaleOrders.ORDER_STATUS_FOR_SHIPMENT, order_status_for_shipment == "" ? "0" : order_status_for_shipment);
		contentValues.put(MobileStoreContract.SaleOrders.ORDER_VALUE_STATUS, order_value_status == "" ? "0" : order_value_status);
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		return dataHolders;
	}

}
