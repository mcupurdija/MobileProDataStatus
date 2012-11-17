package rs.gopro.mobile_store.ui;

import rs.gopro.mobile_store.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FirstSectionFragment extends ListFragment {

	String android_versions[] = new String[] { "Jelly Bean", "IceCream Sandwich", "HoneyComb", "Ginger Bread", "Froyo" };

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("****PROSAO");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, android_versions);
		setListAdapter(adapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	//getListView().setBackgroundResource(R.color.error_color);
		super.onActivityCreated(savedInstanceState);
	}
	
	 @Override
	    public void onStart() {
	        super.onStart();
	        /** Setting the multiselect choice mode for the listview */
	       // getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    }
}
