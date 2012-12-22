package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomersColumns;
import rs.gopro.mobile_store.ui.fragment.CustomerFragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;

public class AddVisitActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

	private static final String CUSTOMER_TEXT = "customer_text";
	private AutoCompleteTextView salePersionAutocomplete;
	private AutoCompleteTextView customerAutoComplete;
	SimpleCursorAdapter customerCursorAdapter;
	SimpleCursorAdapter salePersonCusrosAdapter;

	static String[] customer_projection = new String[] { MobileStoreContract.Customers._ID, MobileStoreContract.Customers.NAME };
	static String[] sale_projection = new String[] { MobileStoreContract.SaleOrders._ID, MobileStoreContract.SaleOrders.SALES_ORDER_NO };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportLoaderManager().initLoader(1, null, this);
		getSupportLoaderManager().initLoader(2, null, this);

		setContentView(R.layout.activity_add_visit);
		salePersionAutocomplete = (AutoCompleteTextView) findViewById(R.id.sale_person_autocomplete);
		customerAutoComplete = (AutoCompleteTextView) findViewById(R.id.customer_autocomplete);
		customerCursorAdapter = new CustomerSimpleCusrsorAdapter(this, android.R.layout.simple_dropdown_item_1line, null, customer_projection, new int[] { android.R.id.empty, android.R.id.text1 }, 0);

		customerCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {

				Cursor cursor = null;
				if (getContentResolver() != null) {
					cursor = getContentResolver().query(Customers.buildCustomSearchUri(constraint.toString(), "0"), customer_projection, null, null, null);
				}
				return cursor;
			}
		});
		customerAutoComplete.setAdapter(customerCursorAdapter);

		salePersonCusrosAdapter = new SalePersonCustomSimpleCurorAdapter(this, android.R.layout.simple_dropdown_item_1line, null, sale_projection, new int[] { android.R.id.empty, android.R.id.text1 }, 0);
		salePersonCusrosAdapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence constraint) {
				Cursor cursor = null;
				if (getContentResolver() != null) {
					cursor = getContentResolver().query(MobileStoreContract.SaleOrders.buildCustomSearchUri(constraint.toString(), "1"), sale_projection, null, null, null);
				}
				return cursor;
			}
		});
		
		salePersionAutocomplete.setAdapter(salePersonCusrosAdapter);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		salePersionAutocomplete.addTextChangedListener(new SalePersonCustomTextWathcer());
		customerAutoComplete.addTextChangedListener(new CustomTextWathcer());
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.plan_of_visits, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case 1:
			System.out.println("Swap firts loader");
			break;
		case 2:
			System.out.println("Swap second loader");
		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	class CustomTextWathcer implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String queryString = s.toString();
			System.out.println(queryString);
			customerCursorAdapter.getFilter().filter(queryString);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}
	}

	class CustomerSimpleCusrsorAdapter extends SimpleCursorAdapter {

		public CustomerSimpleCusrsorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			final int columnIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME);
			final String str = cursor.getString(columnIndex);
			return str;

		}

	}

	class SalePersonCustomTextWathcer implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
			String queryString = s.toString();
			salePersonCusrosAdapter.getFilter().filter(queryString);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}
	}

	class SalePersonCustomSimpleCurorAdapter extends SimpleCursorAdapter {

		public SalePersonCustomSimpleCurorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			final int columnIndex = cursor.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_NO);
			final String str = cursor.getString(columnIndex);
			return str;

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.submit_visit_form:
			submitForm();
		}
		return super.onOptionsItemSelected(item);
	}

	private void submitForm() {
		Object object = customerAutoComplete.getAdapter().getItem(0);
		Cursor cursor = (Cursor) object;
		Integer iden = cursor.getInt(cursor.getColumnIndexOrThrow(MobileStoreContract.Customers._ID));
		String sss = cursor.getString(cursor.getColumnIndexOrThrow(MobileStoreContract.Customers.NAME));
		System.out.println("id je : " + iden + " ime je :" + sss);
		
		Cursor cursor2 = (Cursor) salePersionAutocomplete.getAdapter().getItem(0);
		Integer id = cursor2.getInt(cursor2.getColumnIndexOrThrow(MobileStoreContract.SaleOrders._ID));
		String no = cursor2.getString(cursor2.getColumnIndexOrThrow(MobileStoreContract.SaleOrders.SALES_ORDER_NO));
		System.out.println("SALE : "+ id +" number je "+no);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
