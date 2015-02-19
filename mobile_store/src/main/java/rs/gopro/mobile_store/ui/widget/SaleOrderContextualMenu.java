package rs.gopro.mobile_store.ui.widget;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.DialogUtil;
import rs.gopro.mobile_store.util.UIUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
			Cursor cursorStatus = activity.getContentResolver().query(MobileStoreContract.SaleOrders.CONTENT_URI, new String[] {SaleOrders.SALES_ORDER_DEVICE_NO, SaleOrders.SALES_ORDER_NO}, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { saleOrderId }, null);
			if (cursorStatus.moveToFirst()) {
				if (cursorStatus.isNull(1)) {
					activity.startActivity(editIntent);
				} else {
					DialogUtil.showInfoDialog(activity, "Poruka", activity.getString(R.string.dokumentPoslatError));
				}
			}
			if (cursorStatus != null && !cursorStatus.isClosed()) {
				cursorStatus.close();
			}
			mode.finish();
			return true;
		case R.id.view_sale_order_header:
			Intent viewIntent = new Intent(Intent.ACTION_VIEW, MobileStoreContract.SaleOrders.buildSaleOrderUri(saleOrderId));
			//intent.putExtra(SaleOrderAddEditActivity.EXTRA_CUSTOMER_ID, saleOrderId);
			activity.startActivity(viewIntent);
			mode.finish();
			//activity.finish();
			return true;
		case R.id.view_sale_order_saldo:
			//activity.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
			String quantity = MobileStoreContract.SaleOrderLines.QUANTITY;
			String price = MobileStoreContract.SaleOrderLines.PRICE;
			String discount = MobileStoreContract.SaleOrderLines.REAL_DISCOUNT;
			String vat_rate = MobileStoreContract.SaleOrderLines.VAT_RATE;
			
			double saldo = 0d;
			double total = 0d;
			double pdv = 0d;
			
			String[] projection = new String[] { "sum(" + quantity + "*(" + price + "-(" + price + "*(" + discount + "/100))))" };
			String[] projection_with_pdv = new String[] { "round(sum((" + quantity + "*(" + price + "-(" + price + "*(" + discount + "/100))))*(1+" + vat_rate + "/100)), 2)" };
			Cursor cursor = activity.getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderSaldo(), projection, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { saleOrderId }, null);
			if (cursor.moveToFirst()) {
				saldo = cursor.getDouble(0);
			}
			cursor = activity.getContentResolver().query(MobileStoreContract.SaleOrders.buildSaleOrderSaldo(), projection_with_pdv, Tables.SALE_ORDERS + "." + MobileStoreContract.SaleOrders._ID + "=?", new String[] { saleOrderId }, null);
			if (cursor.moveToFirst()) {
				total = cursor.getDouble(0);
				pdv = total - saldo;
			}
			cursor.close();
			
//			if (cursor.moveToFirst()) {
//				saldo = cursor.getDouble(0);
//				pdv = 0.2*saldo;
//				total = saldo+pdv;
//			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage("Iznos bez pdv: " + UIUtils.formatDoubleForUI(saldo) + "\n"
					+ "PDV: " + UIUtils.formatDoubleForUI(pdv) + "\n"
					+ "Ukupno: " + UIUtils.formatDoubleForUI(total))
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                //do things
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
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