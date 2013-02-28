package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerTradeAgreemnt;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;

public class CustomerTradeAgreementDomain extends Domain {
	
	public String customer_no;
	public String entry_type;
	public String code;
	public String minimum_quantity;
	public String starting_date; 
	public String ending_date;
	public String actual_discount;
	
	private static final String[] COLUMNS = new String[] {"customer_no", "entry_type", "code", "minimum_quantity", "starting_date", "ending_date", "actual_discount"};

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(CustomerTradeAgreemnt.CUSTOMER_ID, customer_no);
		contentValues.put(CustomerTradeAgreemnt.ENTRY_TYPE, getEntry_type());
		contentValues.put(CustomerTradeAgreemnt.CODE, getCode());
		contentValues.put(CustomerTradeAgreemnt.MINIMUM_QUANTITY, getMinimum_quantity());
		contentValues.put(CustomerTradeAgreemnt.STARTING_DATE,  DateUtils.formatDateFromNavisionToDB(getStarting_date()));
		contentValues.put(CustomerTradeAgreemnt.ENDING_DATE,  DateUtils.formatDateFromNavisionToDB(getEnding_date()));
		contentValues.put(CustomerTradeAgreemnt.ACTUAL_DISCOUNT, getActual_discount());
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		dataHolders.add(new RowItemDataHolder(Tables.CUSTOMERS, ElectronicCardCustomer.CUSTOMER_NO, customer_no, ElectronicCardCustomer.CUSTOMER_ID));
		return dataHolders;
	}

	public String getEntry_type() {
		return entry_type;
	}

	public void setEntry_type(String entry_type) {
		this.entry_type = entry_type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMinimum_quantity() {
		return minimum_quantity;
	}

	public void setMinimum_quantity(String minimum_quantity) {
		this.minimum_quantity = minimum_quantity;
	}

	public String getStarting_date() {
		return starting_date;
	}

	public void setStarting_date(String starting_date) {
		this.starting_date = starting_date;
	}

	public String getEnding_date() {
		return ending_date;
	}

	public void setEnding_date(String ending_date) {
		this.ending_date = ending_date;
	}

	public String getActual_discount() {
		return actual_discount;
	}

	public void setActual_discount(String actual_discount) {
		this.actual_discount = actual_discount;
	}

	
	
}
