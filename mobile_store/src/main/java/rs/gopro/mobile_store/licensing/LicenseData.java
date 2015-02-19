package rs.gopro.mobile_store.licensing;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;


public class LicenseData {
	
	/**
	 * Metoda koja vraća trenutno validan serijski broj iz ugrađene liste
	 * 
	 * @return Serijski broj kao String, null ako nije pronađen
	 */
	public static String getCurrentSerial() {
		
		DateTime d1 = new DateTime(), d2;
		
        for (LicenseModel lm : LicenseModel.values()) {

        	d2 = new DateTime(lm.getYear(), lm.getMonth(), 1, 0, 0);
			
        	if (DateTimeComparator.getDateOnlyInstance().compare(d1, d2) == -1) {
        		return lm.getSerial();
			}
		}
		return null;
	}

	private enum LicenseModel {

		S0		(12,	2014,	"b8bbf2fb-eecb-470d-a0e0-d079f02d9fa0"),
		S1		(3,		2015,	"6c9130c4-5fd1-47cb-9a24-5bfa609c8084"),
		S2		(6,		2015,	"12764954-adfc-4e4e-be8f-6811b207c37d"),
		S3		(9,		2015,	"961c426c-43ec-4ff0-ac01-9787bd0f9d0f"),
		S4		(12,	2015,	"8bfc67f5-703c-4b60-ae63-531da2b329b2"),
		S5		(3,		2016,	"c47524dc-9a1f-4d12-a5fd-b883a1b1fc40"),
		S6		(6,		2016,	"625dbd76-6232-4351-b50a-d3e9f176c20d"),
		S7		(9,		2016,	"ef363cb5-e58f-487c-a96f-6074aa1dd192"),
		S8		(12,	2016,	"0a1e39f9-3eee-43e6-9d46-9df9dd4242f7"),
		S9		(3,		2017,	"9db4d369-f59c-4c98-9d07-5bd60b740d14"),
		S10		(6,		2017,	"9c85f6da-797e-48f3-b338-8c16fc5b17ba"),
		S11		(9,		2017,	"ea7370f1-b7a2-4b77-993d-4c16111ac0f5"),
		S12		(12,	2017,	"b046c6e1-e654-4130-854b-8259df758f87"),
		S13		(3,		2018,	"4d7d538b-9d46-4626-9567-999bbe569059"),
		S14		(6,		2018,	"d80a91c6-8866-44f9-b2ad-dc4220fa9a91"),
		S15		(9,		2018,	"fa2515d5-599d-4afe-a19e-2a2c4a754401"),
		S16		(12,	2018,	"f8ff385e-b764-4d21-a079-f9ce9f84062e"),
		S17		(3,		2019,	"42209734-caf0-467d-932d-8e8c3bbb985c"),
		S18		(6,		2019,	"698ceec4-61fe-43af-971c-d01167dff5e0"),
		S19		(9,		2019,	"23a64a11-d890-422f-a20d-4c82f5c870bd"),
		S20		(12,	2019,	"f06a2721-98ee-456b-8071-94357d1d2ec5"),
		S21		(3,		2020,	"3bdf4531-8000-440a-b6aa-a2f6216ebf58"),
		S22		(6,		2020,	"865e28fe-e1ce-4e9f-a395-b375c7b63b74"),
		S23		(9,		2020,	"22daaf7c-8085-4971-80e0-6a6e0fc2151c"),
		S24		(12,	2020,	"02f6492c-20a0-4d17-b69a-9f4211f7303c"),
		S25		(3,		2021,	"de24477f-42cf-49da-a380-75a7ee277f73"),
		S26		(6,		2021,	"ffe7eba0-36e3-47ae-b171-d069f2682fe1"),
		S27		(9,		2021,	"be577f98-8c0b-45b7-a811-7807042121f2"),
		S28		(12,	2021,	"2cb4f11e-2172-4207-9d96-372659d159d0"),
		S29		(3,		2022,	"22c7f086-8977-4554-a538-095299c282aa");

		private LicenseModel(int cMonth, int cYear, String cSerial) {
			month = cMonth;
			year = cYear;
			serial = cSerial;
		}

		public int getMonth() {
			return month;
		}

		public int getYear() {
			return year;
		}

		public String getSerial() {
			return serial;
		}

		private int month;
		private int year;
		private String serial;

	}

}
