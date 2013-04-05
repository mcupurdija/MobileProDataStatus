package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.SalesPerson;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SetPotentialCustomerDomain extends Domain {
	
	public String customer_no;
	public String name;
	public String name2;
	public String address;
	public String city;
	public String phone;
	public String mobile;
	public String sales_person_no;
	public String vat_reg_no;
	public String post_code;
	public String email;
	public String company_no;
	public String company_id;
	public String global_dimension;
	public String number_of_blue_coat;
	public String number_of_grey_coat;
	public String department;
	public String position;
	
	private static final String[] COLUMNS = new String[] { "customer_no", "name", "name2", "address", "city", "phone", "mobile", 
		"sales_person_no", "vat_reg_no", "post_code", "email", "company_no",
		"company_id", "global_dimension", "number_of_blue_coat", "number_of_grey_coat", "department", "position"
	};
	
	public SetPotentialCustomerDomain() {
	}

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(MobileStoreContract.Customers.CUSTOMER_NO, customer_no);
		contentValues.put(MobileStoreContract.Customers.NAME, name);
		contentValues.put(MobileStoreContract.Customers.NAME_2 , name2 );
		contentValues.put(MobileStoreContract.Customers.PHONE , phone);
		contentValues.put(MobileStoreContract.Customers.MOBILE , mobile);
		contentValues.put(MobileStoreContract.Customers.EMAIL , email);
		contentValues.put(MobileStoreContract.Customers.CITY , city);
		contentValues.put(MobileStoreContract.Customers.VAT_REG_NO, vat_reg_no);
		contentValues.put(MobileStoreContract.Customers.POST_CODE, post_code);
		contentValues.put(MobileStoreContract.Customers.COMPANY_ID, company_id);
		contentValues.put(MobileStoreContract.Customers.GLOBAL_DIMENSION, global_dimension);
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_BLUE_COAT, number_of_blue_coat);
		contentValues.put(MobileStoreContract.Customers.NUMBER_OF_GREY_COAT, number_of_grey_coat);
		contentValues.put(MobileStoreContract.SalesPerson.SALE_PERSON_NO, sales_person_no);
		
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> transferNoToIdList = new ArrayList<RowItemDataHolder>();
		transferNoToIdList.add(new RowItemDataHolder(Tables.SALES_PERSONS, SalesPerson.SALE_PERSON_NO, sales_person_no, Contacts.SALES_PERSON_ID));	
		return transferNoToIdList;
	}

}
