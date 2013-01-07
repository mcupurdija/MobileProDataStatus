package rs.gopro.mobile_store.ws.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.csv.CSVDomainReader;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;

public class ItemsSyncObject extends SyncObject {
	private static String TAG = "ItemsSyncObject";
	
	private String mCSVString;
	private String mItemNoa46;
	private Integer mOverstockAndCampaignOnly;
	private String mSalespersonCode;
	private Date mDateModified;
	
	
	public static final Creator<ItemsSyncObject> CREATOR = new Creator<ItemsSyncObject>() {
		
		@Override
		public ItemsSyncObject createFromParcel(Parcel source) {
			return new ItemsSyncObject(source);
		}

		@Override
		public ItemsSyncObject[] newArray(int size) {
			return new ItemsSyncObject[size];
		}
		
	};
	
	public ItemsSyncObject() {
		super();
	}
	
	public ItemsSyncObject(Parcel source) {
		// there is reading of properties here
		super(source);
		
		setmCSVString(source.readString());
		setmItemNoa46(source.readString());
		setmOverstockAndCampaignOnly(source.readInt());
		setmSalespersonCode(source.readString());
		setmDateModified(new Date(source.readLong()));
		
	}
	
	public ItemsSyncObject(String mCSVString, String mItemNoa46,Integer mOverstockAndCampaignOnly, String mSalespersonCode,
			Date mDateModified) {
		super();
		this.mCSVString = mCSVString;
		this.mItemNoa46 = mItemNoa46;
		this.mDateModified = mDateModified;
		this.mOverstockAndCampaignOnly = mOverstockAndCampaignOnly;
		this.mSalespersonCode = mSalespersonCode;
	}

	@Override
	public String getWebMethodName() {
		return "GetItems";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>(); 
		
		PropertyInfo pCSVString = new PropertyInfo();
		pCSVString.setName("pCSVString");
		pCSVString.setValue(mCSVString);
		pCSVString.setType(String.class);
		properies.add(pCSVString);
	  
		PropertyInfo pItemNoa46 = new PropertyInfo();
		pItemNoa46.setName("pItemNoa46");
		pItemNoa46.setValue(mItemNoa46);
		pItemNoa46.setType(String.class);
		properies.add(pItemNoa46);
	  
		PropertyInfo pOverstockAndCampaignOnly = new PropertyInfo();
	    pOverstockAndCampaignOnly.setName("pOverstockAndCampaignOnly");
	    pOverstockAndCampaignOnly.setValue(mOverstockAndCampaignOnly);
	    pOverstockAndCampaignOnly.setType(Integer.class);
	    properies.add(pOverstockAndCampaignOnly);
	    
	    PropertyInfo pSalespersonCode = new PropertyInfo();
	    pSalespersonCode.setName("pSalespersonCode");
	    pSalespersonCode.setValue(mSalespersonCode);
	    pSalespersonCode.setType(String.class);
	    properies.add(pSalespersonCode);
		
	    PropertyInfo pDateModified = new PropertyInfo();
	    pDateModified.setName("pDateModified");
	    pDateModified.setValue(mDateModified);
	    pDateModified.setType(Date.class);
	    properies.add(pDateModified);

	    return properies;
	}
	
	@Override
	public void logSyncStart(ContentResolver contentResolver) {
		//MobileStoreContract.Items.
		//contentResolver.insert(null, null);
	}
	

	@Override
	public void saveSOAPResponse(Object response, ContentResolver contentResolver) throws SOAPResponseException {
		if (response instanceof SoapPrimitive) {
			SoapPrimitive soapresult = (SoapPrimitive)response;
			
			int inserted = 0;
			try {
				inserted = parseAndSave(contentResolver, soapresult);
			} catch (CSVParseException e) {
				throw new SOAPResponseException(e);
			}
			result = String.valueOf(inserted);
			LogUtils.LOGI(TAG, "New Items inserted:"+inserted);
		} else if (response instanceof Vector<?>) {
			Vector<SoapPrimitive> result = (Vector<SoapPrimitive>) response;
			for (SoapPrimitive primitive : result) {
				LogUtils.LOGI(TAG, primitive.toString());
			}
			//LogUtils.LOGI(TAG, result.toString());
		} else if (response instanceof SoapFault) {
			SoapFault result = (SoapFault)response;
			LogUtils.LOGE(TAG, result.faultstring);
			throw new SOAPResponseException(result.getMessage());
		}  else if (response instanceof SoapFault12) {
			SoapFault12 result = (SoapFault12)response;
			LogUtils.LOGE(TAG, result.faultstring);
			throw new SOAPResponseException(result.getMessage());
		}
		
		
	}

	private int parseAndSave(ContentResolver contentResolver, SoapPrimitive result) throws CSVParseException {
		List<ItemsDomain> parsedItems = CSVDomainReader.parse(new StringReader(result.toString()), ItemsDomain.class);
		
		List<ContentValues> valuesForDb = new ArrayList<ContentValues>();
		for (ItemsDomain item : parsedItems) {
			valuesForDb.add(item.getContentValues());
		}
		
		ContentValues[] valuesForInsert = valuesForDb.toArray(new ContentValues[valuesForDb.size()]);
		
		int numOfInserted = contentResolver.bulkInsert(MobileStoreContract.Items.CONTENT_URI, valuesForInsert);
		
		return numOfInserted;
	}

	@Override
	public void logSyncEnd(ContentResolver contentResolver) {
		// TODO Auto-generated method stub
		LogUtils.LOGI(TAG, "Sync of items finished.");
	}

	public String getmCSVString() {
		return mCSVString;
	}

	public void setmCSVString(String mCSVString) {
		this.mCSVString = mCSVString;
	}

	public String getmItemNoa46() {
		return mItemNoa46;
	}

	public void setmItemNoa46(String mItemNoa46) {
		this.mItemNoa46 = mItemNoa46;
	}

	public Date getmDateModified() {
		return mDateModified;
	}

	public void setmDateModified(Date mDateModified) {
		this.mDateModified = mDateModified;
	}

	public Integer getmOverstockAndCampaignOnly() {
		return mOverstockAndCampaignOnly;
	}

	public void setmOverstockAndCampaignOnly(Integer mOverstockAndCampaignOnly) {
		this.mOverstockAndCampaignOnly = mOverstockAndCampaignOnly;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getCompany());
		dest.writeString(getStatusMessage());
		dest.writeString(getmCSVString());
		dest.writeString(getmItemNoa46());
		dest.writeInt(getmOverstockAndCampaignOnly());
		dest.writeString(getmSalespersonCode());
		dest.writeLong(getmDateModified().getTime());
		
	}

	public String getmSalespersonCode() {
		return mSalespersonCode;
	}

	public void setmSalespersonCode(String mSalespersonCode) {
		this.mSalespersonCode = mSalespersonCode;
	}
}
