/*******************************************************************************
 * Copyright 2012 Steven Rudenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package rs.gopro.mobile_store.adapter;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.ui.MainActivity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.DropBoxManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Menu item decorator
 * 
 * @author aleksandar
 * 
 */
public class ActionsAdapter extends BaseAdapter {

	private static final int VIEW_TYPE_CATEGORY = 0;
	private static final int VIEW_TYPE_SETTINGS = 1;
	private static final int VIEW_TYPE_SITES = 2;
	private static final int VIEW_TYPES_COUNT = 3;

	private final LayoutInflater mInflater;

	private final String[] mTitles;
	private final String[] mUrls;
	//private final TypedArray mIcons;
	Resources res;
	View currentSelectedView;
	MainActivity mainActivity;
	
	private final Drawable iconListOption;
	private final Drawable iconListActiveOption;

	public ActionsAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		res = context.getResources();
		mTitles = res.getStringArray(R.array.actions_names);
		mUrls = res.getStringArray(R.array.actions_links);
		//mIcons = res.obtainTypedArray(R.array.actions_icons);
		mainActivity = (MainActivity) context;
		iconListOption = res.getDrawable(R.drawable.ic_list_option);
		iconListOption.setBounds(0, 0, iconListOption.getIntrinsicWidth(), iconListOption.getIntrinsicHeight());
		iconListActiveOption = res.getDrawable(R.drawable.ic_list_active_option);
		iconListActiveOption.setBounds(0, 0, iconListActiveOption.getIntrinsicWidth(), iconListActiveOption.getIntrinsicHeight());
	}

	@Override
	public int getCount() {
		return mUrls.length;
	}

	@Override
	public Uri getItem(int position) {
		return Uri.parse(mUrls[position]);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int type = getItemViewType(position);
		final ViewHolder holder;
		if (convertView == null) {
			if (type == VIEW_TYPE_CATEGORY) {
				convertView = mInflater.inflate(R.layout.list_category_item_main_menu, parent, false);
			} else {
				convertView = mInflater.inflate(R.layout.list_action_item_main_menu, parent, false);

			}
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(mTitles[position]);
		if (type != VIEW_TYPE_CATEGORY) {
			if(position == mainActivity.getCurrentItemPosition().intValue()){
				currentSelectedView = convertView;
				selectedItem(position);
			}else{
				holder.text.setCompoundDrawables(null, null, iconListOption, null);
			}
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPES_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		final Uri uri = Uri.parse(mUrls[position]);
		final String scheme = uri.getScheme();
		if ("category".equals(scheme))
			return VIEW_TYPE_CATEGORY;
		else if ("settings".equals(scheme))
			return VIEW_TYPE_SETTINGS;
		return VIEW_TYPE_SITES;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != VIEW_TYPE_CATEGORY;
	}

	private static class ViewHolder {
		TextView text;
	}

	private void selectedItem(int position) {
		ViewHolder holder = (ViewHolder) currentSelectedView.getTag();
		holder.text.setCompoundDrawables(null, null, iconListActiveOption, null);
	}

	private void unselectedItem() {
		ViewHolder holder = (ViewHolder) currentSelectedView.getTag();
		Drawable drawable = holder.text.getCompoundDrawables()[0];
		holder.text.setCompoundDrawables(drawable, null, iconListOption, null);
	}

	public void markSecletedItem(View view) {
		if (currentSelectedView == null) {
			currentSelectedView = view;
			selectedItem(mainActivity.getCurrentItemPosition());
		} else {
			unselectedItem();
			currentSelectedView = view;
			selectedItem(mainActivity.getCurrentItemPosition());

		}
	}

	public String getItemTitle(int position) {
		return mTitles[position];

	}
	

}
