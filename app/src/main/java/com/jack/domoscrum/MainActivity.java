package com.jack.domoscrum;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    private TextView nameComplete;
    private TextView fecha;
    private TextView temperature;
    private ProgressDialog pDialog;
    private int success=-1;
    private static final int LIGHT_ONE = 1;
    private static final int TEMPERATURE = 4;
    private boolean TURN_ON_LIGHT_ONE=false;
    private String IP = "10.0.2.2";
    private String url_login = "http://" + IP+ ":8080/domoScrum";
    private JSONObject json=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameComplete = (TextView) findViewById(R.id.textView);
        fecha =(TextView) findViewById(R.id.fecha);
        temperature = (TextView) findViewById(R.id.temp);
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
        //setea la direccion del shareprefrence
        IP = sharedPref.getString("ip", IP);
        url_login = "http://" + IP+ ":8080/domoScrum";


    }




    public String calcularFecha()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate1 = df1.format(c.getTime());
        return formattedDate1;
    }

    public void salir(View v)
    {
        SharedPreferences sharedPref = getSharedPreferences("PASA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();	//Remueve todos los valores del SharedPreference,
        //cuando se ejecute commit() solo permaneceran 			//los valores definidos en este Editor
        editor.putBoolean("User", false);//Esta preferencia permanecer
        editor.commit();
        finish();
        startActivity(new Intent("com.google.xtreme.CLEARSPLASH"));
    }

    public void update(View v)
    {
        //verifica el estado de la luces y temperatura al iniciar la actividad
        new DeviceAction(true).execute(); //como Un Boton
    }

    public void turnOnOff(View v)
    {
        Device device= new Device();
        device.setId(findViewById(R.id.light).getId());
        device.setName("light");
        new DeviceAction(device).execute();


    }

    public void knowTemp(View v)
    {
        Device device= new Device();
        device.setId(findViewById(R.id.temp).getId());
        device.setName("temp");
        device.setIsLight(false);
        new DeviceAction(device).execute();

        //new DeviceAction(TEMPERATURE).execute();
    }

    /**
     * Background Async Task to turn on/off Light
     * */
    class DeviceAction extends AsyncTask<String, String, String>
    {

        private boolean verify= true;
        private Device device=null;

        public DeviceAction(boolean verify) {
            this.verify=verify;

        }

        public DeviceAction(Device device) {
            this.device=device;


        }


        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Consultando estado");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating Empleado
         * */
        protected String doInBackground(String... args) {

            //Enciende, apaga o cunsulta la temperatura
            if (!verify)
            {
                //json=returnParams(device);
                json = new JSONObject();

                try {
                    if (json != null) {
                        //int success = json.getInt(TAG_SUCCESS);
                        success = 1;
                        //String nombre = json.getString(TAG_NOMBRE);


                    } else {
                        json.getString("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error, JSONException", ((json != null) ? json.toString() : e.getStackTrace().toString()));
                }

            }
            else
            {
                success = 1;
                json= returnParams(device);


            }

            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(success == -1)
            {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
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
            else
            {

                ImageButton deviceOne;
                deviceOne = (ImageButton) findViewById(R.id.light);
                if (!TURN_ON_LIGHT_ONE)
                    deviceOne.setBackgroundResource(R.drawable.balas_predator);
                else
                    deviceOne.setBackgroundResource(R.drawable.light_on);


                try {

                    //temperature.setText(json.getString("temperature"));
                    //para pruebas
                    temperature.setText("25°C");
                }
                //catch (JSONException e)
                catch (Exception e) {
                    temperature.setText("N/A");
                }
            }





        }

    }

    private  JSONObject returnParams(Device device)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = null;
        if(device!=null)
        {
            params.add(new BasicNameValuePair("method", device.getName()));
            if (device.isLight())
                params.add(new BasicNameValuePair("value", (device.isState() ? "1" : "0")));

        }
        else
        {
            params.add(new BasicNameValuePair("method", "states"));

        }
        JSONParser jsonParser = new JSONParser();
        json = jsonParser.makeHttpRequest(url_login, "GET", params);

        return  json;




    }

    class Device
    {
        private boolean state=false;
        private String name;
        private String value="0";
        private boolean isLight= true;
        private int id;


        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isLight() {
            return isLight;
        }

        public void setIsLight(boolean isLight) {
            this.isLight = isLight;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }



}
