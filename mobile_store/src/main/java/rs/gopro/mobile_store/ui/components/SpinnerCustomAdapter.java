package rs.gopro.mobile_store.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerCustomAdapter extends ArrayAdapter<SpinnerItem> {

	// Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<SpinnerItem> values = new ArrayList<SpinnerItem>();
	private SpinnerItem defautItem = new SpinnerItem(-1, "-1", "Izaberi adresu...");
    
    
	public SpinnerCustomAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.context = context;
        this.values.add(defautItem);
	}

	public SpinnerCustomAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
		this.values.add(defautItem);
	}

	public SpinnerCustomAdapter(Context context, int textViewResourceId,
			SpinnerItem[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
        this.values.addAll(Arrays.asList(objects));
	}

	public SpinnerCustomAdapter(Context context, int textViewResourceId,
			List<SpinnerItem> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.values.addAll(objects);
	}

	public SpinnerCustomAdapter(Context context, int resource,
			int textViewResourceId, SpinnerItem[] objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.values.addAll(Arrays.asList(objects));
	}

	public SpinnerCustomAdapter(Context context, int resource,
			int textViewResourceId, List<SpinnerItem> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.values.addAll(objects);
	}

	@Override
	public void addAll(Collection<? extends SpinnerItem> collection) {
		super.addAll(collection);
		values.addAll(collection);
	}
	
	@Override
	public void addAll(SpinnerItem... items) {
		super.addAll(items);
		Collections.addAll(values, items);
	}
	
	@Override
	public void add(SpinnerItem object) {
		super.add(object);
		values.add(object);
	}
	
	public int getCount(){
       return values.size();
    }

    public SpinnerItem getItem(int position){
       return values.get(position);
    }

    public long getItemId(int position){
       return values.get(position).getId();
    }
    
    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getDescription());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(values.get(position).getDescription());

        return label;
    }
}
