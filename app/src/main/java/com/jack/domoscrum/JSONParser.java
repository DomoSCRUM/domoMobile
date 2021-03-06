package com.jack.domoscrum;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

	InputStream is = null;
	JSONObject jObj = null;
	String json = "";


	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

		// Making HTTP request
		try {
			
			// check for request method
			if(method == "POST"){
				// request method is POST
				// defaultHttpClient
				final HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
				HttpPost httpPost = new HttpPost(url);
				if(params !=null)
					httpPost.setEntity(new UrlEncodedFormEntity(params));



				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
				
			}else if(method == "GET"){
				// request method is GET
				final HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
				if(params !=null) {
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
				}
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}			
			

		} catch (UnsupportedEncodingException e) {

			//Log.e("JSON Parser", "Error UnsupportedEncodingException ", e);
			return null;
		} catch (ClientProtocolException e) {

			//Log.e("JSON Parser", "Error ClientProtocolException ", e);
			return null;
		} catch (IOException e) {

			//Log.e("JSON Parser", "Error IOException " , e);
			return null;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			if(!sb.toString().isEmpty()) {
				if (sb.toString().contains(","))
					json = "{result:" + sb.toString().replace(",", "_") + "}";
				else
					json = "{result:" + sb.toString() + "}";
			}


		} catch (Exception e) {
			//Log.e("Buffer Error", "Error converting result ", e);
			return null;
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			json="{error: Error al leer los datos}";
			try {
				jObj = new JSONObject(json);
			} catch (JSONException e1) {
				//e1.printStackTrace();
			}
			//Log.e("JSON Parser", "Error parsing data " ,e);
			return null;
		}

		// return JSON String
		return jObj;

	}
}
