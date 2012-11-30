package rs.gopro.mobile_store.ui;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ItemsListFragment extends ListFragment {
	
	String items[] = new String[] { "The Shawshank Redemption", "The Godfather", "The Godfather: Part II", "Pulp Fiction", "The Good, the Bad and the Ugly", "The Dark Knight", "Schindler's List" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, items);
		setListAdapter(adapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
