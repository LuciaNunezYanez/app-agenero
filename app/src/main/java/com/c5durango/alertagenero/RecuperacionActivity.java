package com.c5durango.alertagenero;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.c5durango.alertagenero.Utilidades.Utilidades;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RecuperacionActivity extends AppCompatActivity {

    Button btnGenerar, btnValidar;
    EditText txtCorreo, txtCodigo;
    static String TAG = "Recuperacion";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion);
        txtCorreo = findViewById(R.id.txtCorreoRecuperacion);
        txtCodigo = findViewById(R.id.txtCodigoRecuperacion);
        btnGenerar = findViewById(R.id.btnGenerarCodigo);
        btnValidar = findViewById(R.id.btnRecuperar);

        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utilidades.validEmail(txtCorreo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "¡El correo electrónico es inválido!", Toast.LENGTH_SHORT).show();
                } else
                    generarCodigo(txtCorreo.getText().toString());
            }
        });
        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utilidades.validEmail(txtCorreo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "¡El correo electrónico es inválido!", Toast.LENGTH_SHORT).show();
                } else if ( txtCodigo.getText().toString().length() < 7 ){
                    Toast.makeText(getApplicationContext(), "¡El código de recuperación debe contener 8 dígitos!", Toast.LENGTH_SHORT).show();
                } else {
                    validarCodigo(txtCorreo.getText().toString(), txtCodigo.getText().toString());
                }
            }
        });

    }

    public void generarCodigo(String correo){
        if (correo.length() > 1) {
            String URL = Constantes.URL + "/recuperar/";
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonObjectBody = new JSONObject();
            try {
                jsonObjectBody.put("correo", correo);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
            final String requestBody = jsonObjectBody.toString();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(getApplicationContext(), response.getString("resp"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            requestQueue.stop();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "E#13 " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(getRequest);
        } else {
            Toast.makeText(getApplicationContext(), "¡El correo electrónico es inválido!", Toast.LENGTH_SHORT).show();
        }
    }

    public void validarCodigo(final String correo, String codigo){
        if (correo.length() > 1) {
            String URL = Constantes.URL + "/recuperar/validar";
            final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonObjectBody = new JSONObject();
            try {
                jsonObjectBody.put("correo", correo);
                jsonObjectBody.put("codigo", codigo);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
            final String requestBody = jsonObjectBody.toString();
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                Boolean ok = response.getBoolean("ok");
                                Toast.makeText(getApplicationContext(),  response.getString("resp"), Toast.LENGTH_SHORT).show();
                                if(ok){
                                    Intent intent = new Intent(getApplicationContext(), NuevaContrasenaActivity.class);
                                    intent.putExtra("correo", correo);
                                    startActivity(intent);
                                    RecuperacionActivity.this.finish();
                                }
                            }
                            catch(JSONException e){
                                Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                            requestQueue.stop();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "E#12 " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };
            requestQueue.add(getRequest);
        } else {
            Toast.makeText(getApplicationContext(), "¡El correo electrónico es inválido!", Toast.LENGTH_SHORT).show();
        }
    }
}