package rs.gopro.mobile_store.ws.model;

import java.util.Date;

import android.os.Parcel;

public class PlannedVisitsToCustomersSyncObjectIn extends PlannedVisitsToCustomersSyncObject {
	public static String TAG = "PlannedVisitsToCustomersSyncObjectIn";

	public static final Creator<PlannedVisitsToCustomersSyncObjectIn> CREATOR = new Creator<PlannedVisitsToCustomersSyncObjectIn>() {

		@Override
		public PlannedVisitsToCustomersSyncObjectIn createFromParcel(Parcel source) {
			return new PlannedVisitsToCustomersSyncObjectIn(source);
		}

		@Override
		public PlannedVisitsToCustomersSyncObjectIn[] newArray(int size) {
			return new PlannedVisitsToCustomersSyncObjectIn[size];
		}

	};

	public PlannedVisitsToCustomersSyncObjectIn() {
		super();
	}

	public PlannedVisitsToCustomersSyncObjectIn(Parcel parcel) {
		super(parcel);
	}

	public PlannedVisitsToCustomersSyncObjectIn(String cSVString, String salespersonCode, Date visitDateFrom, Date visitDateTo, String customerNoa46, Integer pPotentialCustomer, Integer pPendingSynchronization) {
		super(cSVString, salespersonCode, visitDateFrom, visitDateTo, customerNoa46, pPotentialCustomer, pPendingSynchronization);
	}

	@Override
	public String getTag() {
		return TAG;
	}

}
