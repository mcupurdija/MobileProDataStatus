package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import android.content.ContentResolver;
import android.os.Parcel;

public class ItemQtySalesPriceAndDiscSyncObject extends SyncObject {

	public static String TAG = "ItemQtySalesPriceAndDiscSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.ITEM_QTY_SALE_PRICE_DISC_SYNC_ACTION";

	private String pItemNoa46;
	private Integer pPotentialCustomer;
	private String pCustomerNoa46;
	private String pQuantityOnSalesLineAsTxt;
	private String pSalespersonCode;
	private Integer pDocumentType;
	private String pDocumentNoa46;
	private Integer pAvailableToWholeShip;
	private String pQuantityAsTxt;
	private String pSalesPriceRSDAsTxt;
	private String pDiscountPctAsTxt;
	private String pSubstituteItemNoa46;
	private String pOutstandingPurchaseLinesTxt;
	private String pMinimumSalesQuantityTxt;
	private String pVATRate;
	
	public static final Creator<ItemQtySalesPriceAndDiscSyncObject> CREATOR = new Creator<ItemQtySalesPriceAndDiscSyncObject>() {

		@Override
		public ItemQtySalesPriceAndDiscSyncObject createFromParcel(Parcel source) {
			return new ItemQtySalesPriceAndDiscSyncObject(source);
		}

		@Override
		public ItemQtySalesPriceAndDiscSyncObject[] newArray(int size) {
			return new ItemQtySalesPriceAndDiscSyncObject[size];
		}

	};

	public ItemQtySalesPriceAndDiscSyncObject() {
		super();
	}

	public ItemQtySalesPriceAndDiscSyncObject(Parcel parcel) {
		super(parcel);
		setpItemNoa46(parcel.readString());
		setpPotentialCustomerNoa46(parcel.readInt());
		setpCustomerNoa46(parcel.readString());
		setpQuantityOnSalesLineAsTxt(parcel.readString());
		setpSalespersonCode(parcel.readString());
		setpDocumentType(parcel.readInt());
		setpDocumentNoa46(parcel.readString());
		setpAvailableToWholeShip(parcel.readInt());
		setpQuantityAsTxt(parcel.readString());
		setpSalesPriceRSDAsTxt(parcel.readString());
		setpDiscountPctAsTxt(parcel.readString());
		setpSubstituteItemNoa46(parcel.readString());
		setpOutstandingPurchaseLinesTxt(parcel.readString());
		setpMinimumSalesUnitQuantityTxt(parcel.readString());
		setpVATRate(parcel.readString());
	}

