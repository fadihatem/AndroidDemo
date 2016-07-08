package com.morganstanley.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.morganstanley.data.Weather;
import com.morganstanley.demo.CityDetailFragment;

/**
 * AsyncTask for fetching data from weather API. 
 *
 * @author Fadi Hatem
 */
public class WebAPITask extends AsyncTask<String, Integer, String>
{
	private ProgressDialog progDialog;
	private CityDetailFragment activity;
	private static final String debugTag = "LastFMWebAPITask";
	
	/**
	 * Construct a task
	 * @param activity
	 */
    public WebAPITask(CityDetailFragment activity) {
		super();
		this.activity = activity;
	}

	@Override
    protected void onPreExecute() {
        super.onPreExecute(); 
    	progDialog = ProgressDialog.show(this.activity.getContext(), "Search", "Looking for weather data..." , true, false);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
        	Log.d(debugTag,"Background:" + Thread.currentThread().getName());
            String result = WSHelper.downloadFromServer(params);
            return result;
        } catch (Exception e) {
            return new String();
        }
    }
    
    @Override
    protected void onPostExecute(String result) 
    {
    	
    	Weather weather = new Weather("","");
    	
    	progDialog.dismiss();
        if (result.length() == 0) {
            Toast.makeText(this.activity.getContext(),"Unable to find track data. Try again later.",Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
			JSONObject respObj = new JSONObject(result);
			JSONObject currWeather = respObj.getJSONObject("CurrentWeather");	
			String temperature = currWeather.getString("Temperature");
			String wind = currWeather.getString("Wind");
			weather=new Weather(temperature,wind);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.activity.setWeather(weather);
             
    }
}