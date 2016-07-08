package com.morganstanley.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.morganstanley.data.Weather;
import com.morganstanley.demo.dummy.DummyContent;
import com.morganstanley.tasks.WebAPITask;

/**
 * A fragment representing a single City detail screen. This fragment is either
 * contained in a {@link CityListActivity} in two-pane mode (on tablets) or a
 * {@link CityDetailActivity} on handsets.
 */
public class CityDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;
	private View currRoot;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CityDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
			WebAPITask lfmTask = new WebAPITask(CityDetailFragment.this);
	        try {
	            lfmTask.execute(mItem.id);
	        }
	        catch (Exception e)
	        {
	            lfmTask.cancel(true);
	            Toast.makeText(this.getContext(),"Unable to find track data. Try again later.",Toast.LENGTH_SHORT).show();
	        }
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_city_detail,
				container, false);
		currRoot=rootView;

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.city_detail))
			.setText(mItem.toString());
		}

		return rootView;
	}

	public void setWeather(Weather weather){
		mItem.content="Temperature = "+weather.getTemperature()+"\nWind is "+weather.getWind();
		if(currRoot!=null){
			((TextView) currRoot.findViewById(R.id.city_detail)).setText(mItem.toString());
		}
	}
}
