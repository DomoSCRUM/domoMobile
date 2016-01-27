package com.jack.domoscrum;

import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

public class Splash extends Activity {
	boolean spActive;      //Bandera de Splash activo
	boolean spPaused;    //Bandera de Splash pausado
	long spTime = 500;  //Tiempo duracion de Splash en milisegundos
	TextView nameComplete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		spPaused = false;
    	spActive = true;
    	Thread splashTimer = new Thread() {
    	     public void run() {
    	          try{ //Bucla de espera
    	               long ms = 0;
    	               while(spActive && ms < spTime){
    	                    sleep(100);
    	               //El temporizador avanza si no se ha pausado
    	                    if(!spPaused)
    	                         ms += 100;
    	               }
    	               //Avanza a la siguiente pantalla
    	               SharedPreferences sharedPref = getSharedPreferences("PASA",
    	           			MODE_PRIVATE);
	    	           	boolean hayUsuario = sharedPref.getBoolean("User", false);
	    	           	if (hayUsuario) {
	    	           		String nombre = sharedPref.getString("nombre", "");
//							Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
//							addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//							addAccountIntent.putExtra(Settings.EXTRA_AUTHORITIES, new String[]{"com.google.xtreme.MainActivity"});
//							startActivity(addAccountIntent);
	    	           		startActivity(new Intent("com.google.xtreme.MainActivity"));
	    					
	    	           		// Nunca modificar algo del Layout antes de establecerlo en pantalla
	    	           		nameComplete = (TextView) findViewById(R.id.textView);
	    	           		nameComplete.setText(nombre);
							finish();
						} else {
	    	           		//setContentView(R.layout.activity_login);
	    	           		startActivity(new Intent ("com.google.xtreme.CLEARSPLASH"));
							finish();
						}
    	              // startActivity(new Intent ("com.google.xreme.CLEARSPLASH"));
    	               //No vuelve a llamarse esta Actividad al pulsar back
    	               //finish();
    	          }
                  catch(Exception e){
                      //Thread exception
					  finish();
                      System.err.println(e.toString());
                 }
			 }
       };
       splashTimer.start();
       setContentView(R.layout.activity_splash);        
       return;
   	
       
       
  }
	protected void onStop() {
		super.onStop();
	}
	protected void onPause() {
		super.onPause();
		spPaused = true;
		finish();
	}
	protected void onResume() {
		super.onResume();
		spPaused = false;
	}
	protected void onDestroy() {
		super.onDestroy();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Quitar Splash si se pulsa cualquier tecla
		super.onKeyDown(keyCode, event);
		spActive = false;
		return true;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
