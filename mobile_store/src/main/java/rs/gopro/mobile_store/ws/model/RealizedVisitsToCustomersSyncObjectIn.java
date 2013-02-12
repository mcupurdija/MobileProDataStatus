package rs.gopro.mobile_store.ws.model;
import java.util.Date;

import android.os.Parcel;

public class RealizedVisitsToCustomersSyncObjectIn extends RealizedVisitsToCustomersSyncObject {
	public static String TAG = "RealizedVisitsToCustomersSyncObjectIn";

	
	public static final Creator<RealizedVisitsToCustomersSyncObjectIn> CREATOR = new Creator<RealizedVisitsToCustomersSyncObjectIn>() {

		@Override
		public RealizedVisitsToCustomersSyncObjectIn createFromParcel(Parcel source) {
			return new RealizedVisitsToCustomersSyncObjectIn(source);
		}

		@Override
		public RealizedVisitsToCustomersSyncObjectIn[] newArray(int size) {
			return new RealizedVisitsToCustomersSyncObjectIn[size];
		}

	};

	
	
	public RealizedVisitsToCustomersSyncObjectIn() {
		super();
	}

	public RealizedVisitsToCustomersSyncObjectIn(Parcel parcel) {
		super(parcel);
	}

	public RealizedVisitsToCustomersSyncObjectIn(String cSVString, String salespersonCode, Date visitDateFrom, Date visitDateTo, String customerNoa46) {
		super(cSVString, salespersonCode, visitDateFrom, visitDateTo, customerNoa46);
	}

	
	
	
	
	
	
	
	
	
	
	@Override
	public String getTag() {
		return TAG;
	}

}
