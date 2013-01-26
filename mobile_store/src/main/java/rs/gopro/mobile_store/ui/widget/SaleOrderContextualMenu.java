package rs.gopro.mobile_store.ui.widget;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.ui.SaleOrdersPreviewActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 * @author Administrator
 * Create Callback methods for contextual action bar
 */
public class SaleOrderContextualMenu implements ActionMode.Callback {
	String saleOrderId;
	Activity activity;
	
	public SaleOrderContextualMenu(Activity activity, String visitId) {
		this.saleOrderId = visitId;
		this.activity = activity;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.deactivate_sale_order_header:
			// TODO not here this code!!!!! or if here at least must be async
			activity.getContentResolver()
					.delete(MobileStoreContract.SaleOrders
							.buildSaleOrderUri(saleOrderId),
							null, null);
			mode.finish();
			return true;
		case R.id.edit_sale_order_header:
			Intent editIntent = new Intent(Intent.ACTION_EDIT, MobileStoreContract.SaleOrders.buildSaleOrderUri(saleOrderId));
			//intent.putExtra(SaleOrderAddEditActivity.EXTRA_CUSTOMER_ID, saleOrderId);
//			activity.startActivityForResult(editIntent, SaleOrdersPreviewActivity.CALL_EDIT);
			activity.startActivity(editIntent);
			mode.finish();
			//activity.finish();
			return true;
		case R.id.view_sale_order_header:
			Intent viewIntent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.SaleOrders.buildSaleOrderUri(saleOrderId));
			//intent.putExtra(SaleOrderAddEditActivity.EXTRA_CUSTOMER_ID, saleOrderId);
			activity.startActivity(viewIntent);
			mode.finish();
			//activity.finish();
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		 // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.sale_order_preview_contextual_menu, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}
	
}