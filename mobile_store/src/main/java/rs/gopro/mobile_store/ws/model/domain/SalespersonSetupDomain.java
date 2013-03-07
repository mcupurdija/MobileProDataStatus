package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SalespersonSetupDomain extends Domain {

	public String default_location;
	public String allow_location_change_on_mdsd;
	public String team_partner;
	public String access_to_all_customers;
	public String invoice_query_number_of_days;
	public String password;
	
	private static final String[] COLUMNS = new String[] { "default_location","allow_location_change_on_mdsd","team_partner","access_to_all_customers","invoice_query_number_of_days","password" };
	
	public SalespersonSetupDomain() {
	}

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MobileStoreContract.SalesPersonsColumns.DEFAULT_LOCATION, default_location);
		contentValues.put(MobileStoreContract.SalesPersonsColumns.ALLOW_LOCATION_CHANGE_ON_MDSD, allow_location_change_on_mdsd);
		contentValues.put(MobileStoreContract.SalesPersonsColumns.TEAM_PARTNER, team_partner);
		contentValues.put(MobileStoreContract.SalesPersonsColumns.ACCESS_TO_ALL_CUSTOMERS, access_to_all_customers);
		contentValues.put(MobileStoreContract.SalesPersonsColumns.INVOICE_QUERY_NUMBER_OF_DAYS, invoice_query_number_of_days);
		contentValues.put(MobileStoreContract.SalesPersonsColumns.PASSWORD, password);
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		return dataHolders;
	}

}
