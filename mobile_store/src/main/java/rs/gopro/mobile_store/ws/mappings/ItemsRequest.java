package rs.gopro.mobile_store.ws.mappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class ItemsRequest implements KvmSerializable {

	private String pCSVString;
	private String pItemNoa46;
	private String pDateModified;
	private Integer pCampaignStatus;

	public ItemsRequest() {
		super();
	}

	public ItemsRequest(String pCSVString, String pItemNoa46,
			String pDateModified, Integer pCampaignStatus) {
		super();
		this.pCSVString = pCSVString;
		this.pItemNoa46 = pItemNoa46;
		this.pDateModified = pDateModified;
		this.pCampaignStatus = pCampaignStatus;
	}

	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:
			return pCSVString;
		case 1:
			return pItemNoa46;
		case 2:
			return pDateModified;
		case 3:
			return pCampaignStatus;
		}

		return null;
	}

	@Override
	public int getPropertyCount() {
		return 4;
	}

	@Override
	public void setProperty(int index, Object value) {
		switch(index)
        {
        case 0:
        	pCSVString = value.toString();
            break;
        case 1:
        	pItemNoa46 = value.toString();
            break;
        case 2:
        	pDateModified = value.toString();
            break;
        case 3:
        	pCampaignStatus = Integer.parseInt(value.toString());
            break;
        default:
            break;
        }
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
		switch (index) {
		case 0:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "pCSVString";
			break;
		case 1:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "pItemNoa46";
			break;
		case 2:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "pDateModified";
			break;
		case 3:
			info.type = PropertyInfo.INTEGER_CLASS;
			info.name = "pCampaignStatus";
			break;
		default:
			break;
		}
	}
	
	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

	public String getpItemNoa46() {
		return pItemNoa46;
	}

	public void setpItemNoa46(String pItemNoa46) {
		this.pItemNoa46 = pItemNoa46;
	}

	public String getpDateModified() {
		return pDateModified;
	}

	public void setpDateModified(String pDateModified) {
		this.pDateModified = pDateModified;
	}

	public Integer getpCampaignStatus() {
		return pCampaignStatus;
	}

	public void setpCampaignStatus(Integer pCampaignStatus) {
		this.pCampaignStatus = pCampaignStatus;
	}
}
