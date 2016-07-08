package com.morganstanley.tasks;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.DocumentsContract.Document;
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
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	org.w3c.dom.Document doc = dBuilder.parse(result);
        			
        	//optional, but recommended
        	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        	doc.getDocumentElement().normalize();

        	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        			
        	NodeList nList = doc.getElementsByTagName("CurrentWeather");

        	for (int temp = 0; temp < nList.getLength(); temp++) {

        		Node nNode = nList.item(temp);
        				
        		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

        			Element eElement = (Element) nNode;
        			String temperature =  eElement.getElementsByTagName("Temperature").item(0).getTextContent();
        			String wind = eElement.getElementsByTagName("Wind").item(0).getTextContent();
        			weather=new Weather(temperature,wind);
        		}
        	}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.activity.setWeather(weather);
             
    }
}