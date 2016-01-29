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
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView nameComplete;
    private TextView temperature;
    private ProgressDialog pDialog;
    private boolean success=false;
    private String IP = "10.0.2.2";
    private String url_login = "http://" + IP+ ":8080/OjosTest/Peticion";//http://190.6.160.42:8080/OjosTest/Peticion
    private JSONObject json=null;
    private Device deviceLight=null;
    private Device deviceTemp=null;
    private String TAG_STATE= "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameComplete = (TextView) findViewById(R.id.textView);

        temperature = (TextView) findViewById(R.id.temp);

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
        url_login = "http://" + IP+ ":8080/OjosTest/Peticion";
        beginDevice();


    }

    private void beginDevice() {
        deviceLight= new Device();
        deviceLight.setId(findViewById(R.id.light).getId());
        deviceLight.setName("light");
        deviceTemp= new Device();
        deviceTemp.setId(findViewById(R.id.temp).getId());
        deviceTemp.setName("temp");
        deviceTemp.setIsLight(false);
    }


    public String calcularFecha()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.US);
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
        editor.apply();
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

        new DeviceAction(deviceLight).execute();
    }

    public void knowTemp(View v)
    {

        new DeviceAction(deviceTemp).execute();

        //new DeviceAction(TEMPERATURE).execute();
    }

    /**
     * Background Async Task to turn on/off Light
     * */
    class DeviceAction extends AsyncTask<String, String, String>
    {

        private boolean verify= false;
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
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Creating Empleado
         * */
        protected String doInBackground(String... args) {

            //Enciende, apaga o cunsulta la temperatura
            if (!verify)
            {
                json=returnParams(device);
                //json = new JSONObject();

                try {
                    if (json != null) {
                        //int success = json.getInt(TAG_SUCCESS);
                        success = true;
                        int state = json.getInt(TAG_STATE);
                        if(state == 0)
                            device.setState(false);
                        else
                            device.setState(true);
                        //device.setState(!device.isState());
                        //device.setState(!device.isState());


                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.e("Error", "JSONException", e);
                }
            }
            else
            {
                success = true;
                json= returnParams(device);
                try
                {
                    String result=json.getString(TAG_STATE);
                    String values[] = result.split("_");
                    for(int j=0;j< values.length;j++)
                    {
                        if(j==0){
                            int state = Integer.parseInt(values[j]);
                            if(state == 0)
                                deviceLight.setState(false);
                            else
                                deviceLight.setState(true);
                        }
                        else
                        {
                            deviceTemp.setValue(values[j].replace("_","."));
                        }
                    }




                }catch (JSONException e)
                {

                    Log.e("JSON Parser", "Error parsing data " ,e);


                }


                //json = new JSONObject();

            }

            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(success)
            {
                if(verify) {

                    ImageButton deviceOne;
                    deviceOne = (ImageButton) findViewById(R.id.light);

                    if (!deviceLight.isState())
                        deviceOne.setBackgroundResource(R.drawable.balas_predator);
                    else
                        deviceOne.setBackgroundResource(R.drawable.light_on);

                    temperature.setText(deviceTemp.getValue() +" "+ calcularFecha());
                    //para pruebas
                    //temperature.setText("25°C " + calcularFecha());


                }
                else
                {
                    if(device.isLight())
                    {
                        ImageButton deviceOne;
                        deviceOne = (ImageButton) findViewById(R.id.light);
                        if (device.isState())
                            deviceOne.setBackgroundResource(R.drawable.light_on);
                        else
                            deviceOne.setBackgroundResource(R.drawable.balas_predator);
                    }
                    else
                    {
                        temperature.setText(deviceTemp.getValue() +" "+ calcularFecha());
                        //para pruebas
                        //temperature.setText("28°C "+calcularFecha());

                    }
                }
            }
            else
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
                params.add(new BasicNameValuePair("value", (device.isState() ? "0" : "1")));

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
