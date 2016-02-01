package com.jack.domoscrum;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	// Progress Dialog
	public ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	EditText name;
	EditText pass;
	public String IP = "";
	private String url_login = "http://" + IP+ ":8080/OjosTest/Peticion";//http://190.6.160.42:8080/OjosTest/Peticion
	// JSON Node names
	private boolean success=false;
	private String TAG_STATE= "result";
	private boolean state=false;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Edit Text
		name = (EditText) findViewById(R.id.name);
		pass = (EditText) findViewById(R.id.password);
		//setea la direccion del shareprefrence

	}

	public void acceder(View view) {
		if (validateInput())
			// creating new Empleado in background thread
			new LoginAcepted().execute();
	}

    public void configure(View view) {

        configureIP();
    }

	public void configureIP() {
		final EditText inputIp = new EditText(this);
		inputIp.setKeyListener(IPAddressKeyListener.getInstance());
		inputIp.setText(IP);

		AlertDialog dialog = new AlertDialog.Builder(Login.this)
				//AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Configuracion")
				.setMessage("Por favor ingrese la  direccion IP del servidor")
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								validateIP(inputIp);
							}
						})
				.setView(inputIp)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface cancel, int which) {
								cancel.cancel();
							}
						})
				.create();
		dialog.show();
	}

	public void validateIP(EditText input) {

		if(input.getText().toString().isEmpty())
		{
			Toast customtoast=new Toast(getApplicationContext());
			LayoutInflater inflater=getLayoutInflater();
			View customToastroot =inflater.inflate(R.layout.yellow_toast, null);
			TextView msg= (TextView) customToastroot.findViewById(R.id.txtMensaje);
			msg.setText("Por favor ingrese la direccion IP");
			customtoast.setView(customToastroot);
			customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			customtoast.setDuration(Toast.LENGTH_LONG);
			customtoast.show();
		}
		else
		{
			String patternIp="^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
			Pattern pattern= Pattern.compile(patternIp);
			Matcher matcher;
			matcher = pattern.matcher(input.getText().toString());
			//valida si la ip ingresada, coincide con el formato valido.
			if (matcher.find())
			{
				SharedPreferences sharedPref = getSharedPreferences("PASA",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("ip", input.getText().toString());
				editor.apply();
				IP=input.getText().toString();
				url_login = "http://" + IP+ ":8080/OjosTest/Peticion";//http://190.6.160.42:8080/OjosTest/Peticion


			}
			else
			{
				Toast toast3 = new Toast(getApplicationContext());

				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.yellow_toast,
						(ViewGroup) findViewById(R.id.lytLayout));

				TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
				txtMsg.setText("Direccion IP Invalida");

				toast3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
				toast3.setDuration(Toast.LENGTH_LONG);
				toast3.setView(layout);
				toast3.show();

			}

		}
	}

	public boolean validateInput()
	{
		if(name.getText().toString().isEmpty()||pass.getText().toString().isEmpty())
		{

			Toast customToast = new Toast(getApplicationContext());

			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.red_toast,
					(ViewGroup) findViewById(R.id.lytLayout));

			TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
			txtMsg.setText("Por favor ingrese el usuario y/o contraseña");

			customToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			customToast.setDuration(Toast.LENGTH_LONG);
			customToast.setView(layout);
			customToast.show();

			return false;

		}
		if(IP.isEmpty())
		{
			Toast customtoast=new Toast(getApplicationContext());
			LayoutInflater inflater=getLayoutInflater();
			View customToastroot =inflater.inflate(R.layout.yellow_toast, null);
			TextView msg= (TextView) customToastroot.findViewById(R.id.txtMensaje);
			msg.setText("Por favor configure la direccion IP del servidor");
			customtoast.setView(customToastroot);
			customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
			customtoast.setDuration(Toast.LENGTH_LONG);
			customtoast.show();
			return false;


		}
		return true;

	}

	/**
	 * Background Async Task to Create new Empleado
	 * */
	class LoginAcepted extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Ingresando..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating Empleado
		 * */
		protected String doInBackground(String... args) {
			String usuarios = name.getText().toString();
			String password = pass.getText().toString();

			JSONObject json = returnParams(usuarios, password);
			// getting JSON Object
			// Note that create Empleado url accepts POST method, Comentar para pruebas
			//JSONObject json = jsonParser.makeHttpRequest(url_login,"POST", params);

			// check log cat fro response, comentar para pruebasdnf
			//Log.d("Create Response", ((json !=null) ? json.toString():"Error"));
			// check for success tag
			//Descomentar para Pruebas, comentar para produccion
			//JSONObject json= new JSONObject();

			try {
				if(json != null)
				{
					//int success = json.getInt(TAG_SUCCESS);
					state = json.getBoolean(TAG_STATE);
					success = true;
					if(state) {

						// successfully created Empleado
						SharedPreferences sharedPref = getSharedPreferences("PASA",
								MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putString("nombre", usuarios);
						editor.putBoolean("User", true);
						editor.apply();
						//finish();

						Intent i = new Intent("com.google.xtreme.MainActivity");

						i.putExtra("nameComplete", usuarios);
						startActivity(i);
						finish();
					}
					// closing this screen
				}

			} catch (JSONException e) {
				//e.printStackTrace();
				Toast customtoast=new Toast(getApplicationContext());
				LayoutInflater inflater=getLayoutInflater();
				View customToastroot =inflater.inflate(R.layout.red_toast, null);
				TextView msg= (TextView) customToastroot.findViewById(R.id.txtMensaje);
				msg.setText("Error al parsear los datos" + e.getMessage());
				customtoast.setView(customToastroot);
				customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
				customtoast.setDuration(Toast.LENGTH_LONG);
				customtoast.show();
				//Log.e("Error, JSONException", ((json != null) ? json.toString() : e.getStackTrace().toString()));
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			if(!success)
			{
				AlertDialog dialog = new AlertDialog.Builder(Login.this)
						//AlertDialog dialog = new AlertDialog.Builder(this)
						.setTitle("Error")
						.setMessage("Error de Conexion con el servidor")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								}).create();
				dialog.show();

			}
			else if(!state )
			{
				AlertDialog dialog = new AlertDialog.Builder(Login.this)
							//AlertDialog dialog = new AlertDialog.Builder(this)
							.setTitle("Error")
							.setMessage("Usuario y/o Contraseña invalidos")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									}).create();
					dialog.show();

			}



		}

	}

	private  JSONObject returnParams(String user, String passw)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JSONObject json = null;
		params.add(new BasicNameValuePair("method", "login"));
		params.add(new BasicNameValuePair("user", user));
		params.add(new BasicNameValuePair("pass", passw));

		JSONParser jsonParser = new JSONParser();
		json = jsonParser.makeHttpRequest(url_login, "GET", params);

		return  json;

	}
}
