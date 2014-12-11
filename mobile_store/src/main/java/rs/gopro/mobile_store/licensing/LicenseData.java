package rs.gopro.mobile_store.licensing;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class LicenseData {
	
	/**
	 * Metoda koja vraća trenutno validan serijski broj iz ugrađene liste
	 * 
	 * @return Serijski broj kao String, null ako nije pronađen
	 */
	public static String getCurrentSerial() {
		
		Calendar calendar1 = GregorianCalendar.getInstance();
		Calendar calendar2 = GregorianCalendar.getInstance();
		
		calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), 0, 0, 0, 0);
		
		for (LicenseModel lm : LicenseModel.values()) {
			
			calendar2.set(lm.getYear(), lm.getMonth() - 1, 0, 0, 0, 0);
			if (calendar1.equals(calendar2) || calendar1.before(calendar2)) {
				return lm.getSerial();
			}
		}
		return null;
	}
	
	/**
	 * Metoda koja vraća serijski broj za postavljeni mesec/godinu iz ugrađene liste
	 * 
	 * @param month primer: '12'
	 * @param year  primer: '2015'
	 * @return Serijski broj kao String, null ako nije pronađen
	 */
	public static String getSerialForMonthDate(int month, int year) {
		
		Calendar calendar1 = GregorianCalendar.getInstance();
		Calendar calendar2 = GregorianCalendar.getInstance();
		
		calendar1.set(year, month - 1, 0, 0, 0, 0);
		
		for (LicenseModel lm : LicenseModel.values()) {
			calendar2.set(lm.getYear(), lm.getMonth() - 1, 0, 0, 0, 0);
			if (calendar1.equals(calendar2)) {
				return lm.getSerial();
			}
		}
		return null;
	}

	private enum LicenseModel {

		S0		(12, 2014, "75a0cfe0-f7fc-4b48-a4d0-a9be5ad31d1c"),
		S1		(3,  2015, "cca5ab1d-4e44-4fa7-8eb1-4e3ec0596897"),
		S2		(6,  2015, "ca5b39c4-7b7a-4eff-b435-ae5c9dd5d5b4"),
		S3		(9,  2015, "ad05b31b-192d-4a2c-954d-8e01903a8657"),
		S4		(12, 2015, "cb2e99a8-7503-45f1-b025-d8368297d3b6"),
		S5		(3,  2016, "166c2c20-514a-41ba-bd66-4eb51813b2b8"),
		S6		(6,  2016, "3da9c24d-4ab4-4d7c-b782-585a2b7a6516"),
		S7		(9,  2016, "207adf4c-8e98-4ecc-9231-75ae96d58188"),
		S8		(12, 2016, "6ab7fbe2-fea0-463c-827c-7f04ca0e7835"),
		S9		(3,  2017, "a4597b8f-7ae0-4b37-9e80-4561bf2835f0"),
		S10		(6,  2017, "29b7ccc2-163f-425d-93f1-cfe8be1380ca"),
		S11		(9,  2017, "44c2922a-bfd1-455e-a362-10871d6e25d9"),
		S12		(12, 2017, "82141c2b-640e-4443-92f6-0dfdf9766815"),
		S13		(3,  2018, "38ff9f82-6a3f-48a5-92f7-a30f2ce55770"),
		S14		(6,  2018, "a2bd6eb2-0cca-41a8-830e-01a3cdc1a2dc"),
		S15		(9,  2018, "ebebef54-4de9-4ba0-a05f-aeaf8aa0c315"),
		S16		(12, 2018, "c92379b7-c501-4430-a931-6848767e86c0"),
		S17		(3,  2019, "5dbe7eb9-e69c-4ce7-b19a-ba90ac64a662"),
		S18		(6,  2019, "1db15630-e273-4d7a-a36d-b19fac0676dc"),
		S19		(9,  2019, "60b57392-36c1-47f0-96e2-ac579bbaf8fd"),
		S20		(12, 2019, "f4623d6f-4022-455e-85b4-04eee61ff1c1");

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
