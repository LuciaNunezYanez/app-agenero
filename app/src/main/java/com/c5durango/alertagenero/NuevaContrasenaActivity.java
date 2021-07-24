package com.c5durango.alertagenero;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
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

public class NuevaContrasenaActivity extends AppCompatActivity {

    EditText txtCorreo, txtPass1, txtPass2;
    Button btnCambiar;
    static String TAG = "NuevaContrasena";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_contrasena);
        txtCorreo = findViewById(R.id.txtCorreoCambiarC);
        txtPass1 = findViewById(R.id.txtNuevaContr);
        txtPass2 = findViewById(R.id.txtConfirmarCont);
        btnCambiar = findViewById(R.id.btnCambiarCont);

        txtCorreo.setText(getIntent().getExtras().get("correo").toString());

        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtPass1.getText().toString().length() < 8 || txtPass2.getText().toString().length() < 8){
                    Toast.makeText(getApplicationContext(), "¡Las contraseñas deben tener al menos 8 carácteres!", Toast.LENGTH_LONG).show();
                } else if (!txtPass1.getText().toString().equals(txtPass2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "¡Las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
                } else {
                    cambiarContrasena(txtCorreo.getText().toString(), txtPass1.getText().toString());
                }
            }
        });
    }

    public void cambiarContrasena(final String correo, final String contrasena){
        StringRequest requestCambiarContr;
        String URL = Constantes.URL + "/recuperar/cc";

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contrasena",contrasena);
            jsonObject.put("correo",correo);
        }catch (JSONException e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        final String requestBody = jsonObject.toString();
        requestCambiarContr = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    Boolean ok = obj.getBoolean("ok");
                    Toast.makeText(getApplicationContext(), obj.getString("resp"), Toast.LENGTH_SHORT).show();
                    if(ok){
                        // Pantalla para modificar datos
                        Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                        intent.putExtra("id", obj.getInt("id"));
                        intent.putExtra("correo", correo);
                        intent.putExtra("accion", "recuperacion");
                        intent.putExtra("contrasena", contrasena);
                        startActivity(intent);
                        NuevaContrasenaActivity.this.finish();
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "E#14 Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                }

                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "E#14 " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
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
        requestQueue.add(requestCambiarContr);
    };
}