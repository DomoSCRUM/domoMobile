package com.jack.domoscrum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Comprar extends Activity {

	GridView gridView;
	EditText input=null;	
	private ProgressDialog pDialog;	
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> itemList;	
	ArrayList<HashMap<String, String>> compra;
	public static String IP = "10.0.2.2";
	private static final String url_busqueda = "http://" +IP+ "/xtreme/busquedaItems.php";
	private static String url_comprar = "http://" + IP+ "/xtreme/comprar.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ITEMS = "items";
	private static final String TAG_DESCRIPCION = "descripcion";
	private static final String TAG_PRECIO = "precio";
	private static final String TAG_CANTIDAD = "cantidad";
	private static final String TAG_IMAGEN = "imagen";	
	JSONArray items = null;
	String ver="";
	int acumulado=0;
	TextView precio;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comprar);
		itemList = new ArrayList<HashMap<String, String>>();
		compra = new ArrayList<HashMap<String, String>>();		
		
		gridView = (GridView) findViewById(R.id.griditem);
		
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			
			public void onItemClick(AdapterView<?> parent, final View v,int position, long id)
			{
				input = new EditText(getApplicationContext());
				input.setHint("Ingrese Cantidad");
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				
				AlertDialog dialog = new AlertDialog.Builder(Comprar.this)
				.setTitle("Carro Compras")
				.setView(input)
				.setMessage("Cuantas unidades desea Comprar")
				.setPositiveButton("OK",new DialogInterface.OnClickListener()
				{
					
					public void onClick(DialogInterface dialog,int which)
					{
						int total=0;
						if(!input.getText().toString().equals(""))
						{
						    total=Integer.parseInt(((TextView) v.findViewById(R.id.precioItem)).getText().toString())
						    		*Integer.parseInt(input.getText().toString());
						    acumulado+=total;
							ver+= ((TextView) v.findViewById(R.id.descripcion)).getText().toString()+"$"+total+"\n";
							HashMap<String, String> map = new HashMap<String, String>();							
							map.put(TAG_DESCRIPCION,((TextView) v.findViewById(R.id.descripcion)).getText().toString());
							map.put(TAG_CANTIDAD, input.getText().toString());
							map.put("fecha",calcularFecha());
							compra.add(map);
						}
					}
				}).create();
				dialog.show();
				input.setText("");
				

			}
		});
		
		new LoadItems().execute();
	}
	
	public String calcularFecha()
	{
		Calendar c = Calendar.getInstance();		
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		String formattedDate1 = df1.format(c.getTime());
		return formattedDate1;
	}
	
	public void viewCarBuy(View v){
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("Resultado")
		.setMessage("Sus Compras Son: \n"+ ver+"\n"+"El total es $ "+acumulado)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				}).create();
		dialog.show();
			
	}
	
	
	public void comprarOnline(View v)
	{
		new sendItems().execute();
	}
	
	class sendItems extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Comprar.this);
			pDialog.setMessage("Realizando Compra. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			for (int i = 0; i < compra.size(); i++)
			{
				System.out.println(compra.size());
				Iterator it = compra.get(i).entrySet().iterator();		
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				while (it.hasNext())
				{
					Map.Entry e = (Map.Entry) it.next();
						
					if (e.getValue() != null)
					{
						if (e.getKey().toString().equals(TAG_DESCRIPCION))					
							params.add(new BasicNameValuePair("descripcion", e.getValue().toString()));					
						if(e.getKey().toString().equals(TAG_CANTIDAD))
							params.add(new BasicNameValuePair("cantidad", e.getValue().toString()));
						if(e.getKey().toString().equals("fecha"))
							params.add(new BasicNameValuePair("fecha", e.getValue().toString()));
					}
				}
				JSONObject json = jParser.makeHttpRequest(url_comprar,"POST", params);
				try {
					int success = json.getInt(TAG_SUCCESS);
					if (success == 1) {		
						System.out.println("Correcto");
					}
					else
					{
						System.out.println("Error..");
					}
				}catch(Exception e){
					System.err.println(e.getMessage());
				}
						
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all empleados
			pDialog.dismiss();
		}
		
	}
	
	/**
	 * Background Async Task to Load all Empleado by making HTTP Request
	 * */
	class LoadItems extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Comprar.this);
			pDialog.setMessage("Cargando Productos. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All items from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_busqueda, "POST", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Items: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// empleados found
					// Getting Array of empleados
					items = json.getJSONArray(TAG_ITEMS);

					// looping through All empleados
					for (int i = 0; i < items.length(); i++)
					{
						JSONObject c = items.getJSONObject(i);

						// Storing each json item in variable
						String descripcion = c.getString(TAG_DESCRIPCION);
						String precio = c.getString(TAG_PRECIO);
						String imagen = c.getString(TAG_IMAGEN);
						

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_DESCRIPCION, descripcion);
						map.put(TAG_PRECIO, precio);
						map.put(TAG_IMAGEN, imagen);
						

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
					gridView.setAdapter(new MobileList(Comprar.this, itemList));
				}
			});

		}

	}


}
