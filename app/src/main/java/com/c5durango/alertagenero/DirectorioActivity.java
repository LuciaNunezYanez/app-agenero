package com.c5durango.alertagenero;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertagenero.Clases.AdaptadorDirectorio;
import com.c5durango.alertagenero.Utilidades.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectorioActivity extends AppCompatActivity {

    static String TAG = "Directorio";
    static ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directorio);
        lista = findViewById(R.id.listDirectorio);
        getDirectorio(getApplicationContext(), "genero");
    }

    public static void getDirectorio(final Context context, String filtro){

        StringRequest requestGetDirectorio;
        String URL = Constantes.URL + "/directorio/" + filtro;
        Log.d(TAG, URL);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        requestGetDirectorio = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject object_response = new JSONObject(response);
                    Boolean ok = object_response.getBoolean("ok");
                    if(ok){
                        JSONArray object_directorio = object_response.getJSONArray("directorio");
                        String datos_DB[][] = new String[object_directorio.length()][3];
                        for (int e = 0; e < object_directorio.length(); e ++){
                            try {
                                JSONObject centro =  new JSONObject( object_directorio.getString(e));
                                try{
                                    datos_DB[e][0] = centro.getString("nombre_direct");
                                } catch (Exception p ){
                                    datos_DB[e][0] = "CENTRO DESCONOCIDO.";
                                }
                                try{
                                    String direccion_completa =
                                                centro.getString("calle") +
                                                " #" + centro.getString("numero") +
                                                ", " + centro.getString("colonia") +
                                                "\nC.P. " + centro.getString("cp") +
                                                " " + centro.getString("nombre_localidad") +
                                                ", " + centro.getString("abrev") +
                                                "\nEntre " + centro.getString("entre_calle_1") +
                                                " y " + centro.getString("entre_calle_2");
                                    datos_DB[e][1] = direccion_completa;
                                } catch (Exception p){
                                    datos_DB[e][1] = "Dirección incorrecta.";
                                }
                                try{
                                    datos_DB[e][2] = centro.getString("telefonos");
                                } catch (Exception p){
                                    datos_DB[e][2] = "Teléfono desconocido.";
                                }

                            } catch (Exception i){
                                datos_DB[e][0] = "Centro desconocido.";
                                datos_DB[e][1] = "Dirección incorrecta.";
                                datos_DB[e][2] = "Teléfono incorrecto.";
                            }
                        }
                        lista.setAdapter(new AdaptadorDirectorio(context, datos_DB));
                    }
                }
                catch(JSONException e){
                    Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,  Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                Log.e(TAG,  Utilidades.tipoErrorVolley(error));
                requestQueue.stop();
            }
        });
        requestQueue.add(requestGetDirectorio);
    }

    /*public static HashMap<String, String> createEmployee(String name, String number){
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }*/

}
