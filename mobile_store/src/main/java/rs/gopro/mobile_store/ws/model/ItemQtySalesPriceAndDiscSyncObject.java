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
	private String pLocationCode;
	private Integer pCampaignStatus;
	private Integer pPotentialCustomer;
	private String pCustomerNoa46;
	private Double pQuantityOnSalesLine;
	private String pSalespersonCode;
	private Integer pDocumentType;
	private Integer pAvailableToWholeShip;
	private String pQuantityAsTxt;
	private String pSalesPriceRSDAsTxt;
	private String pSalesPriceEURAsTxt;
	private String pMinimumDiscountPctAsTxt;
	private String pMaximumDiscountPctAsTxt;
	private String pDiscountPctAsTxt;
	private String pSubstituteItemNoa46;
	private String pOutstandingPurchaseLinesTxt;
	private String pMinimumSalesUnitQuantityTxt;
	
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
		setpLocationCode(parcel.readString());
		setpCampaignStatus(parcel.readInt());
		setpPotentialCustomerNoa46(parcel.readInt());
		setpCustomerNoa46(parcel.readString());
		
		setpQuantityOnSalesLine(parcel.readDouble());
		setpSalespersonCode(parcel.readString());
		setpDocumentType(parcel.readInt());
		setpAvailableToWholeShip(parcel.readInt());
		setpQuantityAsTxt(parcel.readString());
		setpSalesPriceRSDAsTxt(parcel.readString());
		setpSalesPriceEURAsTxt(parcel.readString());
		setpMinimumDiscountPctAsTxt(parcel.readString());
		setpMaximumDiscountPctAsTxt(parcel.readString());
		setpDiscountPctAsTxt(parcel.readString());
		setpSubstituteItemNoa46(parcel.readString());
		setpOutstandingPurchaseLinesTxt(parcel.readString());
		setpMinimumSalesUnitQuantityTxt(parcel.readString());
	}

	public ItemQtySalesPriceAndDiscSyncObject(String pItemNoa46, String pLocationCode, Integer pCampaignStatus, Integer pPotentialCustomer, String pCustomerNoa46, Double pQuantityOnSalesLine, String pSalespersonCode,
			Integer pDocumentType, Integer pAvailableToWholeShip, String pQuantityAsTxt, String pSalesPriceRSDAsTxt, String pSalesPriceEURAsTxt, String pMinimumDiscountPctAsTxt, String pMaximumDiscountPctAsTxt, String pDiscountPctAsTxt, String pSubstituteItemNoa46, 
			String pOutstandingPurchaseLinesTxt) {
		super();
		this.pItemNoa46 = pItemNoa46;
		this.pLocationCode = pLocationCode;
		this.pCampaignStatus = pCampaignStatus;
		this.pPotentialCustomer = pPotentialCustomer;
		this.pCustomerNoa46 = pCustomerNoa46;
		this.pQuantityOnSalesLine = pQuantityOnSalesLine;
		this.pSalespersonCode = pSalespersonCode;
		this.pDocumentType = pDocumentType;
		this.pAvailableToWholeShip = pAvailableToWholeShip;
		this.pQuantityAsTxt = pQuantityAsTxt;
		this.pSalesPriceRSDAsTxt = pSalesPriceRSDAsTxt;
		this.pSalesPriceEURAsTxt = pSalesPriceEURAsTxt;
		this.pMinimumDiscountPctAsTxt = pMinimumDiscountPctAsTxt;
		this.pMaximumDiscountPctAsTxt = pMaximumDiscountPctAsTxt;
		this.pDiscountPctAsTxt = pDiscountPctAsTxt;
		this.pSubstituteItemNoa46 = pSubstituteItemNoa46;
		this.pOutstandingPurchaseLinesTxt = pOutstandingPurchaseLinesTxt;
		this.pMinimumSalesUnitQuantityTxt = ""; // can set like this because it is return parameter
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpItemNoa46());
		dest.writeString(getpLocationCode());
		dest.writeInt(getpCampaignStatus());
		dest.writeInt(getpPotentialCustomer());
		dest.writeString(getpCustomerNoa46());
		
		dest.writeDouble(getpQuantityOnSalesLine());
		dest.writeString(getpSalespersonCode());
		dest.writeInt(getpDocumentType());
		dest.writeInt(getpAvailableToWholeShip());
		dest.writeString(getpQuantityAsTxt());
		dest.writeString(getpSalesPriceRSDAsTxt());
		dest.writeString(getpSalesPriceEURAsTxt());
		dest.writeString(getpMinimumDiscountPctAsTxt());
		dest.writeString(getpMaximumDiscountPctAsTxt());
		dest.writeString(getpDiscountPctAsTxt());
		dest.writeString(getpSubstituteItemNoa46());
		dest.writeString(getpOutstandingPurchaseLinesTxt());
		dest.writeString(getpMinimumSalesUnitQuantityTxt());
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

		PropertyInfo locationCodeInfo = new PropertyInfo();
		locationCodeInfo.setName("pLocationCode");
		locationCodeInfo.setValue(pLocationCode);
		locationCodeInfo.setType(String.class);
		properies.add(locationCodeInfo);

		PropertyInfo campaignStatusInfo = new PropertyInfo();
		campaignStatusInfo.setName("pCampaignStatus");
		campaignStatusInfo.setValue(pCampaignStatus);
		campaignStatusInfo.setType(Integer.class);
		properies.add(campaignStatusInfo);

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
		quantityOnSalesLineInfo.setName("pQuantityOnSalesLine");
		quantityOnSalesLineInfo.setValue(pQuantityOnSalesLine);
		quantityOnSalesLineInfo.setType(Double.class);
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

		PropertyInfo salesPriceEURAsTxtInfo = new PropertyInfo();
		salesPriceEURAsTxtInfo.setName("pSalesPriceEURAsTxt");
		salesPriceEURAsTxtInfo.setValue(pSalesPriceEURAsTxt);
		salesPriceEURAsTxtInfo.setType(String.class);
		properies.add(salesPriceEURAsTxtInfo);

		PropertyInfo minimumDiscountPctAsTxtInfo = new PropertyInfo();
		minimumDiscountPctAsTxtInfo.setName("pMinimumDiscountPctAsTxt");
		minimumDiscountPctAsTxtInfo.setValue(pMinimumDiscountPctAsTxt);
		minimumDiscountPctAsTxtInfo.setType(String.class);
		properies.add(minimumDiscountPctAsTxtInfo);

		PropertyInfo maximumDiscountPctAsTxtInfo = new PropertyInfo();
		maximumDiscountPctAsTxtInfo.setName("pMaximumDiscountPctAsTxt");
		maximumDiscountPctAsTxtInfo.setValue(pMaximumDiscountPctAsTxt);
		maximumDiscountPctAsTxtInfo.setType(String.class);
		properies.add(maximumDiscountPctAsTxtInfo);

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
		minimumSalesUnitQuantityTxt.setName("pMinimumSalesUnitQuantityTxt");
		minimumSalesUnitQuantityTxt.setValue(pMinimumSalesUnitQuantityTxt);
		minimumSalesUnitQuantityTxt.setType(String.class);
		properies.add(minimumSalesUnitQuantityTxt);
		
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
		this.pMinimumDiscountPctAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pMinimumDiscountPctAsTxt"));
		this.pMaximumDiscountPctAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pMaximumDiscountPctAsTxt"));
		this.pQuantityAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pQuantityAsTxt"));
		this.pSalesPriceRSDAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pSalesPriceRSDAsTxt"));
		this.pSalesPriceEURAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pSalesPriceEURAsTxt"));
		this.pDiscountPctAsTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pDiscountPctAsTxt"));
		this.pSubstituteItemNoa46 = soapResponse.getPropertyAsString("pSubstituteItemNoa46");
		this.pOutstandingPurchaseLinesTxt = soapResponse.getPropertyAsString("pOutstandingPurchaseLinesTxt");
		this.pMinimumSalesUnitQuantityTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("pMinimumSalesUnitQuantityTxt"));
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

	public String getpLocationCode() {
		return pLocationCode;
	}

	public void setpLocationCode(String pLocationCode) {
		this.pLocationCode = pLocationCode;
	}

	public Integer getpCampaignStatus() {
		return pCampaignStatus;
	}

	public void setpCampaignStatus(Integer pCampaignStatus) {
		this.pCampaignStatus = pCampaignStatus;
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

	public Double getpQuantityOnSalesLine() {
		return pQuantityOnSalesLine;
	}

	public void setpQuantityOnSalesLine(Double pQuantityOnSalesLine) {
		this.pQuantityOnSalesLine = pQuantityOnSalesLine;
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

	public String getpSalesPriceEURAsTxt() {
		return pSalesPriceEURAsTxt;
	}

	public void setpSalesPriceEURAsTxt(String pSalesPriceEURAsTxt) {
		this.pSalesPriceEURAsTxt = pSalesPriceEURAsTxt;
	}

	public String getpMinimumDiscountPctAsTxt() {
		return pMinimumDiscountPctAsTxt;
	}

	public void setpMinimumDiscountPctAsTxt(String pMinimumDiscountPctAsTxt) {
		this.pMinimumDiscountPctAsTxt = pMinimumDiscountPctAsTxt;
	}

	public String getpMaximumDiscountPctAsTxt() {
		return pMaximumDiscountPctAsTxt;
	}

	public void setpMaximumDiscountPctAsTxt(String pMaximumDiscountPctAsTxt) {
		this.pMaximumDiscountPctAsTxt = pMaximumDiscountPctAsTxt;
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
		return pMinimumSalesUnitQuantityTxt;
	}

	public void setpMinimumSalesUnitQuantityTxt(String pMinimumSalesUnitQuantityTxt) {
		this.pMinimumSalesUnitQuantityTxt = pMinimumSalesUnitQuantityTxt;
	}
}
