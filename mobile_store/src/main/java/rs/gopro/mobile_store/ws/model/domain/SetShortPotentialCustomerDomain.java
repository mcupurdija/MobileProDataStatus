package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SetShortPotentialCustomerDomain extends Domain {
	
	public String customer_no;
	
	private static final String[] COLUMNS = new String[] { "customer_no"
	};
	
	public SetShortPotentialCustomerDomain() {
	}

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(MobileStoreContract.Customers.CUSTOMER_NO, customer_no);
		
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		return new ArrayList<RowItemDataHolder>();
	}

}
