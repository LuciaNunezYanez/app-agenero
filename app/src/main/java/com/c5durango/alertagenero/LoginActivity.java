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

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegistrar, btnOlvide;
    EditText txtCorreo, txtContrasena;
    static String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnIniciarSesion);
        btnRegistrar = findViewById(R.id.btnRegistrarme);
        btnOlvide = findViewById(R.id.btnOlvideContrasena);
        txtContrasena = findViewById(R.id.txtContrasenaLogin);
        txtCorreo = findViewById(R.id.txtCorreoLogin);

        btnOlvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RecuperacionActivity.class));
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                intent.putExtra("id",0);
                intent.putExtra("accion", "nuevo");
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtCorreo.getText().toString().length() > 1 && txtContrasena.getText().toString().length() > 1)
                    iniciarSesion(txtCorreo.getText().toString(), txtContrasena.getText().toString());
                else
                    Toast.makeText(getApplicationContext(), "¡Ingrese correo y contraseña!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void iniciarSesion(final String correo, final String contrasena){
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String URL = Constantes.URL + "/login/app/";

        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("c", correo);
            jsonObjectBody.put("ct", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        final String requestBody = jsonObjectBody.toString();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(), response.getString("resp"), Toast.LENGTH_LONG).show();
                            if(response.getBoolean("ok")){
                                // Pantalla para modificar datos
                                Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                                intent.putExtra("id", response.getInt("id"));
                                intent.putExtra("correo", correo);
                                intent.putExtra("contrasena", contrasena);
                                intent.putExtra("accion", "login");
                                startActivity(intent);
                            }
                        } catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, error.getMessage());
                        Toast.makeText(getApplicationContext(),  Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
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
    }
}