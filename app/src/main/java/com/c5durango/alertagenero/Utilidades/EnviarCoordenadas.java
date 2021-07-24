package com.c5durango.alertagenero.Utilidades;

import android.content.Context;
import android.util.Log;

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

public class EnviarCoordenadas {

    private static String TAG = "Coordenadas";

    public static Boolean enviarCoordenadas(final Context context, Double latitud, Double longitud, String fecha, int reporteCreado){
        StringRequest requestCoordenadas;
        Boolean seEnviaron;

        if(reporteCreado >=1 ) {
            String URL = Constantes.URL + "/coordenadas/" + reporteCreado;

            final RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject jsonObjectBody = new JSONObject();
            try {
                jsonObjectBody.put("lat_coord_reporte", latitud);
                jsonObjectBody.put("lng_coord_reporte", longitud);
                jsonObjectBody.put("fecha_coord_reporte", fecha);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            final String requestBody = jsonObjectBody.toString();
            requestCoordenadas = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, "La respuesta de las coordenas es: " + response);
                    int valorTrue = response.indexOf(":true,");
                    switch (valorTrue){
                        case -1:
                            Log.d(TAG, "No viene TRUE en la respuesta");
                            int valorFalse = response.indexOf(":false,");
                            switch (valorFalse){
                                case -1:
                                    Log.d(TAG, "No viene TRUE ni FALSE en la respuesta");
                                    break;
                                default:
                                    Log.d(TAG, "Viene FALSE, por lo que no se agregaron coordenadas");
                                    break;
                            }
                            break;
                        default:
                            Log.d(TAG, "Si viene la cadena en: " + valorTrue);
                            break;
                    }
                    // Detener proceso una vez enviadas las coordenadas
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorResp = "Error #3: " + Utilidades.tipoErrorVolley(error);
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
            requestQueue.add(requestCoordenadas);
            seEnviaron = true;
        } else if (reporteCreado == -1){
            seEnviaron = false;
        } else {
            Log.d(TAG, "Se agotó el tiempo de espera para coordenadas!!! ");
            seEnviaron = false;
        }
        return seEnviaron;
    }
}