	public ItemQtySalesPriceAndDiscSyncObject(String pItemNoa46, Integer pPotentialCustomer, String pCustomerNoa46, String pQuantityOnSalesLineAsTxt, String pSalespersonCode,
			Integer pDocumentType, String pDocumentNoa46, Integer pAvailableToWholeShip, String pQuantityAsTxt, String pSalesPriceRSDAsTxt, String pDiscountPctAsTxt, String pSubstituteItemNoa46, 
			String pOutstandingPurchaseLinesTxt, String pVATRate) {
		super();
		this.pItemNoa46 = pItemNoa46;
		this.pPotentialCustomer = pPotentialCustomer;
		this.pCustomerNoa46 = pCustomerNoa46;
		this.pQuantityOnSalesLineAsTxt = pQuantityOnSalesLineAsTxt;
		this.pSalespersonCode = pSalespersonCode;
		this.pDocumentNoa46 = pDocumentNoa46;
		this.pDocumentType = pDocumentType;
		this.pAvailableToWholeShip = pAvailableToWholeShip;
		this.pQuantityAsTxt = pQuantityAsTxt;
		this.pSalesPriceRSDAsTxt = pSalesPriceRSDAsTxt;
		this.pDiscountPctAsTxt = pDiscountPctAsTxt;
		this.pSubstituteItemNoa46 = pSubstituteItemNoa46;
		this.pOutstandingPurchaseLinesTxt = pOutstandingPurchaseLinesTxt;
		this.pMinimumSalesQuantityTxt = ""; // can set like this because it is return parameter
		this.pVATRate = pVATRate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpItemNoa46());
		dest.writeInt(getpPotentialCustomer());
		dest.writeString(getpCustomerNoa46());
		dest.writeString(getpQuantityOnSalesLineAsTxt());
		dest.writeString(getpSalespersonCode());
		dest.writeInt(getpDocumentType());
		dest.writeString(getpDocumentNoa46());
		dest.writeInt(getpAvailableToWholeShip());
		dest.writeString(getpQuantityAsTxt());
		dest.writeString(getpSalesPriceRSDAsTxt());
		dest.writeString(getpDiscountPctAsTxt());
		dest.writeString(getpSubstituteItemNoa46());
		dest.writeString(getpOutstandingPurchaseLinesTxt());
		dest.writeString(getpMinimumSalesUnitQuantityTxt());
		dest.writeString(getpVATRate());
	}

	@Override
	public String getWebMethodName() {
		return "GetItemQtySalesPriceAndDisc";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();
		PropertyInfo itemNoa46Info = new PropertyInfo();
		itemNoa46Info.setName("pItemNoa46");
		itemNoa46Info.setValue(pItemNoa46);
		itemNoa46Info.setType(String.class);
		properies.add(itemNoa46Info);

		PropertyInfo potentialCustomerNoa46Info = new PropertyInfo();
		potentialCustomerNoa46Info.setName("pPotentialCustomer");
		potentialCustomerNoa46Info.setValue(pPotentialCustomer);
		potentialCustomerNoa46Info.setType(Integer.class);
		properies.add(potentialCustomerNoa46Info);
		
		PropertyInfo customerNoa46Info = new PropertyInfo();
		customerNoa46Info.setName("pCustomerNoa46");
		customerNoa46Info.setValue(pCustomerNoa46);
		customerNoa46Info.setType(String.class);
		properies.add(customerNoa46Info);

		PropertyInfo quantityOnSalesLineInfo = new PropertyInfo();
		quantityOnSalesLineInfo.setName("pQuantityOnSalesLineAsTxt");
		quantityOnSalesLineInfo.setValue(pQuantityOnSalesLineAsTxt);
		quantityOnSalesLineInfo.setType(String.class);
		properies.add(quantityOnSalesLineInfo);

		PropertyInfo salespersonCode = new PropertyInfo();
		salespersonCode.setName("pSalespersonCode");
		salespersonCode.setValue(pSalespersonCode);
		salespersonCode.setType(String.class);
		properies.add(salespersonCode);

		PropertyInfo documentTypeInfo = new PropertyInfo();
		documentTypeInfo.setName("pDocumentType");
		documentTypeInfo.setValue(pDocumentType);
		documentTypeInfo.setType(Integer.class);
		properies.add(documentTypeInfo);
		
		PropertyInfo documentNo = new PropertyInfo();
		documentNo.setName("pDocumentNoa46");
		documentNo.setValue(pDocumentNoa46);
		documentNo.setType(String.class);
		properies.add(documentNo);

		PropertyInfo availableToWholeShipInfo = new PropertyInfo();
		availableToWholeShipInfo.setName("pAvailableToWholeShip");
		availableToWholeShipInfo.setValue(pAvailableToWholeShip);
		availableToWholeShipInfo.setType(Integer.class);
		properies.add(availableToWholeShipInfo);

		PropertyInfo quantityAsTxtInfo = new PropertyInfo();
		quantityAsTxtInfo.setName("pQuantityAsTxt");
		quantityAsTxtInfo.setValue(pQuantityAsTxt);
		quantityAsTxtInfo.setType(String.class);
		properies.add(quantityAsTxtInfo);

		PropertyInfo salesPriceRSDAsTxtInfo = new PropertyInfo();
		salesPriceRSDAsTxtInfo.setName("pSalesPriceRSDAsTxt");
		salesPriceRSDAsTxtInfo.setValue(pSalesPriceRSDAsTxt);
		salesPriceRSDAsTxtInfo.setType(String.class);
		properies.add(salesPriceRSDAsTxtInfo);

		PropertyInfo discountPctAsTxtInfo = new PropertyInfo();
		discountPctAsTxtInfo.setName("pDiscountPctAsTxt");
		discountPctAsTxtInfo.setValue(pDiscountPctAsTxt);
		discountPctAsTxtInfo.setType(String.class);
		properies.add(discountPctAsTxtInfo);
		
		PropertyInfo substituteItemNoa46 = new PropertyInfo();
		substituteItemNoa46.setName("pSubstituteItemNoa46");
		substituteItemNoa46.setValue(pSubstituteItemNoa46);
		substituteItemNoa46.setType(String.class);
		properies.add(substituteItemNoa46);
		
		PropertyInfo outstandingPurchaseLinesTxt = new PropertyInfo();
		outstandingPurchaseLinesTxt.setName("pOutstandingPurchaseLinesTxt");
		outstandingPurchaseLinesTxt.setValue(pOutstandingPurchaseLinesTxt);
		outstandingPurchaseLinesTxt.setType(String.class);
		properies.add(outstandingPurchaseLinesTxt);
		
		PropertyInfo minimumSalesUnitQuantityTxt = new PropertyInfo();
		minimumSalesUnitQuantityTxt.setName("pMinimumSalesQuantityTxt");
		minimumSalesUnitQuantityTxt.setValue(pMinimumSalesQuantityTxt);
		minimumSalesUnitQuantityTxt.setType(String.class);
		properies.add(minimumSalesUnitQuantityTxt);
		
		PropertyInfo pVATRateInfo = new PropertyInfo();
		pVATRateInfo.setName("pVATRate");
		pVATRateInfo.setValue(pVATRate);
		pVATRateInfo.setType(String.class);
		properies.add(pVATRateInfo);
		
		return properies;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
//		Map<String, String> responseProperties = new HashMap<String, String>();
//		for(int i=0;i<soapResponse.getPropertyCount();i++){
//			responseProperties.put(soapResponse.getProperty(i), responseProperty.toString());
//		}
		bindResponseProperties(soapResponse);
		return 0;
	}
	
	private void bindResponseProperties(SoapObject soapResponse) {
		try {
			this.pAvailableToWholeShip = Integer.valueOf(soapResponse.getPropertyAsString("pAvailableToWholeShip"));
		} catch (NumberFormatException ne) {
			LogUtils.LOGE(TAG, "Bat int format!", ne);
			this.pAvailableToWholeShip = -1;
		}
		this.pQuantityAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pQuantityAsTxt"));
		this.pSalesPriceRSDAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pSalesPriceRSDAsTxt"));
		this.pDiscountPctAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pDiscountPctAsTxt"));
		this.pSubstituteItemNoa46 = soapResponse.getPropertyAsString("pSubstituteItemNoa46");
		this.pOutstandingPurchaseLinesTxt = soapResponse.getPropertyAsString("pOutstandingPurchaseLinesTxt");
		this.pMinimumSalesQuantityTxt = soapResponse.getPropertyAsString("pMinimumSalesQuantityTxt");
		this.pVATRate = soapResponse.getPrimitivePropertyAsString("pVATRate");
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	public String getpItemNoa46() {
		return pItemNoa46;
	}

	public void setpItemNoa46(String pItemNoa46) {
		this.pItemNoa46 = pItemNoa46;
	}

	public String getpCustomerNoa46() {
		return pCustomerNoa46;
	}

	public void setpCustomerNoa46(String pCustomerNoa46) {
		this.pCustomerNoa46 = pCustomerNoa46;
	}

	public Integer getpPotentialCustomer() {
		return pPotentialCustomer;
	}

	public void setpPotentialCustomerNoa46(Integer pPotentialCustomer) {
		this.pPotentialCustomer = pPotentialCustomer;
	}

	public String getpQuantityOnSalesLineAsTxt() {
		return pQuantityOnSalesLineAsTxt;
	}

	public void setpQuantityOnSalesLineAsTxt(String pQuantityOnSalesLineAsTxt) {
		this.pQuantityOnSalesLineAsTxt = pQuantityOnSalesLineAsTxt;
	}

	public String getpSalespersonCode() {
		return pSalespersonCode;
	}

	public void setpSalespersonCode(String pSalespersonCode) {
		this.pSalespersonCode = pSalespersonCode;
	}

	public Integer getpDocumentType() {
		return pDocumentType;
	}

	public void setpDocumentType(Integer pDocumentType) {
		this.pDocumentType = pDocumentType;
	}

	public Integer getpAvailableToWholeShip() {
		return pAvailableToWholeShip;
	}

	public void setpAvailableToWholeShip(Integer pAvailableToWholeShip) {
		this.pAvailableToWholeShip = pAvailableToWholeShip;
	}

	public String getpQuantityAsTxt() {
		return pQuantityAsTxt;
	}

	public void setpQuantityAsTxt(String pQuantityAsTxt) {
		this.pQuantityAsTxt = pQuantityAsTxt;
	}

	public String getpSalesPriceRSDAsTxt() {
		return pSalesPriceRSDAsTxt;
	}

	public void setpSalesPriceRSDAsTxt(String pSalesPriceRSDAsTxt) {
		this.pSalesPriceRSDAsTxt = pSalesPriceRSDAsTxt;
	}

	public String getpDiscountPctAsTxt() {
		return pDiscountPctAsTxt;
	}

	public void setpDiscountPctAsTxt(String pDiscountPctAsTxt) {
		this.pDiscountPctAsTxt = pDiscountPctAsTxt;
	}
	
	public String getpSubstituteItemNoa46() {
		return pSubstituteItemNoa46;
	}

	public void setpSubstituteItemNoa46(String pSubstituteItemNoa46) {
		this.pSubstituteItemNoa46 = pSubstituteItemNoa46;
	}

	public String getpOutstandingPurchaseLinesTxt() {
		return pOutstandingPurchaseLinesTxt;
	}

	public void setpOutstandingPurchaseLinesTxt(String pOutstandingPurchaseLinesTxt) {
		this.pOutstandingPurchaseLinesTxt = pOutstandingPurchaseLinesTxt;
	}

	public String getpMinimumSalesUnitQuantityTxt() {
		return pMinimumSalesQuantityTxt;
	}

	public void setpMinimumSalesUnitQuantityTxt(String pMinimumSalesUnitQuantityTxt) {
		this.pMinimumSalesQuantityTxt = pMinimumSalesUnitQuantityTxt;
	}

	public String getpDocumentNoa46() {
		return pDocumentNoa46;
	}

	public void setpDocumentNoa46(String pDocumentNoa46) {
		this.pDocumentNoa46 = pDocumentNoa46;
	}

	public String getpMinimumSalesQuantityTxt() {
		return pMinimumSalesQuantityTxt;
	}

	public void setpMinimumSalesQuantityTxt(String pMinimumSalesQuantityTxt) {
		this.pMinimumSalesQuantityTxt = pMinimumSalesQuantityTxt;
	}

	public void setpPotentialCustomer(Integer pPotentialCustomer) {
		this.pPotentialCustomer = pPotentialCustomer;
	}

	public String getpVATRate() {
		return pVATRate;
	}

	public void setpVATRate(String pVATRate) {
		this.pVATRate = pVATRate;
	}
	
}
