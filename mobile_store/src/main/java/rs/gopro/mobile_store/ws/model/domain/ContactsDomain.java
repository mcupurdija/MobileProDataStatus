package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.SalesPerson;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class ContactsDomain extends Domain {

	public String contact_no;
	public String name;
	public String name2;
	public String phone;
	public String mobile;
	public String sales_person_no;
	public String email;
	public String company_no;
	public String department;
	public String position;
	
	private static final String[] COLUMNS = new String[] { "contact_no", "name", "name2", "phone", "mobile", 
		"sales_person_no", "email", "company_no", "department", "position"
	};
	
	public ContactsDomain() {
	}

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(MobileStoreContract.Contacts.CONTACT_NO, contact_no);
		contentValues.put(MobileStoreContract.Contacts.NAME, name);
		contentValues.put(MobileStoreContract.Contacts.NAME2 , name2 );
		contentValues.put(MobileStoreContract.Contacts.PHONE , phone);
		contentValues.put(MobileStoreContract.Contacts.MOBILE_PHONE , mobile);
		contentValues.put(MobileStoreContract.Contacts.EMAIL , email);
		contentValues.put(MobileStoreContract.SalesPerson.SALE_PERSON_NO, sales_person_no);
		contentValues.put(MobileStoreContract.Contacts.DEPARTMENT, department);
		contentValues.put(MobileStoreContract.Contacts.POSITION, position);
		
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> transferNoToIdList = new ArrayList<RowItemDataHolder>();
		transferNoToIdList.add(new RowItemDataHolder(Tables.SALES_PERSONS, SalesPerson.SALE_PERSON_NO, sales_person_no, Contacts.SALES_PERSON_ID));	
		return transferNoToIdList;
	}

}
