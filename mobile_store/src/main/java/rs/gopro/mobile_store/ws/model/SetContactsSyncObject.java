package rs.gopro.mobile_store.ws.model;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.csv.CSVDomainWriter;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Parcel;
import au.com.bytecode.opencsv.CSVWriter;

public class SetContactsSyncObject extends SyncObject {

	public static String TAG = "SetContactsSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.SET_CONTACTS_SYNC_ACTION";

	private String pCSVString;
	private int contactId;
	
	public static final Creator<SetContactsSyncObject> CREATOR = new Creator<SetContactsSyncObject>() {
		
		@Override
		public SetContactsSyncObject[] newArray(int size) {
			return new SetContactsSyncObject[size];
		}
		
		@Override
		public SetContactsSyncObject createFromParcel(Parcel source) {
			return new SetContactsSyncObject(source);
		}
	};

	public SetContactsSyncObject() {
		super();
	}

	public SetContactsSyncObject(int contactId) {
		super();
		this.contactId = contactId;
	}

	public SetContactsSyncObject(Parcel source) {
		super(source);
		setContactId(source.readInt());
		setpCSVString(source.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeInt(getContactId());
		dest.writeString(getpCSVString());
	}

	@Override
	public String getWebMethodName() {
		return "SetContacts";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();

		setpCSVString(createHeader());
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("pCSVString");
		cSVStringInfo.setValue(pCSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);

		return properties;
	}
	
	private String createHeader() {
		Cursor cursorHeader = context.getContentResolver().query(MobileStoreContract.Contacts.buildContactsExport(), ContactsQuery.PROJECTION, Tables.CONTACTS + "._id=?", new String[] { String.valueOf(contactId) }, null);
		List<String[]> header = CSVDomainWriter.parseCursor(cursorHeader, ContactsQuery.PROJECTION_TYPE);
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter, ';', '"');
		writer.writeAll(header);
		try {
			writer.close();
		} catch (IOException e) {
			writer = null;
		}
		return stringWriter.toString();
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}
	
	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	private interface ContactsQuery {
		String[] PROJECTION = {
				MobileStoreContract.Contacts.CONTACT_NO,
				MobileStoreContract.Contacts.NAME,
				MobileStoreContract.Contacts.NAME2,
				MobileStoreContract.Contacts.COMPANY_NO,
				MobileStoreContract.Contacts.PHONE,
				MobileStoreContract.SalesPerson.SALE_PERSON_NO,
				MobileStoreContract.Contacts.EMAIL
        };
		
		Type[] PROJECTION_TYPE = {
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class
		};
	}

}
