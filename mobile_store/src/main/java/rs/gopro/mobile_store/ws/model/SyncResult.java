package rs.gopro.mobile_store.ws.model;

import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import android.os.Parcel;
import android.os.Parcelable;

public class SyncResult implements Parcelable {

	private SyncStatus status;
	private String result;

	public SyncResult() {
	}

	public SyncResult(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		try {
			setStatus(SyncStatus.valueOf(in.readString()));
		} catch (IllegalArgumentException e) {
			setStatus(null);
		}
		setResult(in.readString());

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatus() == null ? "" : getStatus().name());
		dest.writeString(getResult());

	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public SyncResult createFromParcel(Parcel in) {
			return new SyncResult(in);
		}

		public SyncResult[] newArray(int size) {
			return new SyncResult[size];
		}
	};

	public SyncStatus getStatus() {
		return status;
	}

	public void setStatus(SyncStatus status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
