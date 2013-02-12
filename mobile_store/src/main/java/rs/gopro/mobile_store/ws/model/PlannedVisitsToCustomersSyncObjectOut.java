package rs.gopro.mobile_store.ws.model;

import java.util.Date;

import android.os.Parcel;

public class PlannedVisitsToCustomersSyncObjectOut extends PlannedVisitsToCustomersSyncObject {
	public static String TAG = "PlannedVisitsToCustomersSyncObjectOut";

	public static final Creator<PlannedVisitsToCustomersSyncObjectOut> CREATOR = new Creator<PlannedVisitsToCustomersSyncObjectOut>() {

		@Override
		public PlannedVisitsToCustomersSyncObjectOut createFromParcel(Parcel source) {
			return new PlannedVisitsToCustomersSyncObjectOut(source);
		}

		@Override
		public PlannedVisitsToCustomersSyncObjectOut[] newArray(int size) {
			return new PlannedVisitsToCustomersSyncObjectOut[size];
		}

	};

	public PlannedVisitsToCustomersSyncObjectOut() {
		super();
	}

	public PlannedVisitsToCustomersSyncObjectOut(Parcel parcel) {
		super(parcel);
	}

	public PlannedVisitsToCustomersSyncObjectOut(String cSVString, String salespersonCode, Date visitDateFrom, Date visitDateTo, String customerNoa46) {
		super(cSVString, salespersonCode, visitDateFrom, visitDateTo, customerNoa46);
	}

	@Override
	public String getTag() {
		return TAG;
	}

}
