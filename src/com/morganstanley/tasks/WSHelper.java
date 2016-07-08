package com.morganstanley.tasks;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * Helper to interact with Weather API. 
 * 
 * @author Fadi Hatem
 *
 */
public class WSHelper {

	private static final String weatherURL = 
		"http://www.webservicex.net/globalweather.asmx/GetWeather"; 
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

		Log.d(logTag,"Fetching " + weatherURL);
		
		// create an http client and a request object.
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(weatherURL);

		try {

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("CityName", metro));
			urlParameters.add(new BasicNameValuePair("CountryName", "USA"));

			request.setEntity(new UrlEncodedFormEntity(urlParameters));
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
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			int readCount = 0;
			while ((readCount = ist.read(buff)) != -1) {
				content.write(buff, 0, readCount);
			}
			retval = new String (content.toByteArray());

		} catch (Exception e) {
			throw new ApiException("Problem connecting to the server " + 
					e.getMessage(), e);
		}

		return retval;
	}
}
