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
    private static final int LIGHT_TWO= 2;
    private static final int LIGHT_THREE = 3;
    private static final int TEMPERATURE = 4;
    private boolean TURN_ON_LIGHT_ONE=false;
    private boolean TURN_ON_LIGHT_TWO=true;
    private boolean TURN_ON_LIGHT_THREE=false;
    private String IP = "10.0.2.2";
    private String url_login = "http://" + IP+ "/xtreme/login.php";
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
        url_login = "http://" + IP+ "/xtreme/login.php";

        //verifica el estado de la luces y temperatura al iniciar la actividad
        new DeviceAction(true).execute();
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

    public void turnOnOffOne(View v)
    {
        Device device= new Device();
        device.setId(findViewById(R.id.light1).getId());
        device.setName("Light1");

        new DeviceAction(device).execute();


    }

    public void turnOnOffTwo(View v)
    {
        Device device= new Device();
        device.setId(findViewById(R.id.light2).getId());
        device.setName("Ligh2");
        new DeviceAction(device).execute();
        //new DeviceAction(LIGHT_TWO).execute();


    }

    public void turnOnOffThree(View v)
    {
        Device device= new Device();
        device.setId(findViewById(R.id.light3).getId());
        device.setName("Light3");
        new DeviceAction(device).execute();
        //new DeviceAction(LIGHT_THREE).execute();


    }

    public void knowTemp(View v)
    {
        Device device= new Device();
        device.setId(findViewById(R.id.temp).getId());
        device.setName("Temperature");
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
            pDialog.setMessage("Consultando estado.");
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


                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("deviceName", device.getName()));
                if(!device.isLight())
                    params.add(new BasicNameValuePair("action", ""));
                else
                    params.add(new BasicNameValuePair("action", String.valueOf(device.isState())));

                // getting JSON Object
                // Note that create Empleado url accepts POST method, Comentar para pruebas
                ////JSONParser jsonParser = new JSONParser();
                //JSONObject json = jsonParser.makeHttpRequest(url_login,"POST", params);

                // check log cat fro response, comentar para pruebasdnf
                //Log.d("Create Response", ((json !=null) ? json.toString():"Error"));
                // check for success tag
                //Descomentar para Pruebas, comentar para produccion
                //JSONObject json = new JSONObject();
                json = new JSONObject();

                try {
                    if (json != null) {
                        //int success = json.getInt(TAG_SUCCESS);
                        success = 1;

                        //String nombre = json.getString(TAG_NOMBRE);
                        //String apellido = json.getString(TAG_APELLIDO);

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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("deviceNameAll", device.getName()));
                JSONParser jsonParser = new JSONParser();
                //JSONObject json = jsonParser.makeHttpRequest(url_login,"POST", params);
               // json = jsonParser.makeHttpRequest(url_login,"POST",null);
                json = new JSONObject();
                try {
                    if (json != null) {
                        success = 1;

                        /*TURN_ON_LIGHT_ONE=json.getBoolean(LIGHT_ONE + "");
                        TURN_ON_LIGHT_TWO=json.getBoolean(LIGHT_TWO+"");
                        TURN_ON_LIGHT_THREE=json.getBoolean(LIGHT_THREE+"");*/

                        //Para pruebas
                        TURN_ON_LIGHT_ONE=!device.isState();
                        TURN_ON_LIGHT_TWO=!device.isState();;
                        TURN_ON_LIGHT_THREE=!device.isState();;



                    }
                }
                catch (Exception e)
                {

                }
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
            else {

                ImageButton deviceOne;
                ImageButton deviceTwo;
                ImageButton deviceThree;
                deviceOne = (ImageButton) findViewById(R.id.light1);
                deviceTwo = (ImageButton) findViewById(R.id.light2);
                deviceThree = (ImageButton) findViewById(R.id.light3);

                if (TURN_ON_LIGHT_ONE)
                    deviceOne.setBackgroundResource(R.drawable.balas_predator);
                if (TURN_ON_LIGHT_TWO)
                    deviceTwo.setBackgroundResource(R.drawable.balas_predator);
                if (TURN_ON_LIGHT_THREE)
                    deviceThree.setBackgroundResource(R.drawable.balas_impact);

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
