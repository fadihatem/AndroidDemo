package com.morganstanley.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.morganstanley.data.Weather;

import android.util.Log;

/**
 * Helper to interact with Weather API. 
 * 
 * @author Fadi Hatem
 *
 */
public class WSHelper {

	private static final String weatherURL = 
		"http://www.webservicex.net/globalweather.asmx/GetWeather?CityName="; 
	private static final int HTTP_STATUS_OK = 200;
	private static byte[] buff = new byte[1024];
	private static final String logTag = WSHelper.class.getName();

	public static class ApiException extends Exception {
		private static final long serialVersionUID = 1L;

		public ApiException (String msg)
		{
			super (msg);
		}

		public ApiException (String msg, Throwable thr)
		{
			super (msg, thr);
		}
	}

	/**
	 * download weather data.
	 * @param params search strings
	 * @return Array of json strings returned by the API. 
	 * @throws ApiException
	 */
	protected static synchronized String downloadFromServer (String... params)
	throws ApiException
	{
		String retval = null;
		String metro = params[0]; 


		String url = weatherURL 
		+ metro +"&CountryName=Canada"; 

		Log.d(logTag,"Fetching " + url);
		
		// create an http client and a request object.
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		try {

			// execute the request
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != HTTP_STATUS_OK) {
				// handle error here
				throw new ApiException("Invalid response from weather webservice" + 
						status.toString());
			}

			// process the content. 
			HttpEntity entity = response.getEntity();
			InputStream ist = entity.getContent();
			try {
	        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        	org.w3c.dom.Document doc = dBuilder.parse(ist);
	        			
	        	//optional, but recommended
	        	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	        	doc.getDocumentElement().normalize();

	        	Node nNodeFirst= doc.getElementsByTagName("CurrentWeather").item(0);
	        				
        		if (nNodeFirst.getNodeType() == Node.ELEMENT_NODE) {

        			Element eElement = (Element) nNodeFirst;
        			String temperature =  eElement.getElementsByTagName("Temperature").item(0).getTextContent();
        			String wind = eElement.getElementsByTagName("Wind").item(0).getTextContent();
        			Weather weather=new Weather(temperature,wind);
        			retval="Temperature = "+weather.getTemperature()+"\nWind is "+weather.getWind();
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

		} catch (Exception e) {
			throw new ApiException("Problem connecting to the server " + 
					e.getMessage(), e);
		}

		return retval;
	}
}
