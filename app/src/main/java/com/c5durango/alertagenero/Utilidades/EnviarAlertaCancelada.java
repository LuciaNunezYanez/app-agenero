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

public class EnviarAlertaCancelada {
    static String TAG = "AlertaCancelada";

    public static void enviarAlertaCancelada(final Context context, int id_reporte_cancelar, int nuevo_estatus){
        StringRequest requestAlertaCancelada;
        String URL = Constantes.URL + "/activaciones/" + id_reporte_cancelar;

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("estatus", nuevo_estatus);
        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        final String requestBody = jsonObject.toString();
        requestAlertaCancelada = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if( json.getBoolean("ok"))
                        Toast.makeText(context, "¡Reporte cancelado con éxito!", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, "¡El reporte no pudo ser cancelado", Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    Toast.makeText(context, "¡Ocurrió un error al cancelar el reporte", Toast.LENGTH_LONG).show();
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error #9: " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error #9: " + Utilidades.tipoErrorVolley(error));
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
        requestQueue.add(requestAlertaCancelada);
        //return seCancelo;
    }
}
