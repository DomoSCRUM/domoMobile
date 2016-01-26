package com.jack.domoscrum;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class Menu extends Activity {

	TextView nameComplete;
	TextView fecha;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		nameComplete = (TextView) findViewById(R.id.textView);
		fecha =(TextView) findViewById(R.id.fecha);
		fecha.setText(calcularFecha());
		
		SharedPreferences sharedPref = getSharedPreferences("PASA",
				MODE_PRIVATE);
		boolean hayUsuario = sharedPref.getBoolean("User", false);
		if (hayUsuario) {
			String Nombre = sharedPref.getString("nombre", "");
			
			// Nunca modificar algo del Layout antes de establecerlo en pantalla			
			nameComplete.setText("Bienvenido " + Nombre);
		}
		else
		{
			nameComplete.setText(getIntent().getStringExtra("nameComplete"));
		}
	}
	
	public String calcularFecha()
	{
		Calendar c = Calendar.getInstance();		
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate1 = df1.format(c.getTime());
		return formattedDate1;
	}
	
	public void saldos(View v)
	{
		Intent i = new Intent(getApplicationContext(),Saldos.class);
		startActivity(i);
		
	}
	
	public void salir(View v)
	{
		SharedPreferences sharedPref = getSharedPreferences("PASA",	MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.clear();	//Remueve todos los valores del SharedPreference, 
				//cuando se ejecute commit() solo permaneceran 			//los valores definidos en este Editor
		editor.putBoolean("User", false);//Esta preferencia permanecer
		editor.commit();
		finish();
		startActivity(new Intent ("com.google.xtreme.CLEARSPLASH"));
	}
	
	public void comprar(View v)
	{
		Intent i = new Intent(getApplicationContext(),Comprar.class);
		startActivity(i);
		
	}
	
	public void items(View v)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("Advertencia")
		.setMessage("Esta Opcion no esta Habilitada")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				}).create();
		dialog.show();
		
	}
	
	public void precios(View v)
	{
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("Advertencia")
		.setMessage("Esta Opcion no esta Habilitada")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				}).create();
		dialog.show();
		
	}
	


}
