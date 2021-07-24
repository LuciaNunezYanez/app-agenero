package com.c5durango.alertagenero.Utilidades;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertagenero.Constantes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class EnviarImagenes {
    static String TAG = "Imagenes";

    public static Boolean enviarImagenFrontal(final Context context, String IMAGEN_FRONTAL, String FECHA_FRONTAL, int reporteCreado){

        StringRequest requestFrontal;
        if(reporteCreado >=1 ){
            String archivo = IMAGEN_FRONTAL;
            String URL = Constantes.URL + "/upload/imagenes/" + reporteCreado;

            final RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject jsonObjectBody = new JSONObject();
            try {
                jsonObjectBody.put("fecha", FECHA_FRONTAL);
                jsonObjectBody.put("imagen", archivo);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }

            final String requestBody = jsonObjectBody.toString();
            requestFrontal = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // RECIBIR LA RESPUESTA DEL WEB SERVICE CUANDO TOD ESTA CORRECTO
                    Log.i(TAG, "La respuesta de imagen frontal es: " + response);
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,  "E#4" + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                    requestQueue.stop();
                    Log.e(TAG, error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(requestFrontal);
            return true;
        } else if (reporteCreado == -1){
            return false;
        } else {
            Log.d(TAG, "Se agotó el tiempo de espera frontal!!! ");
            return false;
        }
    }

    public static Boolean enviarImagenTrasera(final Context context, String IMAGEN_TRASERA, String FECHA_TRASERA, int reporteCreado){

        StringRequest requestTrasera;
        if(reporteCreado >=1 ){
            // Cuerpo de la petición
            String archivo = IMAGEN_TRASERA;
            final String URL = Constantes.URL + "/upload/imagenes/" + reporteCreado;

            final RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject jsonObjectBody = new JSONObject();
            try {
                jsonObjectBody.put("fecha", FECHA_TRASERA);
                jsonObjectBody.put("imagen", archivo);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }

            final String requestBody = jsonObjectBody.toString();
            requestTrasera = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, "La respuesta de imagen trasera es: " + response);
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorResp = "Error #5: " + Utilidades.tipoErrorVolley(error);
                    Toast.makeText(context, errorResp, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorResp);
                    requestQueue.stop();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(requestTrasera);
            return true;
        } else if (reporteCreado == -1){
            return false;
        } else {
            Log.d(TAG, "Se agotó el tiempo de espera trasera!!! ");
            return false;
        }
    }
}
