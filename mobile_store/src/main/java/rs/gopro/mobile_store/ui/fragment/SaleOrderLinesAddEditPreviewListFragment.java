package rs.gopro.mobile_store.ui.fragment;

import static rs.gopro.mobile_store.util.LogUtils.makeLogTag;
import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.BaseActivity;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SaleOrderLinesAddEditPreviewListFragment extends ListFragment
		implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = makeLogTag(SaleOrderLinesAddEditPreviewListFragment.class);
	
	private static final String STATE_SELECTED_ID = "selectedId";
	
//	private static final String STANDARD_FILTER = Tables.SALE_ORDER_LINES + "." + MobileStoreContract.SaleOrderLines.SALE_ORDER_ID+"=?";
	
	private Uri mSaleOrderLinesUri;
	private CursorAdapter mAdapter;
//	private String mSelectedSaleOrderId;
	private String mSelectedSaleOrderLineId;
	private boolean mHasSetEmptyText = false;
//	private int salesPersonId = -1;
	
	public SaleOrderLinesAddEditPreviewListFragment() {
	}

	public interface Callbacks {
        /** Return true to select (activate) the vendor in the list, false otherwise. */
        public boolean onSaleOrderLineSelected(String saleOrderLineId);
        
        /** Pass selected visitId and initialize  contextual menu */
        public void onSaleOrderLineLongClick(String saleOrderLineId);
    }
	
	private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public boolean onSaleOrderLineSelected(String saleOrderLineId) {
            return true;
        }

		@Override
		public void  onSaleOrderLineLongClick(String saleOrderLineId) {
		}
    };
	
    /**
     * Dummy implementation to offer mCallbacks for other classes that are interested in this
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // don't need it
//        salesPersonId = Integer.valueOf(SharedPreferencesUtil.getSalePersonId(getActivity()));
        
        if (savedInstanceState != null) {
            mSelectedSaleOrderLineId = savedInstanceState.getString(STATE_SELECTED_ID);
        }

        reloadFromArguments(getArguments());
    }
    
    public void reloadFromArguments(Bundle arguments) {
        // Teardown from previous arguments
        setListAdapter(null);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        mSaleOrderLinesUri = intent.getData();
        if (mSaleOrderLinesUri == null) {
        	LogUtils.LOGE(TAG, "No URI, fragment will nit load.");
            return;
        }

        mAdapter = new SaleOrderLinesAdapter(getActivity());

        setListAdapter(mAdapter);

        // Start background query to load vendors
        getLoaderManager().initLoader(SaleOrderLinesQuery._TOKEN, null, this);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        final ListView listView = getListView();
        listView.setSelector(android.R.color.transparent);
        listView.setCacheColorHint(Color.WHITE);
//        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mHasSetEmptyText) {
            // Could be a bug, but calling this twice makes it become visible
            // when it shouldn't be visible.
            setEmptyText(getString(R.string.empty_sale_order_lines));
            mHasSetEmptyText = true;
        }
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSelectedSaleOrderLineId != null) {
            outState.putString(STATE_SELECTED_ID, mSelectedSaleOrderLineId);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Cursor cursor = (Cursor) mAdapter.getItem(position);     
        String saleOrderLineId = String.valueOf(cursor.getInt(SaleOrderLinesQuery._ID));
        if (mCallbacks.onSaleOrderLineSelected(saleOrderLineId)) {
            mSelectedSaleOrderLineId = saleOrderLineId;
            mAdapter.notifyDataSetChanged();
        }
    }
    
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		return new CursorLoader(getActivity(), mSaleOrderLinesUri, SaleOrderLinesQuery.PROJECTION, STANDARD_FILTER, new String[] {String.valueOf(salesPersonId)},
//				MobileStoreContract.SaleOrderLines.DEFAULT_SORT);
		return new CursorLoader(getActivity(), mSaleOrderLinesUri, SaleOrderLinesQuery.PROJECTION, null, null,
				MobileStoreContract.SaleOrderLines.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }
        
        int token = loader.getId();
        if (token == SaleOrderLinesQuery._TOKEN) {
            mAdapter.swapCursor(cursor);
        } else {
            cursor.close();
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/**
	 * Sets current position in list from caller.
	 * @param id
	 */
	public void setSelectedSalesOrderLineId(String id) {
		mSelectedSaleOrderLineId = id;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
	
    /**
     * {@link CursorAdapter} that renders a {@link SaleOrdersQuery}.
     */
    private class SaleOrderLinesAdapter extends CursorAdapter {
        public SaleOrderLinesAdapter(Context context) {
            super(context, null, false);
        }

        /** {@inheritDoc} */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(R.layout.list_item_sale_order_lines_preview,
                    parent, false);
        }

		/** {@inheritDoc} */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// UIUtils.setActivatedCompat(view,
			// cursor.getString(VisitsQuery.VENDOR_ID)
			// .equals(mSelectedVendorId));
			view.setActivated(String.valueOf(
					cursor.getInt(SaleOrderLinesQuery._ID)).equals(
					mSelectedSaleOrderLineId));

			final TextView timeView = (TextView) view
					.findViewById(R.id.block_time);
			final TextView titleView = (TextView) view
					.findViewById(R.id.block_title);
			final TextView subtitleView = (TextView) view
					.findViewById(R.id.block_subtitle);
			
			final double price = cursor.isNull(SaleOrderLinesQuery.PRICE) ? 0 : cursor.getDouble(SaleOrderLinesQuery.PRICE);
			final double quantity = cursor.isNull(SaleOrderLinesQuery.QUANTITY) ? 0 : cursor.getDouble(SaleOrderLinesQuery.QUANTITY);
			final double discount = cursor.isNull(SaleOrderLinesQuery.REAL_DISCOUNT) ? 0 : cursor.getDouble(SaleOrderLinesQuery.REAL_DISCOUNT);
			
			final double lineAmount = quantity * (price - (price * (discount/100)));
			
			String salesOrderDate = cursor
					.getString(SaleOrderLinesQuery.LINE_NO);
			String salesOrderNo = cursor.getString(SaleOrderLinesQuery.ITEM_NO)
					+ " - " + cursor.getString(SaleOrderLinesQuery.DESCRIPTION);
			String salesOrderCust = "Količina: "
					+ UIUtils.formatDoubleForUI(quantity)
					+ "  -  Iznos:"
					+ UIUtils.formatDoubleForUI(lineAmount);
			timeView.setText(salesOrderDate);
			titleView.setText(salesOrderNo);
			subtitleView.setText(salesOrderCust);
		}
	}
	
	private interface SaleOrderLinesQuery {
		int _TOKEN = 0x5;

		String[] PROJECTION = { BaseColumns._ID,
				MobileStoreContract.SaleOrderLines.SALE_ORDER_ID,
				MobileStoreContract.SaleOrderLines.ITEM_NO,
				MobileStoreContract.SaleOrderLines.DESCRIPTION,
				MobileStoreContract.SaleOrderLines.DESCRIPTION2,
				MobileStoreContract.SaleOrderLines.LINE_NO,
				MobileStoreContract.SaleOrderLines.QUANTITY,
				MobileStoreContract.SaleOrderLines.PRICE,
				MobileStoreContract.SaleOrderLines.REAL_DISCOUNT };

		int _ID = 0;
//		int SALE_ORDER_ID = 1;
		int ITEM_NO = 2;
		int DESCRIPTION = 3;
//		int DESCRIPTION2 = 4;
		int LINE_NO = 5;
		int QUANTITY = 6;
		int PRICE = 7;
		int REAL_DISCOUNT = 8;
	}
	
}
