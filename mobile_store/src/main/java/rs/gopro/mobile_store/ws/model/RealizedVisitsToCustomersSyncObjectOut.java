package rs.gopro.mobile_store.ws.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class RealizedVisitsToCustomersSyncObjectOut extends RealizedVisitsToCustomersSyncObject {

	public static String TAG = "RealizedVisitsToCustomersSyncObjectOut";

	public static final Creator<RealizedVisitsToCustomersSyncObjectOut> CREATOR = new Creator<RealizedVisitsToCustomersSyncObjectOut>() {

		@Override
		public RealizedVisitsToCustomersSyncObjectOut createFromParcel(Parcel source) {
			return new RealizedVisitsToCustomersSyncObjectOut(source);
		}

		@Override
		public RealizedVisitsToCustomersSyncObjectOut[] newArray(int size) {
			return new RealizedVisitsToCustomersSyncObjectOut[size];
		}

	};

	public RealizedVisitsToCustomersSyncObjectOut() {
		super();
	}

	public RealizedVisitsToCustomersSyncObjectOut(Parcel parcel) {
		super(parcel);
	}

	public RealizedVisitsToCustomersSyncObjectOut(String cSVString, String salespersonCode, Date visitDateFrom, Date visitDateTo, String customerNoa46) {
		super(cSVString, salespersonCode, visitDateFrom, visitDateTo, customerNoa46);

	}

	@Override
	public String getTag() {
		return TAG;
	}

}
