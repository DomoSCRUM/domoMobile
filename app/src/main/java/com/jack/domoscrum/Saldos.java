package com.jack.domoscrum;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class Saldos extends ListActivity {

	
	private ProgressDialog pDialog;	
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> itemList;	
	public static String IP = "10.0.2.2";
	private static final String url_busqueda = "http://"+IP+"/xtreme/busqueda.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_empleados = "items";
	private static final String TAG_DESCRIPCION = "descripcion";
	private static final String TAG_CANTIDAD = "sdototal";
	JSONArray saldos = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saldos);

		// Hashmap for ListView
		itemList = new ArrayList<HashMap<String, String>>();

		// Loading empleados in Background Thread
		new LoadSaldos().execute();
	}



	/**
	 * Background Async Task to Load all Empleado by making HTTP Request
	 * */
	class LoadSaldos extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Saldos.this);
			pDialog.setMessage("Cargando Productos. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All empleados from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_busqueda, "POST", params);
			
			// Check your log cat for JSON reponse
			Log.d("Saldos: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// empleados found
					// Getting Array of empleados
					saldos = json.getJSONArray(TAG_empleados);

					// looping through All empleados
					for (int i = 0; i < saldos.length(); i++) {
						JSONObject c = saldos.getJSONObject(i);

						// Storing each json item in variable
						String cedula = c.getString(TAG_DESCRIPCION);
						String nombre = c.getString(TAG_CANTIDAD);	
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_DESCRIPCION, cedula);
						map.put(TAG_CANTIDAD, nombre);
						// adding HashList to ArrayList
						itemList.add(map);
					}
				} else {
					
					System.err.println("Error x aqui");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all empleados
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							Saldos.this, itemList,
							R.layout.list_item, new String[] { TAG_DESCRIPCION,
									TAG_CANTIDAD },
							new int[] { R.id.cedula, R.id.nombre });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}


}
